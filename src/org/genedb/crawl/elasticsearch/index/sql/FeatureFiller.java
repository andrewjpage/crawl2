package org.genedb.crawl.elasticsearch.index.sql;

import java.util.List;

import org.apache.log4j.Logger;
import org.genedb.crawl.model.Feature;
import org.gmod.cat.FeatureMapper;

public class FeatureFiller {
	
	private Logger logger = Logger.getLogger(FeatureFiller.class);
	
	
	List<Feature> features;
	
	FeatureMapper featureMapper;
	
	public FeatureFiller(FeatureMapper featureMapper, List<Feature> features) {
		
		this.featureMapper = featureMapper;
		this.features = features;
	}
	
	public void fill() {
		
		logger.info("Loading Feature information");
		
		for (Feature feature : features) {
			
			feature.terms = featureMapper.terms(feature);
			feature.properties = featureMapper.properties(feature);
				
		}
		
		logger.info("Invoking feature converter");
		
		
	}
	
	
}
