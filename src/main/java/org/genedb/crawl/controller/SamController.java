package org.genedb.crawl.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.genedb.crawl.CrawlException;
import org.genedb.crawl.annotations.ResourceDescription;
import org.genedb.crawl.bam.AlignmentStore;
import org.genedb.crawl.bam.Sam;

import org.genedb.crawl.mappers.OrganismsMapper;
import org.genedb.crawl.model.FileInfo;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.ResultsSAM;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
@ResourceDescription("provides methods for SAM/BAM alignment display")
@RequestMapping("/sams")
public class SamController extends BaseQueryController {
	
	private Logger logger = Logger.getLogger(SamController.class);
	
	@SuppressWarnings("unused")
	private AlignmentStore alignmentStore;
	private Sam sam = new Sam();
	
	@Autowired
	private OrganismsMapper organismsMapper;
	
	@Autowired
	public void setAlignmentStore(AlignmentStore alignmentStore) {
		this.alignmentStore = alignmentStore;
		sam.alignmentStore = alignmentStore;
	}
	
	@RequestMapping(method=RequestMethod.GET, value={"/header", "/header.*"})
	@ResourceDescription("Returns the header attributes for a SAM or BAM in the repository.")
	public ResultsSAM header(
			ResultsSAM results,
			@RequestParam("fileID") int fileID) 
	throws Exception {
		results.header = sam.header(fileID);
		return results;
	}
	
	@RequestMapping(method=RequestMethod.GET, value={"/sequences", "/sequences.*"})
	@ResourceDescription("Returns the sequences for a SAM or BAM in the repository.")
	public ResultsSAM sequences(
			ResultsSAM results,
			@RequestParam("fileID") int fileID) throws Exception {
		results.sequences = sam.sequence(fileID); 
		return results;
	}
	
	
	@RequestMapping(method=RequestMethod.GET, value={"/query", "/query.*"})
	@ResourceDescription("Returns the reads between a start and end position for a SAM or BAM in the repository.")
	public ResultsSAM query(
			ResultsSAM results,
			@RequestParam("fileID") int fileID, 
			@RequestParam("sequence") @ResourceDescription("The FASTA sequence name, as returned by the sequences query") String sequence,
			@RequestParam("start") int start,
			@RequestParam("end") int end,
			@RequestParam(value="contained", defaultValue="true", required=false) Boolean contained,
			@RequestParam(value="filter", defaultValue="0") int filter) throws Exception {
		results.query = sam.query(fileID, sequence, start, end, contained, filter);
		return results;
	}
	
	
	@RequestMapping(method=RequestMethod.GET, value={"/coverage", "/coverage.*"})
	@ResourceDescription("Computes the coverage count for a range, windowed in steps for a SAM or BAM in the repository.")
	public synchronized ResultsSAM coverage(
			ResultsSAM results, 
			@RequestParam("fileID") int fileID, 
			@RequestParam("sequence") String sequence,
			@RequestParam("start") int start,
			@RequestParam("end") int end,
			@RequestParam("window") int window,
			@RequestParam(value="filter", defaultValue="0", required=false) Integer filter) throws Exception {
		results.coverage = sam.coverage(fileID, sequence, start, end, window, filter);
		return results;
	}
	
	
	@ResourceDescription("Returns a list of SAM / BAM files in the repository.")
	@RequestMapping(method=RequestMethod.GET, value={"/list", "/list.*"})
	public ResultsSAM list(ResultsSAM results) {
		results.files = sam.list();
		return results;
	}
	
	@ResourceDescription("Returns a list of SAM / BAM files for a particular organism.")
	@RequestMapping(method=RequestMethod.GET, value={"/listfororganism", "/listfororganism.*"})
	public ResultsSAM listfororganism( 
			ResultsSAM results,
			@RequestParam("organism") String organism) throws CrawlException {
		
		List<FileInfo> matchedAlignments = new ArrayList<FileInfo>();
		
		Organism mappedOrganism = getOrganism(organismsMapper, organism);
		
		if (mappedOrganism != null) {
			logger.debug(mappedOrganism.common_name);
			matchedAlignments = sam.listfororganism(mappedOrganism.common_name);
		}
		
		results.files = matchedAlignments;
		return results;
	}
	
	
	@ResourceDescription("Returns a list of SAM / BAM files for a particular sequence.")
	@RequestMapping(method=RequestMethod.GET, value={"/listforsequence", "/listforsequence.*"})
	public ResultsSAM listforsequence( 
			ResultsSAM results,
			@RequestParam("sequence") String sequence) throws Exception {
		
		List<FileInfo> matchedAlignments = sam.listwithsequence(sequence);
		results.files = matchedAlignments;
		return results;
	}
	
	
}


