package org.genedb.crawl.elasticsearch.index.gff;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.genedb.crawl.elasticsearch.index.gff.GFFFeature.GFFAttributeMap;
import org.genedb.crawl.elasticsearch.index.gff.GFFFeature.GFFAttributeMapList;
import org.genedb.crawl.model.Coordinates;
import org.genedb.crawl.model.Cv;
import org.genedb.crawl.model.Cvterm;
import org.genedb.crawl.model.Dbxref;
import org.genedb.crawl.model.ElasticSequence;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.FeatureProperty;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.Orthologue;
import org.genedb.crawl.model.Pub;

public class GFFFileToFeatureListConverter {
	
	private Logger logger = Logger.getLogger(GFFFileToFeatureListConverter.class);
	
	private List<Feature> features = new ArrayList<Feature>();
	
	private List<ElasticSequence> sequences = new ArrayList<ElasticSequence>();
	
	private Organism organism;
	
	public List<Feature> getFeatures() {
		return features;
	}
	
	public List<ElasticSequence> getSequences() {
		return sequences;
	}
	
	public GFFFileToFeatureListConverter (Organism organism) {
		this.organism = organism;
	}
	
	
	
	public void parse(GFFAnnotatationAndFastaExtractor extractor) throws ParseException  {
		
//		for (ElasticSequence sequence : extractor.getSequences()) {
//			sequence.organism_id = organism.ID;
//			sequences.add(sequence);
//		}
//		
//		for (String line : extractor.getAnnotations()) {
//			
//			
//		}
		
	}
	
}
