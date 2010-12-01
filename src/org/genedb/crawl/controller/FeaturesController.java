package org.genedb.crawl.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.genedb.crawl.CrawlException;
import org.genedb.crawl.annotations.ResourceDescription;
import org.genedb.crawl.model.FeatureGenes;
import org.genedb.crawl.model.FeatureGenesList;
import org.genedb.crawl.model.HierarchicalFeatureList;
import org.genedb.crawl.model.HierarchyGeneFetchResult;
import org.genedb.crawl.model.HierarchyRelation;
import org.genedb.crawl.model.HierarchicalFeature;
import org.genedb.crawl.model.RegionCoordinatesList;
import org.gmod.cat.Features;
import org.gmod.cat.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/features")
@ResourceDescription("Feature related queries")
public class FeaturesController extends BaseQueryController {
	
	private Logger logger = Logger.getLogger(FeaturesController.class);
	
	@Autowired
	Features features;
	
	@Autowired
	Terms terms;
	
	private enum HierarchicalSearchType {
		PARENTS,
		CHILDREN
	}
	
	private Set<String> geneTypes = new HashSet<String>(Arrays.asList(new String[]{"gene", "pseudogene"}));
	private String[] defaultRelationshipTypes = new String[] {"part_of", "derives_from"};
	
	@ResourceDescription("Get a feature's gene")
	@RequestMapping(method=RequestMethod.GET, value="/genes")
	public FeatureGenesList genes(@RequestParam(value="features") List<String> featureList) {
		
		Collection<FeatureGenes> genes = getGeneFeatures(featureList);
		FeatureGenesList featureGeneList = new FeatureGenesList ();
		featureGeneList.genes.addAll(genes);
		return featureGeneList;
		
	}
	
	@ResourceDescription("Returns the hierarchy of a feature (i.e. the parent/child relationship graph), but routed on the feature itself (rather than Gene).")
	@RequestMapping(method=RequestMethod.GET, value="/hierarchy")
	public HierarchicalFeatureList hierarchy(
			@RequestParam("features") List<String> featureList, 
			@RequestParam(value="root_on_genes", defaultValue="false", required=false) boolean root_on_genes,
			@RequestParam(value="relationships", required=false) String[] relationships) throws CrawlException {
		
		if (relationships == null) {
			relationships = defaultRelationshipTypes;
		}
		
		List<Integer> relationshipTypeIDs = getRelationshipTypeIDs(terms, Arrays.asList(relationships));
		List<String> featuresToRecurse = featureList;
		List<HierarchicalFeature> hfs = new ArrayList<HierarchicalFeature>();
		
		if (root_on_genes) {
			featuresToRecurse = new ArrayList<String>();
			
			Collection<FeatureGenes> featureGenes = getGeneFeatures(featureList);
			
			for (FeatureGenes fg : featureGenes) {
				featuresToRecurse.addAll(fg.genes);
			}
			
		}
		
		for (String feature : featuresToRecurse) {
			
			HierarchicalFeature hf = new HierarchicalFeature();
			hf.uniqueName = feature;
			
			this.searchForRelations(hf, relationshipTypeIDs, HierarchicalSearchType.CHILDREN);
			this.searchForRelations(hf, relationshipTypeIDs, HierarchicalSearchType.PARENTS);
			
			hfs.add(hf);
			
		}
		
		HierarchicalFeatureList hfl = new HierarchicalFeatureList();
		hfl.hierarchy = hfs;
		
		return hfl;
	}
	
	@ResourceDescription("Returns coordinages of a feature if located on a region.")
	@RequestMapping(method=RequestMethod.GET, value="/coordinates")
	public RegionCoordinatesList coordinates(@RequestParam("features") List<String> featureList, @RequestParam(value="region", required=false) String region ) {
		return features.coordinates(featureList, region);
	}
	
	
	private List<FeatureGenes> getGeneFeatures(List<String> featureList) {
		Map <String, FeatureGenes> map = new HashMap<String, FeatureGenes>();
		List<HierarchyGeneFetchResult> possibleGenes = features.getGeneForFeature(featureList);
		
		for (HierarchyGeneFetchResult result : possibleGenes) {
			
			String[] ftypes = new String[]{result.ftype,result.ftype2,result.ftype3};
			String[] fs = new String[]{result.f,result.f2,result.f3};
			
			if (! map.containsKey(result.f)) {
				FeatureGenes fg = new FeatureGenes();
				fg.feature = result.f;
				fg.type = result.ftype;
				map.put(result.f, fg);
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
		
		return new ArrayList<FeatureGenes>(map.values());
	}

	/**
	 * 
	 * A recursive trawl up or down feature relationships. Can go up (parents) or down (children).
	 * 
	 * @param feature
	 * @param relationshipTypeIDs
	 * @param searchType
	 */
	private void searchForRelations(HierarchicalFeature feature, List<Integer> relationshipTypeIDs, HierarchicalSearchType searchType) {
		
		List<HierarchyRelation> relations = null;
		
		if (searchType == HierarchicalSearchType.CHILDREN) {
			relations = features.getRelationshipsChildren(feature.uniqueName, relationshipTypeIDs);
		} else {
			relations = features.getRelationshipsParents(feature.uniqueName, relationshipTypeIDs);
		}
		
		if (relations == null) {
			return;
		}
		
		for (HierarchyRelation relation : relations) {
			
			HierarchicalFeature hf = new HierarchicalFeature();
			
			hf.relationship = relation.relationship_type;
			hf.type = relation.type;
			hf.uniqueName = relation.uniqueName;
			hf.name = relation.name;
			
			if (searchType == HierarchicalSearchType.CHILDREN) {
				feature.children.add(hf);
			} else {
				feature.parents.add(hf);
			}
			
			searchForRelations(hf, relationshipTypeIDs, searchType);
			
		}
		
		
		
	}
	
}
