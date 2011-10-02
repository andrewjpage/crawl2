package org.genedb.crawl.controller;

import java.util.Date;
import java.util.List;

import javax.jws.WebService;

import org.genedb.crawl.CrawlException;
import org.genedb.crawl.annotations.ResourceDescription;
import org.genedb.crawl.controller.BaseController;
import org.genedb.crawl.dao.FeaturesDAO;
import org.genedb.crawl.model.BlastPair;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.Gene;
import org.genedb.crawl.model.HierarchicalFeature;
import org.genedb.crawl.model.Statistic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/features")
@ResourceDescription("Feature related queries")
@WebService(serviceName="features")
public class FeaturesController extends BaseController implements FeaturesDAO {
	
    @Autowired
    FeaturesDAO dao;
    
	@Override
    @ResourceDescription("Get a feature's gene")
	@RequestMapping(method=RequestMethod.GET, value="/genes")
	public List<Feature> genes(@RequestParam(value="features") List<String> features) {
	    return dao.genes(features);
	}
	
	@Override
    @ResourceDescription("Returns the hierarchy of a feature (i.e. the parent/child relationship graph), but routed on the feature itself (rather than Gene).")
	@RequestMapping(method=RequestMethod.GET, value="/hierarchy")
	public List<HierarchicalFeature> hierarchy( 
			@RequestParam("features") List<String> features, 
			@RequestParam(value="root_on_genes", defaultValue="false", required=false) Boolean root_on_genes,
			@RequestParam(value="relationships", required=false) String[] relationships) throws CrawlException {
		return dao.hierarchy(features, root_on_genes, relationships);
	}
	
	@Override
    @ResourceDescription("Returns coordinages of a feature if located on a region.")
	@RequestMapping(method=RequestMethod.GET, value="/coordinates")
	public List<Feature> coordinates(
			@RequestParam("features") List<String> features, 
			@RequestParam(value="region", required=false) String region ) {
	    return dao.coordinates(features, region);
	}
	
	@Override
    @ResourceDescription("Returns a feature's synonyms.")
	@RequestMapping(method=RequestMethod.GET, value="/synonyms")
	public List<Feature> synonyms(
			@RequestParam("features") List<String> features,
			@RequestParam(value="types", required=false) List<String> types) {
	    return dao.synonyms(features, types);
	}
	
	@Override
    @ResourceDescription("Return matching features")
	@RequestMapping(method=RequestMethod.GET, value="/withnamelike")
	public List<Feature> withnamelike( 
			@RequestParam("term") String term,
			@RequestParam(value="regex", defaultValue="false") boolean regex, 
			@RequestParam(value="region", required=false) String region) {
	    return dao.withnamelike(term, regex, region);
	}
	
	
	@Override
    @ResourceDescription("Return feature properties")
	@RequestMapping(method=RequestMethod.GET, value="/properties")
	public List<Feature> properties(
			@RequestParam(value="features") List<String> features, 
			@RequestParam(value="types", required=false) List<String> types) {
		return dao.properties(features, types);
	}
	
	@Override
    @ResourceDescription("Return feature properties")
	@RequestMapping(method=RequestMethod.GET, value="/withproperty")
	public List<Feature> withproperty( 
			@RequestParam("value") String value,
			@RequestParam(value="regex", defaultValue="false") boolean regex, 
			@RequestParam(value="region", required=false) String region,
			@RequestParam(value="type", required=false) String type) {
		return dao.withproperty(value, regex, region, type);
	}
	
	@Override
    @ResourceDescription("Return feature pubs")
	@RequestMapping(method=RequestMethod.GET, value="/pubs")
	public List<Feature> pubs(@RequestParam(value="features") List<String> features) {
	    return dao.pubs(features);
	}
	
	@Override
    @ResourceDescription("Return feature dbxrefs")
	@RequestMapping(method=RequestMethod.GET, value="/dbxrefs")
	public List<Feature> dbxrefs(@RequestParam(value="features") List<String> features) {
	    return dao.dbxrefs(features);
	}
	
	
	@Override
    @ResourceDescription(value="Return feature cvterms")
	@RequestMapping(method=RequestMethod.GET, value="/terms")
	public List<Feature> terms(@RequestParam(value="features") List<String> features, @RequestParam(value="cvs", required=false) List<String> cvs) {
	    return dao.terms(features, cvs);
	}
	
	@Override
    @ResourceDescription("Return feature with specified cvterm")
	@RequestMapping(method=RequestMethod.GET, value="/withterm")
	public List<Feature> withterm(
			@RequestParam(value="term") String term, 
			@RequestParam(value="cv", required=false) String cv,
			@RequestParam(value="regex", defaultValue="false") boolean regex, 
			@RequestParam(value="region", required=false) String region) {
		return dao.withterm(term, cv, regex, region);
	}
	
	@Override
    @ResourceDescription("Return feature orthologues")
	@RequestMapping(method=RequestMethod.GET, value="/orthologues")
	public List<Feature> orthologues(@RequestParam(value="features") List<String> features) {
	    return dao.orthologues(features);
	}
	
	@Override
    @ResourceDescription(value="Return feature clusters")
	@RequestMapping(method=RequestMethod.GET, value="/clusters")
	public List<Feature> clusters(@RequestParam(value="features") List<String> features) {
	    return dao.clusters(features);
	}
	
	@Override
    @ResourceDescription(value="Return features that have had annotation changes")
	@RequestMapping(method=RequestMethod.GET, value="/annotation_changes")
	public List<Feature> annotationModified( 
			@RequestParam(value="date") Date date, 
			@RequestParam("organism") String organism, 
			@RequestParam(value="region", required = false) String region) throws CrawlException {
	    return dao.annotationModified(date, organism, region);
	}
	
	@Override
    @ResourceDescription(value="Return features that have had annotation changes")
	@RequestMapping(method=RequestMethod.GET, value="/annotation_changes_statistics")
	public List<Statistic> annotationModifiedStatistics( 
			@RequestParam(value="date") Date date, 
			@RequestParam("organism") String organism, 
			@RequestParam(value="region", required = false) String region) throws CrawlException {
	    return dao.annotationModifiedStatistics(date, organism, region);
	}
	
	
	@Override
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
	    return dao.blastpair(f1, start1, end1, f2, start2, end2, length, score);
	}
	
	@Override
    @ResourceDescription("Return a gene's transcripts")
	@RequestMapping(method=RequestMethod.GET, value="/transcripts")
	public List<Gene> transcripts(
	        @RequestParam(value="gene") String gene, 
	        @RequestParam(value="exons") boolean exons) {
	    return dao.transcripts(gene, exons);
	}
	
	
    
}
