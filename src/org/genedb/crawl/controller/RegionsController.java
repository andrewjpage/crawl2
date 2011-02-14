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
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.ResultsRegions;
import org.genedb.crawl.model.Sequence;
import org.gmod.cat.FeaturesMapper;
import org.gmod.cat.OrganismsMapper;
import org.gmod.cat.RegionsMapper;
import org.gmod.cat.TermsMapper;

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
	RegionsMapper regionsMapper;
	
	@Autowired
	TermsMapper termsMapper;
	
	@Autowired
	FeaturesMapper featuresMapper;
	
	@Autowired
	OrganismsMapper organismsMapper;
	
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
		for (Organism o : organismsMapper.list()) {
			List<String> r = regionsMapper.inorganism( o.ID );
			Collections.sort(r);
			organismRegionMap.put(String.valueOf(o.ID), r);
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
	public ResultsRegions locations(
			ResultsRegions results,
			@RequestParam("region") String region, 
			@RequestParam("start") int start, 
			@RequestParam("end") int end, 
			@RequestParam(value="exclude", required=false) @ResourceDescription("A list of features to exclude.") List<String> exclude
			) throws CrawlException {
		
		//int regionID = featuresMapper.getFeatureID(region);
		
		logger.info(String.format("Getting locations for %s.", region));
				
		// trying to speed up the boundary query by determining the types in advance
        List<Integer> geneTypes = termsMapper.getCvtermIDs("sequence", new String[] {"gene", "pseudogene"});
        
        logger.info("Gene Types " + geneTypes);
        
        int actualStart = start;
        int actualEnd = end;
        
        LocationBoundaries expandedBoundaries = regionsMapper.locationsMinAndMaxBoundaries(region, start, end, geneTypes);
        if (expandedBoundaries != null) {
			if (expandedBoundaries.start != null && expandedBoundaries.start < start) {
				actualStart = expandedBoundaries.start;
			}
			if (expandedBoundaries.end != null &&expandedBoundaries.end > end) {
				actualEnd = expandedBoundaries.end;
			}
        }
        
		logger.info( String.format("Locating on %s : %s-%s (%s)", region, actualStart, actualEnd, exclude));
		
		results.locations = regionsMapper.locations(region, actualStart, actualEnd, exclude);
		results.actual_end = actualEnd;
		results.actual_start = actualStart;
		
		return results;

	}
	
	
	@RequestMapping(method=RequestMethod.GET, value="/sequence")
	@ResourceDescription("Returns the sequence on a region.")
	public ResultsRegions sequence(
			ResultsRegions results,
			@RequestParam("region") String region, 
			@RequestParam("start") int start, 
			@RequestParam("end") int end) {
		
		List<Sequence> sequences = new ArrayList<Sequence>();
		results.sequences = sequences;
		
		//int regionID = featuresMapper.getFeatureID(region);
		String sequenceResidues = regionsMapper.sequence(region);
		int length = sequenceResidues.length();
		
		if (length == 0) {
			return results;
		}
		
		int lastResiduePosition = length -1;
		
		int actualStart = start -1;
		int actualEnd = end -1;
		
		if (actualStart > lastResiduePosition || actualStart > actualEnd) {
			return results;
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
		
		return results;
	}
	
	@RequestMapping(method=RequestMethod.GET, value="inorganism")
	@ResourceDescription("Returns the sequence on a region.")
	public ResultsRegions inorganism(ResultsRegions results, @RequestParam("organism") String organism) throws CrawlException {
		
		Organism o = getOrganism(organismsMapper, organism);
		
		List<String> r = null;
		if (organismRegionMap.containsKey(o.ID)) {
			r = organismRegionMap.get(o.ID);
		} else {
			r = regionsMapper.inorganism( o.ID);
			Collections.sort(r);
			organismRegionMap.put(String.valueOf(o.ID), r);
		}
		
		results.regions = r;
		
		return results;
	}
	
	
}
