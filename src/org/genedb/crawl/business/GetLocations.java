package org.genedb.crawl.business;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.genedb.crawl.model.Locations;
import org.genedb.crawl.model.LocationRegion;

public class GetLocations {
	
	private Logger logger = Logger.getLogger(GetLocations.class);
	
	private AnnotationRepository repo;
	private final Set<String> types = new HashSet<String>(Arrays.asList(new String[] {"gene", "pseudogene"}));
	
	public void setRepo(AnnotationRepository repo) {
		this.repo = repo;
	}
	
	private class Bounds {
		int min = Integer.MAX_VALUE;
		int max = 0;
	}
	
	/*
	 * 
	 * 
	 *  (fl.fmin BETWEEN %(start)s AND %(end)s ) 
	 *  OR (fl.fmax BETWEEN %(start)s AND %(end)s )
	 *  OR ( fl.fmin <= %(start)s AND fl.fmax >= %(end)s )
	 *  
	 *   
	*/
	private BooleanQuery isOverlap(int start, int end) {
		
		BooleanQuery spansBothSides = new BooleanQuery();
		
		// numeric_start <= start && numeric_end >= end  
		NumericRangeQuery<Integer> startLowerThanRequested =  NumericRangeQuery.newIntRange("numeric_start", null, start, true, true);
		NumericRangeQuery<Integer> endHigherThanRequested =  NumericRangeQuery.newIntRange("numeric_end", end, null, true, true);
		
		// both of these must be true for it to span both sides
		spansBothSides.add(startLowerThanRequested, Occur.MUST);
		spansBothSides.add(endHigherThanRequested, Occur.MUST);
		
		BooleanQuery isInsideRange = new BooleanQuery();
		
		// numeric_start >= start && numeric_end <= end
		NumericRangeQuery<Integer> startQuery =  NumericRangeQuery.newIntRange("numeric_start", start, end, true, true);
		NumericRangeQuery<Integer> endQuery =  NumericRangeQuery.newIntRange("numeric_end", start, end, true, true);
		
		isInsideRange.add(startQuery, Occur.SHOULD);
		isInsideRange.add(endQuery, Occur.SHOULD);
		
		BooleanQuery isOverlap = new BooleanQuery();
		isOverlap.add(spansBothSides, Occur.SHOULD);
		isOverlap.add(isInsideRange, Occur.SHOULD);
		
		return isOverlap;
	}
	
	private Bounds getFeaturesOfTypeOverlappingWithBoundariesWithLucene(IndexReader reader,  Set<String> types, String region, int start, int end) throws IOException {
		Bounds b = new Bounds();
		
		BooleanQuery query = new BooleanQuery();
		
		// only pull back features in the region
		TermQuery isInRegion = new TermQuery(new Term("seqid", region));
		
		// only pull back features of a certain type
		BooleanQuery typeQuery = new BooleanQuery();
		for (String type : types) {
			typeQuery.add(new TermQuery(new Term("type", type)), Occur.SHOULD);
		}
		
		BooleanQuery isOverlap = isOverlap(start, end);
		
		query.add(isInRegion, Occur.MUST);
		query.add(typeQuery, Occur.MUST);
		query.add(isOverlap, Occur.MUST);
		
		//logger.info(query.toString());
		
		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs td = searcher.search(query, reader.maxDoc());
		
		for (ScoreDoc sd : td.scoreDocs) {
			
			Document d = reader.document(sd.doc);
			
			//logger.info(d.getField("ID").stringValue() + d.getField("start").stringValue() + " ... " + d.getField("end").stringValue());
			
			int doc_start = Integer.parseInt(d.getField("start").stringValue());
			int doc_end = Integer.parseInt(d.getField("end").stringValue());
			
			if (doc_start < b.min) {
				b.min = doc_start;
			}
			
			if (doc_end > b.max) {
				b.max = doc_end;
			}
			
		}
		
		//logger.info(b.min + " " + b.max);
		
		return b;
	}
	
