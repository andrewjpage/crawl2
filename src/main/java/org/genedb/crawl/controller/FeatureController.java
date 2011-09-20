package org.genedb.crawl.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jws.WebService;

import org.biojava.bio.BioException;
import org.genedb.crawl.CrawlException;
import org.genedb.crawl.annotations.ResourceDescription;
import org.genedb.crawl.mappers.FeatureMapper;
import org.genedb.crawl.mappers.FeaturesMapper;
import org.genedb.crawl.mappers.OrganismsMapper;
import org.genedb.crawl.mappers.RegionsMapper;
import org.genedb.crawl.mappers.TermsMapper;
import org.genedb.crawl.model.Coordinates;
import org.genedb.crawl.model.Cvterm;
import org.genedb.crawl.model.Dbxref;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.Property;

import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.modelling.FeatureMapperUtil;
import org.genedb.util.TranslationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/feature")
@ResourceDescription("Single feature related queries")
@WebService(serviceName="feature")
public class FeatureController extends BaseQueryController{
    
    @Autowired
    public FeaturesMapper  featuresMapper;
    
    @Autowired
    public FeatureMapper   featureMapper;
    
    @Autowired
    public TermsMapper     terms;
    
    @Autowired
    public OrganismsMapper organismsMapper;
    
    @Autowired
    public RegionsMapper regionsMapper;
    
    private String[] defaultRelationshipTypes = new String[] {"part_of", "derives_from"};

    @ResourceDescription("Return a gene's transcripts")
    @RequestMapping(method=RequestMethod.GET, value="/info") // , "/{organism}/{uniqueName}/", "/{organism}/{type}/{uniqueName}", "/{organism}/{type}/{uniqueName}/{name}"
    public LocatedFeature getInfo(
            @RequestParam("uniqueName") String uniqueName, 
            @RequestParam(value="organism",required=false) String organism, 
            @RequestParam(value="name",required=false) String name,
            @RequestParam(value="type",required=false) String type) {
        
        
        Integer organism_id =  null;
        if (organism != null) {
            Organism o = util.getOrganism(organism);
            if (o != null) 
                organism_id = o.ID;
        }
        
        LocatedFeature resultFeature = featureMapper.getOfType(uniqueName, organism_id, name, type);
        
        summarise(resultFeature);
        
        return resultFeature;
        
        //return featuresMapper.pubs(features);
    }
    
    @ResourceDescription("Return feature dbxrefs")
    @RequestMapping(method=RequestMethod.GET, value="/dbxrefs")
    public List<Dbxref> dbxrefs(
            @RequestParam(value="feature") String featureUniqueName, 
            @RequestParam(value="organism",required=false) String organism, 
            @RequestParam(value="name",required=false) String name) {
        
        Feature feature = util.getFeature(featureUniqueName, name, organism);
        return featureMapper.dbxrefs(feature);
    }
    
    private void summarise (Feature feature) {
        
        feature.coordinates = featureMapper.coordinates(feature);
        
        // TODO - this might need to be fixed to work with non-LocatedFeature instances
        if (feature instanceof LocatedFeature 
                && feature.coordinates != null 
                && feature.coordinates.size() > 0) {
            LocatedFeature locatedFeature = (LocatedFeature) feature;
            Coordinates c = locatedFeature.coordinates.get(0);
            locatedFeature.fmin = c.fmin;
            locatedFeature.fmax = c.fmax;
            locatedFeature.region = c.region;
            locatedFeature.phase = c.phase;
            locatedFeature.strand = c.strand;
            
        }
        
        feature.properties = featureMapper.properties(feature);
        feature.terms = featureMapper.terms(feature);
        feature.synonyms = featureMapper.synonyms(feature);
        feature.pubs = featureMapper.pubs(feature);
        feature.dbxrefs = featureMapper.dbxrefs(feature);
        feature.domains = featureMapper.domains(feature);
        feature.orthologues = featureMapper.orthologues(feature);
        
    }
    
    
    
    @RequestMapping(method=RequestMethod.GET, value="/parents")
    public List<Feature> parents( 
            @RequestParam("feature") String featureUniqueName, 
            @RequestParam(value="organism",required=false) String organism, 
            @RequestParam(value="name",required=false) String name,
            @RequestParam(value="relationships", required=false) String[] relationships) throws CrawlException {
        
        if (relationships == null || relationships.length < 1) 
            relationships = defaultRelationshipTypes;
        
        List<Cvterm> relationshipTerms = getRelationshipTypes(Arrays.asList(relationships), terms);
        Feature feature = util.getFeature(featureUniqueName, name, organism);
        return featureMapper.parents(feature, relationshipTerms);
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/children")
    public List<Feature> children( 
            @RequestParam("feature") String featureUniqueName, 
            @RequestParam(value="organism",required=false) String organism, 
            @RequestParam(value="name",required=false) String name,
            @RequestParam(value="relationships", required=false) String[] relationships) throws CrawlException {
        
        if (relationships == null || relationships.length < 1) 
            relationships = defaultRelationshipTypes;
        
        List<Cvterm> relationshipTerms = getRelationshipTypes(Arrays.asList(relationships), terms);
        Feature feature = util.getFeature(featureUniqueName, name, organism);
        return featureMapper.children(feature, relationshipTerms);
    }
    
    
    @ResourceDescription("Returns the hierarchy of a feature (i.e. the parent/child relationship graph), but routed on the feature itself (rather than Gene).")
    @RequestMapping(method=RequestMethod.GET, value="/hierarchy")
    public Feature hierarchy( 
            @RequestParam("uniqueName") String uniqueName, 
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
        
        Feature feature = util.getFeature(uniqueName, name, organism);
        
        
        Feature hierarchyRoot = util.getAncestorGene(feature, ofType);
        hierarchyRoot.organism = this.organismsMapper.getByID(hierarchyRoot.organism_id);
        
        if (hierarchyRoot == null)
            hierarchyRoot = feature;
        
        util.getDescendants(hierarchyRoot, ofType, includeSummaries);
        
        return hierarchyRoot;
        
    }
    
    

    @ResourceDescription("Return features located on features")
    @RequestMapping(method=RequestMethod.GET, value="/locations")
    public List<LocatedFeature> locations(@RequestParam("feature") String  feature ) {
        return this.featuresMapper.locations(feature);
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/domains")
    public List<LocatedFeature> domains(
            @RequestParam("feature") String featureUniqueName, 
            @RequestParam(value="organism",required=false) String organism, 
            @RequestParam(value="name",required=false) String name) {
        List<LocatedFeature> domains = new ArrayList<LocatedFeature>();
        
        Feature feature = util.getFeature(featureUniqueName, name, organism);
        return featureMapper.domains(feature);
        
    }
    
    
    @ResourceDescription("Return feature dbxrefs")
    @RequestMapping(method = RequestMethod.GET, value = "/polypeptide_properties")
    public List<Property> getPolypeptideProperties(
            @RequestParam(value = "feature") String featureUniqueName, 
            @RequestParam(value = "organism", required = false) String organism, 
            @RequestParam(value = "name", required = false) String name) throws BioException, TranslationException {
    
        Feature feature = util.getFeature(featureUniqueName, name, organism);
        
        // assemble a hierarchy for this feature
        List<Cvterm> ofType = getRelationshipTypes(Arrays.asList(defaultRelationshipTypes), terms);
        Feature geneFeature = util.getAncestorGene(feature, ofType);
        util.getDescendants(geneFeature, ofType, false);
        
        return util.getPolypeptideProperties(feature, geneFeature);
        
        
    }
    
    
}