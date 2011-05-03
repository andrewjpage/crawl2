package org.genedb.crawl.controller;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.genedb.crawl.annotations.ResourceDescription;
import org.genedb.crawl.bam.BioDataFileStore;
import org.genedb.crawl.bam.BioDataFileStoreInitializer;
import org.genedb.crawl.mappers.OrganismsMapper;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.ResultsVariants;
import org.genedb.crawl.model.Variant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@ResourceDescription("Provides methods for VCF/BCF variant querying.")
@RequestMapping("/variants")
public class VariantController extends BaseQueryController {
	
	private Logger logger = Logger.getLogger(VariantController.class);

	private BioDataFileStore<Variant> variantStore;
	
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
	
	@ResourceDescription("Queries a region of a variant file.")
	@RequestMapping(method=RequestMethod.GET, value={"/query", "/query.*"})
	public ResultsVariants query(
			ResultsVariants results, 
			@RequestParam("fileID") int fileID, 
			@RequestParam("sequence") String sequence, 
			@RequestParam("start") int start, 
			@RequestParam("end") int end) throws IOException {
		results.records = variantStore.getFile(fileID).getReader().query(sequence, start, end);
		return results;
	}
}
