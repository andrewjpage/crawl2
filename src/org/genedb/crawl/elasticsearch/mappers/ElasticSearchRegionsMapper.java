package org.genedb.crawl.elasticsearch.mappers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SortOrder;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.action.search.SearchRequestBuilder;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.xcontent.BoolQueryBuilder;
import org.elasticsearch.index.query.xcontent.FieldQueryBuilder;
import org.elasticsearch.index.query.xcontent.QueryBuilders;
import org.elasticsearch.index.query.xcontent.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilders;
import org.genedb.crawl.model.Coordinates;
import org.genedb.crawl.model.ElasticSequence;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.LocationBoundaries;
import org.genedb.crawl.model.Organism;
import org.gmod.cat.RegionsMapper;
import org.springframework.stereotype.Component;

@Component
public class ElasticSearchRegionsMapper extends ElasticSearchBaseMapper implements RegionsMapper {

	private Logger logger = Logger.getLogger(ElasticSearchRegionsMapper.class);
	
	private int getTotalInRegion(String region) {
		FieldQueryBuilder regionQuery = QueryBuilders.fieldQuery("region", region);
		
		CountResponse cr = connection.getClient()
		 	.prepareCount(index)
		 	.setQuery(regionQuery)
		 	.execute()
	        .actionGet();
		
		long count = cr.count();
		
		logger.debug(String.format("Count in %s : %s", region, count));
		
		return (int) count;
	}
	
	
	private BoolQueryBuilder isOverlap(String region, int start, int end) {
		
		RangeQueryBuilder startLowerThanRequested = 
			QueryBuilders.rangeQuery("fmin")
				.lte(start);
		
		RangeQueryBuilder endHigherThanRequested = 
			QueryBuilders.rangeQuery("fmax")
				.gte(end);
		
		// (fmin <= start) && (end <= fmax)
		BoolQueryBuilder spansBothSides = 
			QueryBuilders.boolQuery()
				.must(startLowerThanRequested)
				.must(endHigherThanRequested);
		
		
		RangeQueryBuilder startInRange = 
			QueryBuilders.rangeQuery("fmin")
				.from(start)
				.to(end);
		
		RangeQueryBuilder endInRange = 
			QueryBuilders.rangeQuery("fmax")
				.from(start)
				.to(end);
		
		// (start <= fmin <= end) || (start <= fmax <= end) 
		BoolQueryBuilder isInsideRange = 
			QueryBuilders.boolQuery()
				.should(startInRange)
				.should(endInRange);
		
		
		
		BoolQueryBuilder isOverlap = 
			QueryBuilders.boolQuery()
				.should(spansBothSides)
				.should(isInsideRange);
		
		
		FieldQueryBuilder regionQuery = 
			QueryBuilders.fieldQuery("region", region);
		
		BoolQueryBuilder isOverlapOnRegion =
			QueryBuilders.boolQuery()
			.must(isOverlap)
			.must(regionQuery);
		
		
		
		return isOverlapOnRegion;
	}
	
	public static String toString(ToXContent tmp) {
	       try {
	           return
	           tmp.toXContent(JsonXContent.unCachedContentBuilder(),
	        		   ToXContent.EMPTY_PARAMS).
	                   prettyPrint().
	                   string();
	       } catch (Exception ex) {
	           return "<ERROR:" + ex.getMessage() + ">";
	       }
	   }
	
	@Override
	public LocationBoundaries locationsMinAndMaxBoundaries(String region,
			int start, int end, List<Integer> types) {
		
		BoolQueryBuilder isOverlap = isOverlap(region, start, end);
		
		SearchRequestBuilder builder = connection.getClient().prepareSearch(index);
		
		
		SearchResponse response = builder
			.setQuery(isOverlap)
			.setExplain(true)
			.setSize(getTotalInRegion(region))
			.execute()
			.actionGet();
	
		logger.info(toString(builder.internalBuilder()));
		
		LocationBoundaries lb = new LocationBoundaries();
		lb.start = start;
		lb.end = end;
		
		for (SearchHit hit : response.getHits()) {
			
			String source = hit.sourceAsString();
			
			//logger.debug(source);
			
			Feature feature = this.getFeatureFromJson(source);
			
			if (feature != null) {
				
				for (Coordinates co : feature.coordinates) {
					if (co.region.equals(region)) {
						
						if (co.fmin < lb.start) {
							lb.start = co.fmin;
						} 
						
						if (co.fmax > lb.end) {
							lb.end = co.fmax;
						} 
							
						break;
					}
				}
				
				
			}
		}
		
		
		logger.debug(String.format("Actual start: %s. Actual end %s", lb.start, lb.end));
		
		return lb;
		
	}

	@Override
	public List<LocatedFeature> locations(String region, int start, int end,
			List<String> exclude) {
		
		BoolQueryBuilder isOverlap = isOverlap(region, start, end);
		
		SearchRequestBuilder builder = connection.getClient()
			.prepareSearch(index)
			.addSort(SortBuilders.fieldSort("fmin"))
			.addSort(SortBuilders.fieldSort("fmax"));
		
		SearchResponse response = builder
			.setQuery(isOverlap)
			.setExplain(true)
			.setSize(getTotalInRegion(region))
			.execute()
			.actionGet();
		
		logger.info(toString(builder.internalBuilder()));
		
		
		List<LocatedFeature> features = new ArrayList<LocatedFeature>();
		
		String[] fieldNames = new String[] {"uniqueName", "fmin", "fmax", "isObsolete", "parent", "phase", "type", "strand"};
		
		for (SearchHit hit : response.getHits()) {
		
			String source = hit.sourceAsString();
			//logger.debug(source);
			
			LocatedFeature feature = this.getFeatureFromJson(source);
			if (feature != null) {
				for (Coordinates co : feature.coordinates) {
					if (co.region.equals(region)) {
						
						try {
							features.add(copy(feature, fieldNames, LocatedFeature.class));
						} catch (InstantiationException e) {
							logger.error(e);
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							logger.error(e);
							e.printStackTrace();
						}
						break;
					}
				}
			}
		}
		
		return features;
		
	}

	@Override
	public String sequence(String region) {
		
		String json = connection.getClient().prepareGet("sequences", "Sequence", region).execute().actionGet().sourceAsString();
		
		try {
			ElasticSequence sequence = (ElasticSequence) jsonIzer.fromJson(json, ElasticSequence.class);
			
			return sequence.sequence;
			
		} catch (Exception e) {
			logger.error("Could not find a sequence for " + region);
			e.printStackTrace();
		} 
		
		return "";
	}

	@Override
	public List<String> inorganism(int organismid) {
		
		logger.debug(String.format("%s %s %s", "sequences", "organism_id", String.valueOf(organismid)));
		
		List<String> regions = new ArrayList<String>();
		
		List<ElasticSequence> sequences = getAllMatches("sequences", "organism_id", String.valueOf(organismid), ElasticSequence.class);
		
		logger.debug(sequences);
		
		for (ElasticSequence sequence : sequences) {
			regions.add(sequence.name);
		}
		
		return regions;

	}

}