	public Locations queryWithLucene(String region, int start, int end) throws IOException {
		
		Locations locations = new Locations();
		locations.region = region;
		locations.request_start = start;
		locations.request_end = end;
		//locations.name = "regions/locations";
		locations.actual_start = start;
		locations.actual_end = end;
		
		IndexReader reader = repo.luceneIndexReader();
		IndexSearcher searcher = new IndexSearcher(reader);
		
		Bounds bounds = getFeaturesOfTypeOverlappingWithBoundariesWithLucene(reader, types, region, start, end);
		if (bounds.min < locations.actual_start) {
			locations.actual_start = bounds.min;
		}
		if (bounds.max > locations.actual_end) {
			locations.actual_end = bounds.max;
		}
		
		BooleanQuery query = new BooleanQuery();
		
		TermQuery isInRegion = new TermQuery(new Term("seqid", region));
		
		BooleanQuery isOverlap = isOverlap(locations.actual_start, locations.actual_end);
		
		query.add(isOverlap, Occur.MUST);
		query.add(isInRegion, Occur.MUST);
		
		
		logger.info("Query : \t" + query.toString());
		
		TopDocs td = searcher.search(query, reader.maxDoc());
		
		for (ScoreDoc sd : td.scoreDocs) {
			
			Document d = reader.document(sd.doc);
			
			LocationRegion lr = new LocationRegion();
			
			lr.start = d.getField("start").stringValue();
			lr.end = d.getField("end").stringValue();
			
			lr.phase = d.getField("phase").stringValue();
			lr.is_obsolete = d.getField("isObsolete").stringValue();
			lr.feature = d.getField("ID").stringValue();
			lr.strand = d.getField("strand").stringValue();
			lr.type = d.getField("type").stringValue();
			
			locations.features.add(lr);
			
		}
		
		return locations;
	}
	
	public Locations query(String region, int start, int end) throws IOException {
		
		Locations locations = new Locations();
		locations.region = region;
		locations.request_start = start;
		locations.request_end = end;
		//locations.name = "regions/locations";
		locations.actual_start = start;
		locations.actual_end = end;
		
		TabixReaderInfo readerInfo = repo.getTabixReaderInfo(region);
		TabixReader reader = readerInfo.getReader();
		
		
		
		Bounds bounds = getFeaturesOfTypeOverlappingWithBoundaries(reader, types, region, start, end);
		if (bounds.min < locations.actual_start) {
			locations.actual_start = bounds.min;
		}
		if (bounds.max > locations.actual_end) {
			locations.actual_end = bounds.max;
		}
		
		String queryString = String.format("%s:%s-%s", region, locations.actual_start, locations.actual_end);
		
		logger.info("Query : \t" + queryString);
		
		TabixReader.Iterator iter = reader.query(queryString);
		
		String line;
		while((line=iter.next()) != null) {
			
			GFFFeature feature = new GFFFeature(line);
			
			LocationRegion lr = new LocationRegion();
			lr.start = Integer.toString(feature.start);
			lr.end = Integer.toString(feature.end);
			lr.phase = feature.phase;
			lr.is_obsolete = (String) feature.attributes.map.get("isObsolete");
			lr.strand = feature.strand.getStrand();
			lr.type = feature.type;
			
			lr.feature = (String) feature.attributes.map.get("ID");
			
			
			locations.features.add(lr);
			
			
		}
		
		return locations;
	}
	
	private Bounds getFeaturesOfTypeOverlappingWithBoundaries(TabixReader reader, Set<String> types, String region, int start, int end) throws IOException {
		
		TabixReader.Iterator iter = reader.query(region);
		
		Bounds b = new Bounds();
		
		String line;
		while((line=iter.next()) != null) {
			GFFFeature feature = new GFFFeature(line);
			
			if (! types.contains(feature.type)) {
				continue;
			}
			
//			if (feature.attributes.map.get("ID").equals("PFA0525w")) {
//				logger.info(start + " ... " + end);
//				logger.info(feature.attributes.map.get("ID").toString() + "\t" + feature.start + "\t" + feature.end);
//			}
			
			if ((feature.start < start) && (end < feature.end)) {
				
				//logger.info(feature.attributes.map.get("ID").toString() + "!!!!!!!!!!!!");
				
				if (feature.start < b.min) {
					b.min = feature.start;
				}
				
				if (feature.end > b.max) {
					b.max = feature.end;
				}
			}
			
			
		}
		
		return b;
		
	}
	
	
}
