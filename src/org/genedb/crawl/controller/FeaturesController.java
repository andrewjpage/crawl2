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
import org.genedb.crawl.model.BlastPair;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.FeatureCollection;
import org.genedb.crawl.model.FeatureGenes;
import org.genedb.crawl.model.FeatureGenesList;
import org.genedb.crawl.model.HierarchicalFeatureList;
import org.genedb.crawl.model.HierarchyGeneFetchResult;
import org.genedb.crawl.model.HierarchyRelation;
import org.genedb.crawl.model.HierarchicalFeature;
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
	public FeatureCollection coordinates(@RequestParam("features") List<String> featureList, @RequestParam(value="region", required=false) String region ) {
		FeatureCollection results = new FeatureCollection();
		results.results = features.coordinates(featureList, region);
		return results;
	}
	
	@ResourceDescription("Returns a feature's synonyms.")
	@RequestMapping(method=RequestMethod.GET, value="/synonyms")
	public FeatureCollection synonyms(@RequestParam("features") List<String> featureList, @RequestParam(value="types", required=false) List<String> types) {
		FeatureCollection results = new FeatureCollection();
		results.results = features.synonyms(featureList, types);
		return results;
	}
	
	@ResourceDescription("Return matching features")
	@RequestMapping(method=RequestMethod.GET, value="/withnamelike")
	public FeatureCollection withnamelike(
			@RequestParam("term") String term,
			@RequestParam(value="regex", defaultValue="false") boolean regex, 
			@RequestParam(value="region", required=false) String region) {
		FeatureCollection results = new FeatureCollection();
		
		List<Feature> synonyms = features.synonymsLike(term, regex, region);
		List<Feature> matchingFeatures = features.featuresLike(term, regex, region);
		
		matchingFeatures.addAll(synonyms);
		
		results.results = matchingFeatures;
		
		return results;
	}
	
	
	@ResourceDescription("Return feature properties")
	@RequestMapping(method=RequestMethod.GET, value="/properties")
	public FeatureCollection properties(@RequestParam(value="features") List<String> featureList) {
		FeatureCollection results = new FeatureCollection();
		results.results = features.properties(featureList); 
		return results;
	}
	
	@ResourceDescription("Return feature properties")
	@RequestMapping(method=RequestMethod.GET, value="/withproperty")
	public FeatureCollection withproperty(
			@RequestParam("value") String value,
			@RequestParam(value="regex", defaultValue="false") boolean regex, 
			@RequestParam(value="region", required=false) String region,
			@RequestParam(value="type", required=false) String type) {
		FeatureCollection results = new FeatureCollection();
		results.results = features.withproperty(value, regex, region, type); 
		return results;
	}
	
	@ResourceDescription("Return feature pubs")
	@RequestMapping(method=RequestMethod.GET, value="/pubs")
	public FeatureCollection pubs(@RequestParam(value="features") List<String> featureList) {
		FeatureCollection results = new FeatureCollection();
		results.results = features.pubs(featureList); 
		return results;
	}
	
	@ResourceDescription("Return feature dbxrefs")
	@RequestMapping(method=RequestMethod.GET, value="/dbxrefs")
	public FeatureCollection dbxrefs(@RequestParam(value="features") List<String> featureList) {
		FeatureCollection results = new FeatureCollection();
		results.results = features.dbxrefs(featureList); 
		return results;
	}
	
	@ResourceDescription("Return feature cvterms")
	@RequestMapping(method=RequestMethod.GET, value="/terms")
	public FeatureCollection terms(@RequestParam(value="features") List<String> featureList, @RequestParam(value="cvs", required=false) List<String> cvs) {
		FeatureCollection results = new FeatureCollection();
		results.results = features.terms(featureList, cvs); 
		return results;
	}
	
	@ResourceDescription("Return feature with specified cvterm")
	@RequestMapping(method=RequestMethod.GET, value="/withterm")
	public FeatureCollection withterm(
			@RequestParam(value="term") String term, 
			@RequestParam(value="cv", required=false) String cv,
			@RequestParam(value="regex", defaultValue="false") boolean regex, 
			@RequestParam(value="region", required=false) String region) {
		FeatureCollection results = new FeatureCollection();
		
		logger.info(String.format("%s - %s - %s - %s", term, cv, regex, region));
		
		results.results = features.withterm(term, cv, regex, region);
		return results;
	}
	
	@ResourceDescription("Return feature orthologues")
	@RequestMapping(method=RequestMethod.GET, value="/orthologues")
	public FeatureCollection orthologues(@RequestParam(value="features") List<String> featureList) {
		FeatureCollection results = new FeatureCollection();
		results.results = features.orthologues(featureList); 
		return results;
	}
	
	@ResourceDescription("Return feature clusters")
	@RequestMapping(method=RequestMethod.GET, value="/clusters")
	public FeatureCollection clusters(@RequestParam(value="features") List<String> featureList) {
		FeatureCollection results = new FeatureCollection();
		results.results = features.clusters(featureList); 
		return results;
	}
	
	@ResourceDescription("Return blast hits between two features")
	@RequestMapping(method=RequestMethod.GET, value="/blastpair")
	public List<BlastPair> blastpair(
			@RequestParam(value="f1") String f1, @RequestParam(value="start1") int start1, @RequestParam(value="end1") int end1,
			@RequestParam(value="f2") String f2, @RequestParam(value="start1") int start2, @RequestParam(value="end1") int end2,
			@RequestParam(value="length", required=false) Integer length,
			@RequestParam(value="score", required=false) Integer score) {
		return features.blastPairs(f1, start1, end1, f2, start2, end2, length, score);
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
