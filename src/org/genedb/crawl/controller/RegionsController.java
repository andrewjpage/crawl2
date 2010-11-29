package org.genedb.crawl.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.genedb.crawl.CrawlException;
import org.genedb.crawl.annotations.ResourceDescription;
import org.genedb.crawl.model.LocationBoundaries;
import org.genedb.crawl.model.Locations;
import org.gmod.cat.Features;
import org.gmod.cat.Regions;
import org.gmod.cat.Terms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/regions")
@ResourceDescription("Provides queries related to large genomic regions such as chromosomes or contigs")
public class RegionsController extends BaseQueryController {
	
	private Logger logger = Logger.getLogger(RegionsController.class);
	
	@Autowired
	Regions regions;
	
	@Autowired
	Terms terms;
	
	@Autowired
	Features features;
	
	/**
	 * FIXME the exclude parameter works in this form:
	 * 	&exclude=repeat_region&exclude=gene
	 * 
	 * but not this form :
	 * 
	 * 	&exclude[]=repeat_region&exclude[]=gene
	 * 
	 * which jqquery would typically send.
	 * 
	 * 
	 * @param region
	 * @param start
	 * @param end
	 * @param exclude
	 * @return
	 * @throws CrawlException
	 */
	@RequestMapping(method=RequestMethod.GET, value={"/locations", "/locations.*"})
	@ResourceDescription("Returns features and their locations on a region of interest")
	public Locations locations(
			@RequestParam("region") String region, 
			@RequestParam("start") int start, 
			@RequestParam("end") int end, 
			@RequestParam(value="exclude", required=false) @ResourceDescription("A list of features to exclude.") String[] exclude) throws CrawlException {
		
		
		int regionID = features.getFeatureID(region);
		
		logger.info(String.format("Getting locations for %s (%d).", region, regionID));
				
		// trying to speed up the boundary query by determining the types in advance
        List<Integer> geneTypes = terms.getCvtermIDs("sequence", new String[] {"gene", "pseudogene"});
        
        logger.info("Gene Types " + geneTypes);
        
        LocationBoundaries expandedBoundaries = regions.locationsMinAndMaxBoundaries(regionID, start, end, geneTypes);
        
		int actualStart = start;
		if (expandedBoundaries.start != null && expandedBoundaries.start < start) {
			actualStart = expandedBoundaries.start;
		}
		
		int actualEnd = end;
		if (expandedBoundaries.end != null &&expandedBoundaries.end > end) {
			actualEnd = expandedBoundaries.end;
		}

		Locations locations = new Locations();
			
		locations.features = regions.locations(regionID, actualStart, actualEnd, exclude);
		
		locations.actual_start = actualStart;
		locations.actual_end = actualEnd;
		locations.exclude = exclude;
		locations.region = region;
		locations.request_start = start;
		locations.request_end = end;
		
		return locations;
        
	}
	
	
	
}
