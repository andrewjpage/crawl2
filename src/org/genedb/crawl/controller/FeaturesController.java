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
import org.genedb.crawl.model.Cvterm;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.HierarchyGeneFetchResult;
import org.genedb.crawl.model.HierarchyRelation;
import org.genedb.crawl.model.HierarchicalFeature;
import org.genedb.crawl.model.Results;
import org.gmod.cat.FeaturesMapper;
import org.gmod.cat.TermsMapper;
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
	FeaturesMapper featuresMapper;
	
	@Autowired
	TermsMapper terms;
	
	private enum HierarchicalSearchType {
		PARENTS,
		CHILDREN
	}
	
	private Set<String> geneTypes = new HashSet<String>(Arrays.asList(new String[]{"gene", "pseudogene"}));
	private String[] defaultRelationshipTypes = new String[] {"part_of", "derives_from"};
	
	@ResourceDescription("Get a feature's gene")
	@RequestMapping(method=RequestMethod.GET, value="/genes")
	public Results genes(Results results, @RequestParam(value="features") List<String> features) {
		results.features = getGeneFeatures(features);
		return results;
		
	}
	
	@ResourceDescription("Returns the hierarchy of a feature (i.e. the parent/child relationship graph), but routed on the feature itself (rather than Gene).")
	@RequestMapping(method=RequestMethod.GET, value="/hierarchy")
	public Results hierarchy(Results results, 
			@RequestParam("features") List<String> features, 
			@RequestParam(value="root_on_genes", defaultValue="false", required=false) boolean root_on_genes,
			@RequestParam(value="relationships", required=false) String[] relationships) throws CrawlException {
		
		if (relationships == null) {
			relationships = defaultRelationshipTypes;
		}
		
		List<Cvterm> relationshipTypes = getRelationshipTypes(Arrays.asList(relationships), terms);
		List<String> featuresToRecurse = features;
		List<HierarchicalFeature> hfs = new ArrayList<HierarchicalFeature>();
		
		if (root_on_genes) {
			featuresToRecurse = new ArrayList<String>();
			
			Collection<Feature> featureGenes = getGeneFeatures(features);
			
			for (Feature fg : featureGenes) {
				featuresToRecurse.addAll(fg.genes);
			}
			
		}
		
		for (String feature : featuresToRecurse) {
			
			HierarchicalFeature hf = new HierarchicalFeature();
			hf.uniqueName = feature;
			
			this.searchForRelations(hf, relationshipTypes, HierarchicalSearchType.CHILDREN);
			this.searchForRelations(hf, relationshipTypes, HierarchicalSearchType.PARENTS);
			
			hfs.add(hf);
			
			
			
			
		}
		
		
		results.hierarchy = hfs;
		
		return results;
		
	}
	
	@ResourceDescription("Returns coordinages of a feature if located on a region.")
	@RequestMapping(method=RequestMethod.GET, value="/coordinates")
	public Results coordinates(Results results, @RequestParam("features") List<String> features, @RequestParam(value="region", required=false) String region ) {
		results.features = featuresMapper.coordinates(features, region);
		return results;
	}
	
	@ResourceDescription("Returns a feature's synonyms.")
	@RequestMapping(method=RequestMethod.GET, value="/synonyms")
	public Results synonyms(Results results, @RequestParam("features") List<String> features, @RequestParam(value="types", required=false) List<String> types) {
		results.features = featuresMapper.synonyms(features, types);
		return results;
	}
	
	@ResourceDescription("Return matching features")
	@RequestMapping(method=RequestMethod.GET, value="/withnamelike")
	public Results withnamelike(Results results, 
			@RequestParam("term") String term,
			@RequestParam(value="regex", defaultValue="false") boolean regex, 
			@RequestParam(value="region", required=false) String region) {
		List<Feature> synonyms = featuresMapper.synonymsLike(term, regex, region);
		List<Feature> matchingFeatures = featuresMapper.featuresLike(term, regex, region);
		matchingFeatures.addAll(synonyms);
		results.features = matchingFeatures;
		return results;
	}
	
	
	@ResourceDescription("Return feature properties")
	@RequestMapping(method=RequestMethod.GET, value="/properties")
	public Results properties(Results results, @RequestParam(value="features") List<String> features) {
		results.features = featuresMapper.properties(features);
		return results;
	}
	
	@ResourceDescription("Return feature properties")
	@RequestMapping(method=RequestMethod.GET, value="/withproperty")
	public Results withproperty(Results results, 
			@RequestParam("value") String value,
			@RequestParam(value="regex", defaultValue="false") boolean regex, 
			@RequestParam(value="region", required=false) String region,
			@RequestParam(value="type", required=false) String type) {
		results.features = featuresMapper.withproperty(value, regex, region, type);
		return results;
	}
	
	@ResourceDescription("Return feature pubs")
	@RequestMapping(method=RequestMethod.GET, value="/pubs")
	public Results pubs(Results results, @RequestParam(value="features") List<String> features) {
		results.features = featuresMapper.pubs(features);
		return results;
	}
	
	@ResourceDescription("Return feature dbxrefs")
	@RequestMapping(method=RequestMethod.GET, value="/dbxrefs")
	public Results dbxrefs(Results results, @RequestParam(value="features") List<String> features) {
		results.features = featuresMapper.dbxrefs(features);
		return results;
	}
	
	@ResourceDescription(value="Return feature cvterms")
	@RequestMapping(method=RequestMethod.POST, value="/terms")
	public Results termsPOST(Results results, @RequestParam(value="features") List<String> features, @RequestParam(value="cvs", required=false) List<String> cvs) {
		return terms(results, features, cvs);
	}
	
	@ResourceDescription(value="Return feature cvterms")
	@RequestMapping(method=RequestMethod.GET, value="/terms")
	public Results terms(Results results, @RequestParam(value="features") List<String> features, @RequestParam(value="cvs", required=false) List<String> cvs) {
		results.features = featuresMapper.terms(features, cvs);
		return results;
	}
	
	@ResourceDescription("Return feature with specified cvterm")
	@RequestMapping(method=RequestMethod.GET, value="/withterm")
	public Results withterm(Results results, 
			@RequestParam(value="term") String term, 
			@RequestParam(value="cv", required=false) String cv,
			@RequestParam(value="regex", defaultValue="false") boolean regex, 
			@RequestParam(value="region", required=false) String region) {
		
		logger.info(String.format("%s - %s - %s - %s", term, cv, regex, region));
		
		results.features = featuresMapper.withterm(term, cv, regex, region);
		return results;
		
		
	}
	
	@ResourceDescription("Return feature orthologues")
	@RequestMapping(method=RequestMethod.GET, value="/orthologues")
	public Results orthologues(Results results, @RequestParam(value="features") List<String> features) {
		results.features = featuresMapper.orthologues(features);
		return results;
	}
	
	@ResourceDescription(value="Return feature clusters", type="Results")
	@RequestMapping(method=RequestMethod.GET, value="/clusters")
	public Results clusters(Results results, @RequestParam(value="features") List<String> features) {
		results.features = featuresMapper.clusters(features);
		return results; 
	}
	
	@ResourceDescription("Return blast hits between two features")
	@RequestMapping(method=RequestMethod.GET, value="/blastpair")
	public Results blastpair(Results results, 
			@RequestParam(value="f1") String f1, @RequestParam(value="start1") int start1, @RequestParam(value="end1") int end1,
			@RequestParam(value="f2") String f2, @RequestParam(value="start1") int start2, @RequestParam(value="end1") int end2,
			@RequestParam(value="length", required=false) Integer length,
			@RequestParam(value="score", required=false) Integer score) {
		results.blastPairs = featuresMapper.blastPairs(f1, start1, end1, f2, start2, end2, length, score);
		return results; 
	}
	
