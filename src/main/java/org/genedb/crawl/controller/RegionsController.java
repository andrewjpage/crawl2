package org.genedb.crawl.controller;

import java.util.List;

import javax.jws.WebService;

import org.genedb.crawl.CrawlException;
import org.genedb.crawl.annotations.ResourceDescription;
import org.genedb.crawl.dao.RegionsDAO;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.Sequence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/regions")
@ResourceDescription("Provides queries related to large genomic regions such as chromosomes or contigs")
@WebService(serviceName="regions")
public class RegionsController extends BaseController implements RegionsDAO {
	
	
	@Autowired
	RegionsDAO dao;
	
	/**
     * The exclude parameter works in this form:
     *  &exclude=repeat_region&exclude=gene
     * 
     * but not this form :
     * 
     *  &exclude[]=repeat_region&exclude[]=gene
     * 
     * which JQuery would typically send. I think we can resolve this by setting 
     * 
     *  jQuery.ajaxSettings.traditional = true;
     * 
     * or
     * 
     *  $.ajaxSetup({ traditional: true }); 
     * 
     * in Web-Artemis.
     * 
     * 
     * @param region
     * @param start
     * @param end
     * @param exclude
     * @return
     * @throws CrawlException
     */
	@Override
    @RequestMapping(method=RequestMethod.GET, value={"/locations", "/locations.*"})
	@ResourceDescription("Returns features and their locations on a region of interest")
	public List<LocatedFeature> locations(
			@RequestParam("region") String region, 
			@RequestParam(value="start",required=false) Integer start, 
			@RequestParam(value="end", required=false) Integer end, 
			@RequestParam(value="exclude", required=false) Boolean exclude,
			@RequestParam(value="types", required=false) @ResourceDescription("A list of features types to exclude or include.") List<String> types
			) throws CrawlException {
		return dao.locations(region, start, end, exclude, types);
	}
	
	
	@Override
    @RequestMapping(method=RequestMethod.GET, value="/sequenceLength")
	@ResourceDescription("Returns the sequence on a region.")
	public List<Sequence>  sequenceLength(
			@RequestParam("region") String region) {
		return dao.sequenceLength(region);
	}
	
	@Override
    @RequestMapping(method=RequestMethod.GET, value="/sequence")
	@ResourceDescription("Returns the sequence on a region.")
	public List<Sequence>  sequence(
			@RequestParam("region") String region, 
			@RequestParam(value="start", required=false) Integer start, 
			@RequestParam(value="end", required=false) Integer end) {
		return dao.sequence(region, start, end);
	}
	
	@Override
    @RequestMapping(method=RequestMethod.GET, value="getInfo")
	@ResourceDescription("Returns the region's information.")
	public Feature getInfo( 
			@RequestParam(value="uniqueName") String uniqueName, 
			@RequestParam(value="name", required=false) String name,
			@RequestParam(value="organism", required=false) String organism) throws CrawlException {
		return dao.getInfo(uniqueName, name, organism);
	}
	
	@Override
    @RequestMapping(method=RequestMethod.GET, value="inorganism")
	@ResourceDescription("Returns the regions in an organism.")
	public List<Feature> inorganism( 
			@RequestParam("organism") String organism,
			@RequestParam(value="limit", required=false) Integer limit, 
			@RequestParam(value="offset", required=false) Integer offset,
			@RequestParam(value="type", required=false) String type) throws CrawlException {
		return dao.inorganism(organism, limit, offset, type);
	}
	
	@Override
    @RequestMapping(method=RequestMethod.GET, value="typesinorganism")
	@ResourceDescription("Returns the types of region present in an organism.")
	public List<Feature> typesInOrganism( 
			@RequestParam("organism") String organism
			) throws CrawlException {
		return dao.typesInOrganism(organism);
	}
	
	
}
