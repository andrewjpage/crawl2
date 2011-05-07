package org.genedb.crawl.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.genedb.crawl.annotations.ResourceDescription;
import org.genedb.crawl.bam.BioDataFileStore;
import org.genedb.crawl.bam.BioDataFileStoreInitializer;
import org.genedb.crawl.mappers.RegionsMapper;
import org.genedb.crawl.mappers.OrganismsMapper;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.LocationBoundaries;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.ResultsVariants;
import org.genedb.crawl.model.Sequence;
import org.genedb.crawl.model.Variant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.sanger.artemis.components.variant.GeneFeature;
import uk.ac.sanger.artemis.components.variant.VariantFilterOptions;

@Controller
@ResourceDescription("Provides methods for VCF/BCF variant querying.")
@RequestMapping("/variants")
public class VariantController extends BaseQueryController {
	
	private static Logger logger = Logger.getLogger(VariantController.class);
	
	private BioDataFileStore<Variant> variantStore;
	
	@Autowired
	private RegionsMapper regionsMapper;
	
	@Autowired
	private OrganismsMapper organismsMapper;
	
	@Autowired
	public void setInitializer(BioDataFileStoreInitializer initializer) {
		variantStore=initializer.getVariants();
	}
	
	@ResourceDescription("Returns the complete list of variant files.")
	@RequestMapping(method=RequestMethod.GET, value={"/list", "/list.*"})
	public ResultsVariants list(ResultsVariants results) throws IOException {
		results.files = variantStore.getFiles();
		return results;
	}
	
	@ResourceDescription("Returns a list of sequences in a variant file.")
	@RequestMapping(method=RequestMethod.GET, value={"/sequences", "/sequences.*"})
	public ResultsVariants sequences(
			ResultsVariants results, 
			@RequestParam("fileID") int fileID) throws IOException {
		results.sequences = variantStore.getSequences(fileID);
		return results;
	}
	
	@ResourceDescription("Returns a list of variant files for a particular organism.")
	@RequestMapping(method=RequestMethod.GET, value={"/listfororganism", "/listfororganism.*"})
	public ResultsVariants listfororganism(
			ResultsVariants results, 
			@RequestParam("organism") String organism) throws IOException {
		
		Organism mappedOrganism = getOrganism(organismsMapper, organism);
		results.files = variantStore.listfororganism(mappedOrganism.common_name);
		logger.info(results.files);
		return results;
		
	}
	
	@ResourceDescription("Returns a list of variant files for a particular sequence.")
	@RequestMapping(method=RequestMethod.GET, value={"/listforsequence", "/listforsequence.*"})
	public ResultsVariants listforsequence( 
			ResultsVariants results,
			@RequestParam("sequence") String sequence) throws Exception {
		results.files = variantStore.listforsequence(sequence);
		return results;
	}
	
	private static final List<String> geneTypes = Arrays.asList(new String[]{"gene", "pseudogene"});
	
	@ResourceDescription("Queries a region of a variant file.")
	@RequestMapping(method=RequestMethod.GET, value={"/query", "/query.*"})
	public ResultsVariants query(
			ResultsVariants results, 
			@RequestParam("fileID") int fileID, 
			@RequestParam("sequence") String sequence, 
			@RequestParam("start") int start, 
			@RequestParam("end") int end,
			@RequestParam(value="filter", required=false) Integer filter) throws IOException {
		
		
		VariantFilterOptions options = new VariantFilterOptions(filter);
		
		String alignmentName = variantStore.getAlignmentFromName(sequence);
		String referenceName = variantStore.getReferenceFromName(sequence);
		
		
		logger.info(String.format("sequence name supplied: %s, alignment sequence name used: %s, reference sequence name used: %s", sequence, alignmentName, referenceName));
		
		Sequence regionSequence = regionsMapper.sequence(referenceName);
		List<GeneFeature> geneFeatures = getGenesAt(referenceName, start, end, regionsMapper); 
		
		
		results.records = variantStore.getFile(fileID).getReader().query(alignmentName, start, end, geneFeatures, options, regionSequence);
		return results;
	}
	
	public static List<GeneFeature> getGenesAt(String sequence, int start, int end, RegionsMapper regionsMapper) {
		
		List<GeneFeature> geneFeatures = new ArrayList<GeneFeature>(); 
		
		LocationBoundaries boundaries = regionsMapper.locationsMinAndMaxBoundaries(sequence, start, end, geneTypes);
		
		if (boundaries == null) {
			return geneFeatures;
		}
		
		logger.info(boundaries);
		logger.info(boundaries.start + " --- " + boundaries.end);
		
		List<LocatedFeature> features = regionsMapper.locations(sequence, boundaries.start, boundaries.end, false, geneTypes);
		
		if (features == null) {
			return geneFeatures;
		}
		
		for (LocatedFeature feature : features) {
			logger.debug("feature??" + feature.uniqueName + " " + feature.region + ":" + feature.fmin + ":" + feature.fmax);
			geneFeatures.add(new GeneFeature(feature, regionsMapper));
		}
		return geneFeatures;
	}
}
