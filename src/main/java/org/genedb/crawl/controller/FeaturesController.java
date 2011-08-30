package org.genedb.crawl.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.jws.WebService;

import org.apache.log4j.Logger;
import org.genedb.crawl.CrawlException;
import org.genedb.crawl.annotations.ResourceDescription;
import org.genedb.crawl.mappers.FeatureMapper;
import org.genedb.crawl.mappers.FeaturesMapper;
import org.genedb.crawl.mappers.MapperUtil;
import org.genedb.crawl.mappers.OrganismsMapper;
import org.genedb.crawl.mappers.TermsMapper;
import org.genedb.crawl.mappers.MapperUtil.HierarchicalSearchType;
import org.genedb.crawl.model.BlastPair;
import org.genedb.crawl.model.Coordinates;
import org.genedb.crawl.model.Cvterm;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.FeatureRelationship;
import org.genedb.crawl.model.Gene;
import org.genedb.crawl.model.HierarchicalFeature;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.Statistic;
import org.genedb.crawl.model.Transcript;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/features")
@ResourceDescription("Feature related queries")
@WebService(serviceName="features")
public class FeaturesController extends BaseQueryController {
	
	private static Logger logger = Logger.getLogger(FeaturesController.class);
	
	@Autowired
	FeaturesMapper featuresMapper;
	
	@Autowired
	FeatureMapper featureMapper;
	
	@Autowired
	TermsMapper terms;
	
	@Autowired
	OrganismsMapper organismsMapper;
	
	private String[] defaultRelationshipTypes = new String[] {"part_of", "derives_from"};
	
	@ResourceDescription("Get a feature's gene")
	@RequestMapping(method=RequestMethod.GET, value="/genes")
	public List<Feature> genes(@RequestParam(value="features") List<String> features) {
		return MapperUtil.getGeneFeatures(featuresMapper, features);
	}
	
	@ResourceDescription("Returns the hierarchy of a feature (i.e. the parent/child relationship graph), but routed on the feature itself (rather than Gene).")
	@RequestMapping(method=RequestMethod.GET, value="/hierarchy")
	public List<HierarchicalFeature> hierarchy( 
			@RequestParam("features") List<String> features, 
			@RequestParam(value="root_on_genes", defaultValue="false", required=false) Boolean root_on_genes,
			@RequestParam(value="relationships", required=false) String[] relationships) throws CrawlException {
		
	    // JAX-WS does not know about defaultValue
	    if (root_on_genes == null)
	        root_on_genes = false;
	    
		if (relationships == null || relationships.length < 1) {
			relationships = defaultRelationshipTypes;
		}
		
		List<Cvterm> relationshipTypes = getRelationshipTypes(Arrays.asList(relationships), terms);
		List<String> featuresToRecurse = features;
		List<HierarchicalFeature> hfs = new ArrayList<HierarchicalFeature>();
		
		if (root_on_genes) {
			featuresToRecurse = new ArrayList<String>();
			
			Collection<Feature> featureGenes = MapperUtil.getGeneFeatures(featuresMapper,features);
			
			for (Feature fg : featureGenes) {
				featuresToRecurse.addAll(fg.genes);
			}
			
		}
		
		for (String feature : featuresToRecurse) {
			
			HierarchicalFeature hf = new HierarchicalFeature();
			hf.uniqueName = feature;
			
			Feature f = featureMapper.getOfType(feature, null, null, null);
			hf.type = f.type.name;
			
			MapperUtil.searchForRelations(featuresMapper, hf, relationshipTypes, HierarchicalSearchType.CHILDREN);
			MapperUtil.searchForRelations(featuresMapper, hf, relationshipTypes, HierarchicalSearchType.PARENTS);
			
			hfs.add(hf);
			
			
			
			
		}
		
		
		return hfs;
		
		
		
	}
	
	@ResourceDescription("Returns coordinages of a feature if located on a region.")
	@RequestMapping(method=RequestMethod.GET, value="/coordinates")
	public List<Feature> coordinates(
			@RequestParam("features") List<String> features, 
			@RequestParam(value="region", required=false) String region ) {
		return featuresMapper.coordinates(features, region);
	}
	
