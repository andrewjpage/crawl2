package org.genedb.crawl.elasticsearch.index.sql;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.genedb.crawl.elasticsearch.LocatedFeatureUtil;
import org.genedb.crawl.mappers.FeatureMapper;
import org.genedb.crawl.mappers.FeaturesMapper;
import org.genedb.crawl.mappers.TermsMapper;
import org.genedb.crawl.model.Coordinates;
import org.genedb.crawl.model.Cvterm;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.HierarchyRelation;
import org.genedb.crawl.model.LocatedFeature;

public class FeatureFiller {
	
	private Logger logger = Logger.getLogger(FeatureFiller.class);
	
	
	List<LocatedFeature> features;
	//List<LocatedFeature> locatedFeatures = new ArrayList<LocatedFeature>();
	
	FeatureMapper featureMapper;
	FeaturesMapper featuresMapper;
	TermsMapper termsMapper;
	
	List<Cvterm> relationships = new ArrayList<Cvterm>();
	
	public List<LocatedFeature> getLocatedFeatures() {
		return features;
	}
	
	public FeatureFiller(
			FeatureMapper featureMapper, 
			FeaturesMapper featuresMapper, 
			TermsMapper termsMapper, 
			List<LocatedFeature> features) {
		
		this.featureMapper = featureMapper;
		this.featuresMapper = featuresMapper;
		this.features = features;
		this.termsMapper = termsMapper;
		
		relationships.add(CvtermUtil.makeTerm(this.termsMapper, "derives_from", "sequence"));
		relationships.add(CvtermUtil.makeTerm(this.termsMapper, "part_of", "relationship"));
		
		
	}
	
	
	public void fill() {
		
		logger.info("Populating features");
		
		for (LocatedFeature feature : features) {
			
			feature.terms = featureMapper.terms(feature);
			feature.properties = featureMapper.properties(feature);
			//feature.coordinates = featureMapper.coordinates(feature);
			//feature.topLevel = false;
			
			//LocatedFeature locatedFeature = LocatedFeatureUtil.fromFeature(feature);
			
			List<HierarchyRelation> relations = featuresMapper.getRelationshipsParents(feature.uniqueName, relationships);
			
			if (relations.size() > 0) {
				
				feature.parent = relations.get(0).uniqueName;
				logger.info("parent : " + feature.parent);
			}
			
//			logger.info(feature.uniqueName + " coordinates :");
//			for (Coordinates coordinates : locatedFeature.coordinates) {
//				logger.info(" -- " + coordinates.region + " " + coordinates.toplevel);
//			}
			
			//locatedFeatures.add(locatedFeature);
			
		}
		
		
		
		
	}
	
	
}
