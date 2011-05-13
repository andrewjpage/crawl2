package org.genedb.crawl.mappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import org.genedb.crawl.model.Cvterm;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.HierarchicalFeature;
import org.genedb.crawl.model.HierarchyGeneFetchResult;
import org.genedb.crawl.model.HierarchyRelation;

public class MapperUtil {
	
	private static Logger logger = Logger.getLogger(MapperUtil.class);
	
	private static Set<String> geneTypes = new HashSet<String>(Arrays.asList(new String[]{"gene", "pseudogene"}));
	
	public static final List<Feature> getGeneFeatures(FeaturesMapper featuresMapper, List<String> features) {
		Map <String, Feature> map = new HashMap<String, Feature>();
		List<HierarchyGeneFetchResult> possibleGenes = featuresMapper.getGeneForFeature(features);
		
		for (HierarchyGeneFetchResult result : possibleGenes) {
			
			String[] ftypes = new String[]{result.ftype,result.ftype2,result.ftype3};
			String[] fs = new String[]{result.f,result.f2,result.f3};
			
			if (! map.containsKey(result.f)) {
				//FeatureGenes fg = new FeatureGenes();
				//fg.feature = result.f;
				//fg.type = result.ftype;
				
				Feature feature = new Feature();
				feature.uniqueName = result.f;
				feature.genes = new ArrayList<String>();
				//feature.t
				
				Cvterm c = new Cvterm();
				c.name = result.ftype;
				
				map.put(result.f, feature);
			}
			
			for (int i = 0; i < ftypes.length; i++) {
				String ftype = ftypes[i];
				String f = fs[i];
				
				logger.debug(String.format("Type: %s, Feature %s .", ftype, f));
				
				if (geneTypes.contains(ftype)) {
					map.get(result.f).genes.add(f);
				}
				
			}
			
		}
		
		logger.debug(map);
		
		return new ArrayList<Feature>(map.values());
	}
	
	/**
	 * 
	 * A recursive trawl up or down feature relationships. Can go up (parents) or down (children).
	 * 
	 * @param feature
	 * @param relationshipTypeIDs
	 * @param searchType
	 */
	public static void searchForRelations(FeaturesMapper featuresMapper, HierarchicalFeature feature, List<Cvterm> relationshipTypes, HierarchicalSearchType searchType) {
		
		List<HierarchyRelation> relations = null;
		
		if (searchType == HierarchicalSearchType.CHILDREN) {
			relations = featuresMapper.getRelationshipsChildren(feature.uniqueName, relationshipTypes);
		} else {
			relations = featuresMapper.getRelationshipsParents(feature.uniqueName, relationshipTypes);
		}
		
		if (relations == null) {
			return;
		}
		
		for (HierarchyRelation relation : relations) {
			
			HierarchicalFeature hf = new HierarchicalFeature();
			
			hf.relationship = relation.relationship_type;
			hf.relationshipType = relation.type;
			hf.uniqueName = relation.uniqueName;
			hf.name = relation.name;
			
			if (searchType == HierarchicalSearchType.CHILDREN) {
				feature.children.add(hf);
			} else {
				feature.parents.add(hf);
			}
			
			searchForRelations(featuresMapper, hf, relationshipTypes, searchType);
			
		}
		
	}
	
	public enum HierarchicalSearchType {
		PARENTS,
		CHILDREN
	}
}