	@ResourceDescription("Returns a feature's synonyms.")
	@RequestMapping(method=RequestMethod.GET, value="/synonyms")
	public List<Feature> synonyms(
			@RequestParam("features") List<String> features,
			@RequestParam(value="types", required=false) List<String> types) {
		return featuresMapper.synonyms(features, types);
	}
	
	@ResourceDescription("Return matching features")
	@RequestMapping(method=RequestMethod.GET, value="/withnamelike")
	public List<Feature> withnamelike( 
			@RequestParam("term") String term,
			@RequestParam(value="regex", defaultValue="false") boolean regex, 
			@RequestParam(value="region", required=false) String region) {
		List<Feature> synonyms = featuresMapper.synonymsLike(term, regex, region);
		List<Feature> matchingFeatures = featuresMapper.featuresLike(term, regex, region);
		matchingFeatures.addAll(synonyms);
		return matchingFeatures;
	}
	
	
	@ResourceDescription("Return feature properties")
	@RequestMapping(method=RequestMethod.GET, value="/properties")
	public List<Feature> properties(
			@RequestParam(value="features") List<String> features, 
			@RequestParam(value="types", required=false) List<String> types) {
		return featuresMapper.properties(features,types);
	}
	
	@ResourceDescription("Return feature properties")
	@RequestMapping(method=RequestMethod.GET, value="/withproperty")
	public List<Feature> withproperty( 
			@RequestParam("value") String value,
			@RequestParam(value="regex", defaultValue="false") boolean regex, 
			@RequestParam(value="region", required=false) String region,
			@RequestParam(value="type", required=false) String type) {
		return featuresMapper.withproperty(value, regex, region, type);
	}
	
	@ResourceDescription("Return feature pubs")
	@RequestMapping(method=RequestMethod.GET, value="/pubs")
	public List<Feature> pubs(@RequestParam(value="features") List<String> features) {
		return featuresMapper.pubs(features);
	}
	
	@ResourceDescription("Return feature dbxrefs")
	@RequestMapping(method=RequestMethod.GET, value="/dbxrefs")
	public List<Feature> dbxrefs(@RequestParam(value="features") List<String> features) {
		return featuresMapper.dbxrefs(features);
	}
	
	
	@ResourceDescription(value="Return feature cvterms")
	@RequestMapping(method=RequestMethod.GET, value="/terms")
	public List<Feature> terms(@RequestParam(value="features") List<String> features, @RequestParam(value="cvs", required=false) List<String> cvs) {
		return featuresMapper.terms(features, cvs);
	}
	
	@ResourceDescription("Return feature with specified cvterm")
	@RequestMapping(method=RequestMethod.GET, value="/withterm")
	public List<Feature> withterm(
			@RequestParam(value="term") String term, 
			@RequestParam(value="cv", required=false) String cv,
			@RequestParam(value="regex", defaultValue="false") boolean regex, 
			@RequestParam(value="region", required=false) String region) {
		
		logger.info(String.format("%s - %s - %s - %s", term, cv, regex, region));
		
		return featuresMapper.withterm(term, cv, regex, region);
		
	}
	
	@ResourceDescription("Return feature orthologues")
	@RequestMapping(method=RequestMethod.GET, value="/orthologues")
	public List<Feature> orthologues(@RequestParam(value="features") List<String> features) {
		return featuresMapper.orthologues(features);
	}
	
	@ResourceDescription(value="Return feature clusters")
	@RequestMapping(method=RequestMethod.GET, value="/clusters")
	public List<Feature> clusters(@RequestParam(value="features") List<String> features) {
		return featuresMapper.clusters(features);
	}
	
	@ResourceDescription(value="Return features that have had annotation changes")
	@RequestMapping(method=RequestMethod.GET, value="/annotation_changes")
	public List<Feature> annotationModified( 
			@RequestParam(value="date") Date date, 
			@RequestParam("organism") String organism, 
			@RequestParam(value="region", required = false) String region) throws CrawlException {
		Organism o = getOrganism(organismsMapper, organism);
		return featuresMapper.annotationModified(date, o.ID, region);
	}
	
