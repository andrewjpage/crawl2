package org.genedb.crawl.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.genedb.crawl.CrawlException;
import org.genedb.crawl.annotations.ResourceDescription;
import org.genedb.crawl.model.LocationBoundaries;
import org.genedb.crawl.model.Locations;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.RegionsInOrganism;
import org.genedb.crawl.model.Sequence;
import org.gmod.cat.Features;
import org.gmod.cat.Organisms;
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
	
	@Autowired
	Organisms organisms;
	
	private boolean cacheRegionsOnStartup = false;
	private Map<String, List<String>> organismRegionMap = new HashMap<String, List<String>>();
	
	/**
	 * Force the controller to cache all organism regions on startup.
	 * @param cacheRegionsOnStartup
	 */
	public void setCacheRegionsOnStartup(boolean cacheRegionsOnStartup) {
		this.cacheRegionsOnStartup = cacheRegionsOnStartup;
	}
	
	@PostConstruct
	void setup() throws CrawlException {
		if (! cacheRegionsOnStartup) {
			return;
		}
		for (Organism o : organisms.list()) {
			List<String> r = regions.inorganism( Integer.parseInt(o.ID));
			Collections.sort(r);
			organismRegionMap.put(o.ID, r);
			logger.info(String.format("Cached %s.", o.common_name));
		}
	}
	
	/**
	 * The exclude parameter works in this form:
	 * 	&exclude=repeat_region&exclude=gene
	 * 
	 * but not this form :
	 * 
	 * 	&exclude[]=repeat_region&exclude[]=gene
	 * 
	 * which JQuery would typically send. I think we can resolve this by setting 
	 * 
	 * 	jQuery.ajaxSettings.traditional = true;
	 * 
	 * or
	 * 
	 * 	$.ajaxSetup({ traditional: true }); 
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
	@RequestMapping(method=RequestMethod.GET, value={"/locations", "/locations.*"})
	@ResourceDescription("Returns features and their locations on a region of interest")
	public Locations locations(
			@RequestParam("region") String region, 
			@RequestParam("start") int start, 
			@RequestParam("end") int end, 
			@RequestParam(value="exclude", required=false) @ResourceDescription("A list of features to exclude.") String[] excludeNormalNotation,
			@RequestParam(value="exclude[]", required=false) @ResourceDescription("A list of features to exclude.") String[] excludeArrayNotation) throws CrawlException {
		
		String[] exclude = mergeArrays(new String[][]{excludeArrayNotation, excludeNormalNotation});
		
		int regionID = features.getFeatureID(region);
		
		logger.info(String.format("Getting locations for %s (%d).", region, regionID));
				
		// trying to speed up the boundary query by determining the types in advance
        List<Integer> geneTypes = terms.getCvtermIDs("sequence", new String[] {"gene", "pseudogene"});
        
        logger.info("Gene Types " + geneTypes);
        
        int actualStart = start;
        int actualEnd = end;
        
        LocationBoundaries expandedBoundaries = regions.locationsMinAndMaxBoundaries(regionID, start, end, geneTypes);
        if (expandedBoundaries != null) {
			if (expandedBoundaries.start != null && expandedBoundaries.start < start) {
				actualStart = expandedBoundaries.start;
			}
			if (expandedBoundaries.end != null &&expandedBoundaries.end > end) {
				actualEnd = expandedBoundaries.end;
			}
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
	
	
	@RequestMapping(method=RequestMethod.GET, value="/sequence")
	@ResourceDescription("Returns the sequence on a region.")
	public List<Sequence> sequence(
			@RequestParam("region") String region, 
			@RequestParam("start") int start, 
			@RequestParam("end") int end) {
		
		List<Sequence> sequences = new ArrayList<Sequence>();
		
		int regionID = features.getFeatureID(region);
		String sequenceResidues = regions.sequence(regionID);
		int length = sequenceResidues.length();
		
		if (length == 0) {
			return sequences;
		}
		
		int lastResiduePosition = length -1;
		
		int actualStart = start -1;
		int actualEnd = end -1;
		
		if (actualStart > lastResiduePosition || actualStart > actualEnd) {
			return sequences;
		}
		
		if (actualEnd > lastResiduePosition) {
			actualEnd = lastResiduePosition;
		}
		
		String dna = sequenceResidues.substring(actualStart, actualEnd);
		
		Sequence sequence = new Sequence();
		sequence.dna = dna;
		sequence.start = start;
		sequence.end = end;
		sequence.length = length;
		sequence.region = region;
		
		sequences.add(sequence);
		
		return sequences;
	}
	
	@RequestMapping(method=RequestMethod.GET, value="inorganism")
	@ResourceDescription("Returns the sequence on a region.")
	public RegionsInOrganism inorganism(@RequestParam("organism") String organism) throws CrawlException {
		
		Organism o = getOrganism(organisms, organism);
		
		List<String> r = null;
		if (organismRegionMap.containsKey(o.ID)) {
			r = organismRegionMap.get(o.ID);
		} else {
			r = regions.inorganism( Integer.parseInt(o.ID));
			Collections.sort(r);
			organismRegionMap.put(o.ID, r);
		}
		
		RegionsInOrganism rio = new RegionsInOrganism();
		rio.organism = o;
		rio.regions = r;
		
		return rio;
	}
	
	
}