//	private List<FeatureGenes> getGeneFeatures(List<String> featureList) {
//		Map <String, FeatureGenes> map = new HashMap<String, FeatureGenes>();
//		List<HierarchyGeneFetchResult> possibleGenes = features.getGeneForFeature(featureList);
//		
//		for (HierarchyGeneFetchResult result : possibleGenes) {
//			
//			String[] ftypes = new String[]{result.ftype,result.ftype2,result.ftype3};
//			String[] fs = new String[]{result.f,result.f2,result.f3};
//			
//			if (! map.containsKey(result.f)) {
//				FeatureGenes fg = new FeatureGenes();
//				fg.feature = result.f;
//				fg.type = result.ftype;
//				map.put(result.f, fg);
//			}
//			
//			for (int i = 0; i < ftypes.length; i++) {
//				String ftype = ftypes[i];
//				String f = fs[i];
//				
//				logger.debug(String.format("Type: %s, Feature %s .", ftype, f));
//				
//				if (geneTypes.contains(ftype)) {
//					map.get(result.f).genes.add(f);
//				}
//				
//			}
//			
//		}
//		
//		logger.debug(map);
//		
//		return new ArrayList<FeatureGenes>(map.values());
//	}

	private List<Feature> getGeneFeatures(List<String> features) {
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
	private void searchForRelations(HierarchicalFeature feature, List<Cvterm> relationshipTypes, HierarchicalSearchType searchType) {
		
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
			
			searchForRelations(hf, relationshipTypes, searchType);
			
		}
		
		
		
	}
	
}
