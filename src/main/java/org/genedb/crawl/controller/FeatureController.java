package org.genedb.crawl.controller;

import java.util.List;

import javax.jws.WebService;

import org.biojava.bio.BioException;
import org.genedb.crawl.CrawlException;
import org.genedb.crawl.annotations.ResourceDescription;
import org.genedb.crawl.controller.BaseController;
import org.genedb.crawl.dao.FeatureDAO;
import org.genedb.crawl.model.Dbxref;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.Property;
import org.genedb.crawl.model.Synonym;

import org.genedb.crawl.model.LocatedFeature;
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
public class FeatureController extends BaseController implements FeatureDAO {
    
    @Autowired
    FeatureDAO dao;
    
    @Override
    @ResourceDescription("Return a gene's information")
    @RequestMapping(method=RequestMethod.GET, value="/info") 
    public Feature get(
            @RequestParam("uniqueName") String uniqueName, 
            @RequestParam(value="organism",required=false) String organism, 
            @RequestParam(value="name",required=false) String name,
            @RequestParam(value="type",required=false) String type) {
        return dao.get(uniqueName, organism, name, type);
    }
    
    @Override
    @ResourceDescription("Return feature dbxrefs")
    @RequestMapping(method=RequestMethod.GET, value="/dbxrefs")
    public List<Dbxref> dbxrefs(
            @RequestParam(value="uniqueName") String featureUniqueName, 
            @RequestParam(value="organism",required=false) String organism, 
            @RequestParam(value="name",required=false) String name) {
        return dao.dbxrefs(featureUniqueName, organism, name);
    }
    
    
    @Override
    @RequestMapping(method=RequestMethod.GET, value="/parents")
    public List<Feature> parents( 
            @RequestParam("uniqueName") String featureUniqueName, 
            @RequestParam(value="organism",required=false) String organism, 
            @RequestParam(value="name",required=false) String name,
            @RequestParam(value="relationships", required=false) String[] relationships) throws CrawlException {
        return dao.parents(featureUniqueName, organism, name, relationships);
    }
    
    @Override
    @RequestMapping(method=RequestMethod.GET, value="/children")
    public List<Feature> children( 
            @RequestParam("uniqueName") String featureUniqueName, 
            @RequestParam(value="organism",required=false) String organism, 
            @RequestParam(value="name",required=false) String name,
            @RequestParam(value="relationships", required=false) String[] relationships) throws CrawlException {
        return dao.children(featureUniqueName, organism, name, relationships);
    }
    
    
    @Override
    @ResourceDescription("Returns the hierarchy of a feature (i.e. the parent/child relationship graph), but routed on the feature itself (rather than Gene).")
    @RequestMapping(method=RequestMethod.GET, value="/hierarchy")
    public Feature hierarchy( 
            @RequestParam("uniqueName") String uniqueName, 
            @RequestParam(value="organism",required=false) String organism, 
            @RequestParam(value="name",required=false) String name,
            @RequestParam(value="relationships", required=false) String[] relationships,
            @RequestParam(value="includeSummaries", required=false) Boolean includeSummaries
            ) throws CrawlException {
        return dao.hierarchy(uniqueName, organism, name, relationships, includeSummaries);
    }
    
    @Override
    @ResourceDescription("Return features located on features")
    @RequestMapping(method=RequestMethod.GET, value="/locations")
    public List<LocatedFeature> locations(@RequestParam("uniqueName") String  feature ) {
        return dao.locations(feature);
    }
    
    @Override
    @RequestMapping(method=RequestMethod.GET, value="/domains")
    public List<LocatedFeature> domains(
            @RequestParam("uniqueName") String featureUniqueName, 
            @RequestParam(value="organism",required=false) String organism, 
            @RequestParam(value="name",required=false) String name) {
        return dao.domains(featureUniqueName, organism, name);
    }
    
    
    @Override
    @ResourceDescription("Return feature dbxrefs")
    @RequestMapping(method = RequestMethod.GET, value = "/polypeptide_properties")
    public List<Property> getPolypeptideProperties(
            @RequestParam(value = "uniqueName") String featureUniqueName, 
            @RequestParam(value = "organism", required = false) String organism, 
            @RequestParam(value = "name", required = false) String name) throws BioException, TranslationException {
        return dao.getPolypeptideProperties(featureUniqueName, organism, name);
    }

    @Override
    @ResourceDescription("Return feature synonyms")
    @RequestMapping(method = RequestMethod.GET, value = "/synonyms")
    public List<Synonym> synonyms(
            @RequestParam("uniqueName") String uniqueName, 
            @RequestParam(value="organism",required=false) String organism, 
            @RequestParam(value="name",required=false) String name) {
        return dao.synonyms(uniqueName, organism, name);
    }

    @Override
    @ResourceDescription("Returns the isoform unique name")
    @RequestMapping(method = RequestMethod.GET, value = "/isoform")
    public Feature getIsoform(
            @RequestParam("uniqueName") String uniqueName, 
            @RequestParam(value="organism",required=false) String organism, 
            @RequestParam(value="name",required=false) String name) {
        return dao.getIsoform(uniqueName, organism, name);
    }
    
    
}