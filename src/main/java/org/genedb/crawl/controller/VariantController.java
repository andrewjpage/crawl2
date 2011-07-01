package org.genedb.crawl.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.genedb.crawl.annotations.ResourceDescription;
import org.genedb.crawl.bam.BioDataFileStore;
import org.genedb.crawl.bam.BioDataFileStoreInitializer;
import org.genedb.crawl.mappers.RegionsMapper;
import org.genedb.crawl.mappers.OrganismsMapper;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.LocationBoundaries;
import org.genedb.crawl.model.MappedSAMSequence;
import org.genedb.crawl.model.MappedVCFRecord;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.Sequence;
import org.genedb.crawl.model.Variant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.sanger.artemis.components.variant.GeneFeature;
import uk.ac.sanger.artemis.components.variant.VariantFilterOption;
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
	public List<Variant> list() throws IOException {
		return variantStore.getFiles();
	}
	
	@ResourceDescription("Returns a list of sequences in a variant file.")
	@RequestMapping(method=RequestMethod.GET, value={"/sequences", "/sequences.*"})
	public List<MappedSAMSequence> sequences(
			@RequestParam("fileID") int fileID) throws IOException {
		return variantStore.getSequences(fileID);
	}
	
	@ResourceDescription("Returns a list of variant files for a particular organism.")
	@RequestMapping(method=RequestMethod.GET, value={"/listfororganism", "/listfororganism.*"})
	public List<Variant> listfororganism( 
			@RequestParam("organism") String organism) throws IOException {
		
		Organism mappedOrganism = getOrganism(organismsMapper, organism);
		return variantStore.listfororganism(mappedOrganism.common_name);
	}
	
	@ResourceDescription("Returns a list of variant files for a particular sequence.")
	@RequestMapping(method=RequestMethod.GET, value={"/listforsequence", "/listforsequence.*"})
	public List<Variant> listforsequence( 
			@RequestParam("sequence") String sequence) throws Exception {
		return variantStore.listforsequence(sequence);
	}
	
	private static final List<String> geneTypes = Arrays.asList(new String[]{"gene", "pseudogene"});
	
	@ResourceDescription("Queries a region of a variant file.")
	@RequestMapping(method=RequestMethod.GET, value={"/query", "/query.*"})
	public List<MappedVCFRecord> query(
			@RequestParam("fileID") int fileID, 
			@RequestParam("sequence") String sequence, 
			@RequestParam("start") int start, 
			@RequestParam("end") int end,
			@RequestParam(value="filter", required=false) Integer filter) throws IOException {
		return doQuery(fileID,sequence,start,end,filter);
	}
	
	@ResourceDescription("Queries a region of a variant file, passing a list of VariantFilterOption filters as a parameter. Current valid values are SHOW_SYNONYMOUS, SHOW_NON_SYNONYMOUS, SHOW_DELETIONS, SHOW_INSERTIONS, SHOW_MULTI_ALLELES, SHOW_NON_OVERLAPPINGS, SHOW_NON_VARIANTS, MARK_NEW_STOPS.")
	@RequestMapping(method=RequestMethod.GET, value={"/queryWithFilters", "/queryWithFilters.*"})
	public List<MappedVCFRecord> queryWithFilters(
			@RequestParam("fileID") int fileID, 
			@RequestParam("sequence") String sequence, 
			@RequestParam("start") int start, 
			@RequestParam("end") int end,
			@RequestParam(value="filters", required=true) List<String> filters) throws IOException {
		Integer filter = null;
		if (filters.size() > 0) {
			filter = 0;
			for (String f : filters) {
				VariantFilterOption option = VariantFilterOption.valueOf(f);
				filter += option.index();			
			}
		}
		return doQuery(fileID,sequence,start,end,filter);
	}
	
	private List<MappedVCFRecord> doQuery(int fileID, String sequence, int start, int end, Integer filter) throws IOException {
		VariantFilterOptions options = new VariantFilterOptions(filter);
		
		logger.info(String.format("Filter %d, values: %s ", filter, options.toString()));
		
		String alignmentName = variantStore.getAlignmentFromName(sequence);
		String referenceName = variantStore.getReferenceFromName(sequence);
		
		logger.info(String.format("sequence name supplied: %s, alignment sequence name used: %s, reference sequence name used: %s", sequence, alignmentName, referenceName));
		
		Sequence regionSequence = regionsMapper.sequence(referenceName);
		List<GeneFeature> geneFeatures = getGenesAt(referenceName, start, end, regionsMapper); 
		
		return variantStore.getFile(fileID).getReader().query(alignmentName, start, end, geneFeatures, options, regionSequence);
	}
	
	public static List<GeneFeature> getGenesAt(String sequence, int start, int end, RegionsMapper regionsMapper) {
		
		List<GeneFeature> geneFeatures = new ArrayList<GeneFeature>(); 
		
		LocationBoundaries boundaries = regionsMapper.locationsMinAndMaxBoundaries(sequence, start, end, false, geneTypes);
		
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
