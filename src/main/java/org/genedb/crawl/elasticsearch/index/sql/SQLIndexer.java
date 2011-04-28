package org.genedb.crawl.elasticsearch.index.sql;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.genedb.crawl.elasticsearch.LocatedFeatureUtil;
import org.genedb.crawl.elasticsearch.mappers.ElasticSearchFeatureMapper;
import org.genedb.crawl.elasticsearch.mappers.ElasticSearchOrganismsMapper;
import org.genedb.crawl.elasticsearch.mappers.ElasticSearchRegionsMapper;
import org.genedb.crawl.mappers.AuditMapper;
import org.genedb.crawl.mappers.FeatureMapper;
import org.genedb.crawl.mappers.FeaturesMapper;
import org.genedb.crawl.mappers.OrganismsMapper;
import org.genedb.crawl.mappers.RegionsMapper;
import org.genedb.crawl.mappers.TermsMapper;
import org.genedb.crawl.model.Cvterm;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.HierarchyRelation;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.OrganismProp;
import org.genedb.crawl.model.Sequence;

public class SQLIndexer {
	
	private static Logger logger = Logger.getLogger(SQLIndexer.class);
	
	public ElasticSearchOrganismsMapper esOrganismMapper;
	public ElasticSearchFeatureMapper esFeatureMapper;
	public ElasticSearchRegionsMapper esRegionsMapper;
	
	public OrganismsMapper organismMapper;
	public FeaturesMapper featuresMapper;
	public FeatureMapper featureMapper;
	public RegionsMapper regionsMapper;
	public TermsMapper termsMapper;
	
	private Set<Integer> organism_ids; 
	private Set<String> regions; 
	private Set<String> features; 
	
	public List<Cvterm> relationships = new ArrayList<Cvterm>();

	public AuditMapper auditMapper;
	
	public boolean exclude = false;
	public List<String> types;
	
	
	public SQLIndexer() {
		reset();
	}
	
	public void reset() {
		organism_ids = new HashSet<Integer>();
		regions = new HashSet<String>(); 
		features = new HashSet<String>(); 
	}
	
	/**
	 * 
	 * @param date
	 * @param organism if null, will query across organisms
	 */
	public void indexFeaturesSince(Date date, Organism organism) {
		
		
		if (auditMapper.exists()) {
			List<Feature> deleted = auditMapper.deleted(date);
			for (Feature toDeleteFromIndex : deleted) {
				esFeatureMapper.delete(toDeleteFromIndex);
			}
		} else {
			logger.warn("Audit schema does not exist in this database. Cannot delete features from index.");
		}
		
		
		List<Feature> modifiedFeatures = null;
		
		if (organism != null) {
			modifiedFeatures = featuresMapper.timelastmodified(date, organism.ID, types, exclude);
		} else {
			modifiedFeatures = featuresMapper.timelastmodified(date, null, types, exclude);
		}
		
		
		for (Feature f : modifiedFeatures) {
			
			f.coordinates = featureMapper.coordinates(f);
			LocatedFeature lf = LocatedFeatureUtil.fromFeature(f);
			
			// features
			if (lf.region != null) {
				indexRegion(lf.region);
			}
			
			indexOrganism(lf.organism_id);
			indexLocatedFeature(lf);
		}
	}
	
	public void indexRegionContents(String region) {
		
		logger.info("indexing region : " + region);
		
		indexRegion(region);
		
		int start = 0;
		int end = regionsMapper.sequence(region).length;
		List<LocatedFeature> features = regionsMapper.locations(region, start, end, exclude, types);
		
		for (LocatedFeature f : features) {
			
			// the regionsMapper.locations does not return the region name
			f.region = region;
			
			indexOrganism(f.organism_id);
			indexLocatedFeature(f);
		}
		
	}
	
	public void indexOrganismContents(Organism o) {
		indexOrganism(o);
		
		for (Feature region : regionsMapper.inorganism(o.ID, null, null, null)) {
			indexRegionContents(region.uniqueName);
		}
		
	}
	
	
	
	public void indexOrganisms() {
		for (Organism o : organismMapper.list()) {
			indexOrganism(o);
		}
	}
	
	public void indexOrganism(int id) {
		Organism o = organismMapper.getByID(id);
		indexOrganism(o);
	}
	
	public void indexOrganism(Organism o) {
		
		if (organism_ids.contains(o.ID)) {
			return;
		}
		
		organism_ids.add(o.ID);
		
		OrganismProp taxon = organismMapper.getOrganismProp(o.ID, "genedb_misc", "taxonId");
		OrganismProp translation_table = organismMapper.getOrganismProp(o.ID, "genedb_misc", "translationTable");
		
		logger.debug("Setting organism " + o.common_name);
		
		if (taxon != null) {
			o.taxonID = Integer.parseInt(taxon.value);
		}
		
		if (translation_table != null) {
			logger.debug("Setting translation table " + translation_table.value);
			o.translation_table = Integer.parseInt(translation_table.value);
		} 
		
		esOrganismMapper.createOrUpdate(o);
	}

	
	private void indexLocatedFeature(LocatedFeature feature) {
		
		if (features.contains(feature.uniqueName)) {
			logger.warn("Already indexed this feature in this run :" + feature.uniqueName);
		}
		
		features.add(feature.uniqueName);
		
		feature.terms = featureMapper.terms(feature);
		feature.properties = featureMapper.properties(feature);
		
		
		List<HierarchyRelation> relations = featuresMapper.getRelationshipsParents(feature.uniqueName, relationships);
		
		if (relations.size() > 0) {
			
			feature.parent = relations.get(0).uniqueName;
			logger.info("parent : " + feature.parent);
		}
		
		esFeatureMapper.createOrUpdate(feature);
	}
	
	public void indexRegion(String region) {
		
		if (regions.contains(region)) {
			return;
		}
		
		regions.add(region);
		
		Feature f = featureMapper.get(region, null, null);
		
		if (f != null) {
			
			logger.info("Generating region : " + f.uniqueName);

			Sequence s = regionsMapper.sequence(region);
			
			f.residues = s.dna;

			esRegionsMapper.createOrUpdate(f);
		} else {
			throw new RuntimeException("Could not find region " + region);
		}
	}
	
	
	
	
	
	
}