	@ResourceDescription(value="Return features that have had annotation changes")
	@RequestMapping(method=RequestMethod.GET, value="/annotation_changes_statistics")
	public List<Statistic> annotationModifiedStatistics( 
			@RequestParam(value="date") Date date, 
			@RequestParam("organism") String organism, 
			@RequestParam(value="region", required = false) String region) throws CrawlException {
		Organism o = getOrganism(organismsMapper, organism);
		return featuresMapper.annotationModifiedStatistics(date, o.ID, region);
	}
	
	
	@ResourceDescription("Return blast hits between two features")
	@RequestMapping(method=RequestMethod.GET, value="/blastpair")
	public List<BlastPair> blastpair( 
			@RequestParam(value="f1") String f1, 
			@RequestParam(value="start1") int start1, 
			@RequestParam(value="end1") int end1,
			@RequestParam(value="f2") String f2, 
			@RequestParam(value="start2") int start2, 
			@RequestParam(value="end2") int end2,
			@RequestParam(value="length", required=false) Integer length,
			@RequestParam(value="normscore", required=false) Double score) {
		logger.info("Filtering on score :");
		logger.info(score);
		return featuresMapper.blastPairs(f1, start1, end1, f2, start2, end2, length, score); 
	}
	
		
		
	
	@ResourceDescription("Return a gene's transcripts")
	@RequestMapping(method=RequestMethod.GET, value="/transcripts")
	public List<Gene> transcripts(@RequestParam(value="gene") String gene, @RequestParam(value="exons") boolean exons) {
		List<Gene> l = new ArrayList<Gene>(); 
		Gene geneFeature = (Gene) featureMapper.getOfType(gene, null, null, "gene");
		if (geneFeature != null) {
			logger.info(geneFeature.getClass());
			logger.info(geneFeature.uniqueName);
			
			geneFeature.transcripts = featureMapper.transcripts(geneFeature, exons);
			logger.info(geneFeature.transcripts);
			
			for (Transcript t : geneFeature.transcripts) {
				logger.info(t.uniqueName);
			}
			
			 
			l.add(geneFeature);
			
		}
		
		
		return l;
		
		//return featuresMapper.pubs(features);
	}
	
	@ResourceDescription("Return a gene's transcripts")
	@RequestMapping(method=RequestMethod.GET, value="/getInfo")
	public LocatedFeature getInfo(
	        @RequestParam(value="feature") String feature, 
	        @RequestParam(value="organism",required=false) String organism, 
	        @RequestParam(value="name",required=false) String name, 
	        @RequestParam(value="type",required=false) String type) {
		
		
		Integer organism_id =  null;
		if (organism != null) {
		    Organism o = this.getOrganism(organismsMapper, organism);
		    if (o != null) 
		        organism_id = o.ID;
		}
		
		LocatedFeature resultFeature = featureMapper.getOfType(feature, organism_id, name, type);
		
		summarise(resultFeature);
		
		return resultFeature;
		
		//return featuresMapper.pubs(features);
	}
	
	private void summarise (Feature resultFeature) {
	    
	    resultFeature.coordinates = featureMapper.coordinates(resultFeature);
        
	    // TODO - this might need to be fixed to work with non-LocatedFeature instances
	    if (resultFeature instanceof LocatedFeature 
	            && resultFeature.coordinates != null 
	            && resultFeature.coordinates.size() > 0) {
            LocatedFeature locatedFeature = (LocatedFeature) resultFeature;
            Coordinates c = locatedFeature.coordinates.get(0);
            locatedFeature.fmin = c.fmin;
            locatedFeature.fmax = c.fmax;
            locatedFeature.region = c.region;
            locatedFeature.phase = c.phase;
            locatedFeature.strand = c.strand;
	        
	    }
	    
        resultFeature.properties = featureMapper.properties(resultFeature);
        resultFeature.terms = featureMapper.terms(resultFeature);
        resultFeature.synonyms = featureMapper.synonyms(resultFeature);
        resultFeature.pubs = featureMapper.pubs(resultFeature);
        
	}
	
	private Feature getFeature(String uniqueName, String name, String organism) {
	    Integer organism_id =  null;
	    
        if (organism != null) {
            Organism o = this.getOrganism(organismsMapper, organism);
            if (o != null) 
                organism_id = o.ID;
        }
        
        Feature resultFeature = featureMapper.get(uniqueName, name, organism_id);
        
        return resultFeature;
	}
	
	
	@RequestMapping(method=RequestMethod.GET, value="/parents")
    public List<FeatureRelationship> parents( 
            @RequestParam("feature") String featureUniqueName, 
            @RequestParam(value="organism",required=false) String organism, 
            @RequestParam(value="name",required=false) String name,
            @RequestParam(value="relationships", required=false) String[] relationships) throws CrawlException {
	    
	    if (relationships == null || relationships.length < 1) 
            relationships = defaultRelationshipTypes;
        
        List<Cvterm> relationshipTerms = getRelationshipTypes(Arrays.asList(relationships), terms);
        Feature feature = getFeature(featureUniqueName, name, organism);
        return featureMapper.parents(feature, relationshipTerms);
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/children")
    public List<FeatureRelationship> children( 
            @RequestParam("feature") String featureUniqueName, 
            @RequestParam(value="organism",required=false) String organism, 
            @RequestParam(value="name",required=false) String name,
            @RequestParam(value="relationships", required=false) String[] relationships) throws CrawlException {
        
        if (relationships == null || relationships.length < 1) 
            relationships = defaultRelationshipTypes;
        
        List<Cvterm> relationshipTerms = getRelationshipTypes(Arrays.asList(relationships), terms);
        Feature feature = getFeature(featureUniqueName, name, organism);
        return featureMapper.children(feature, relationshipTerms);
    }
	
	
	@ResourceDescription("Returns the hierarchy of a feature (i.e. the parent/child relationship graph), but routed on the feature itself (rather than Gene).")
    @RequestMapping(method=RequestMethod.GET, value="/hierarchyNew")
    public Feature geneHierarchy( 
            @RequestParam("feature") String featureUniqueName, 
            @RequestParam(value="organism",required=false) String organism, 
            @RequestParam(value="name",required=false) String name,
            @RequestParam(value="relationships", required=false) String[] relationships,
            @RequestParam(value="includeSummaries", required=false) Boolean includeSummaries
            ) throws CrawlException {
        
        
        if (relationships == null || relationships.length < 1) {
            relationships = defaultRelationshipTypes;
        }
        
        if (includeSummaries == null)
            includeSummaries = true;
        
        List<Cvterm> ofType = getRelationshipTypes(Arrays.asList(relationships), terms);
        
        Feature feature = getFeature(featureUniqueName, name, organism);
        
        Feature hierarchyRoot = hierarchyRoot(feature, ofType);
        
        if (hierarchyRoot == null)
            hierarchyRoot = feature;
        
        getDescendants(hierarchyRoot, ofType, includeSummaries);
        
        return hierarchyRoot;
        
    }
	
	public Feature geneSummary(@RequestParam("feature") String featureUniqueName, 
            @RequestParam(value="organism",required=false) String organism, 
            @RequestParam(value="name",required=false) String name) {
	    
	    List<Cvterm> ofType = getRelationshipTypes(Arrays.asList(defaultRelationshipTypes), terms);
        
        Feature feature = getFeature(featureUniqueName, name, organism);
        
        Feature hierarchyRoot = hierarchyRoot(feature, ofType);
        
        if (hierarchyRoot == null)
            hierarchyRoot = feature;
        
        getDescendants(hierarchyRoot, ofType, false);
        
        return hierarchyRoot;
        
	    
	    
	}
	
	private Feature hierarchyRoot(Feature currentFeature, List<Cvterm> ofType) {
	    
	    if (currentFeature.type.name.equals("gene") || currentFeature.type.name.equals("pseudogene"))
	        return currentFeature;
	    
	    List<FeatureRelationship> parents = featureMapper.parents(currentFeature, ofType);
	    
	    for (FeatureRelationship parent : parents) {
	        // parents are objects
            Feature root = hierarchyRoot(parent.object, ofType);
            
            if (root != null) {
                return root;
            }
            
        }
	    
	    return null;
	}
	
	private void getDescendants(Feature feature, List<Cvterm> ofType, boolean includeSummaries) {
	    
	    feature.children = featureMapper.children(feature, ofType);
	    if (includeSummaries)
	        summarise(feature);
	    
	    if (feature.children == null)
	        return;
	    
	    for (FeatureRelationship relationship : feature.children) {
	        // children are subjects
	        getDescendants(relationship.subject, ofType, includeSummaries);
	    }
	    
	}
	

    @ResourceDescription("Return features located on features")
	@RequestMapping(method=RequestMethod.GET, value="/locations")
    public List<LocatedFeature> locations(@RequestParam("feature") String  feature ) {
        return this.featuresMapper.locations(feature);
    }
}
