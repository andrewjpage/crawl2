package org.genedb.crawl.controller;

import org.apache.log4j.Logger;
import org.genedb.crawl.CrawlException;
import org.genedb.crawl.annotations.ResourceDescription;
import org.genedb.crawl.business.AlignmentStore;
import org.genedb.crawl.business.Sam;

import org.genedb.crawl.model.FileInfoList;
import org.genedb.crawl.model.MappedCoverage;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.MappedQuery;
import org.genedb.crawl.model.MappedSAMHeader;

import org.genedb.crawl.model.MappedSAMSequenceList;
import org.gmod.cat.Organisms;
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
	private Organisms organisms;
	
	@Autowired
	public void setAlignmentStore(AlignmentStore alignmentStore) {
		this.alignmentStore = alignmentStore;
		sam.alignmentStore = alignmentStore;
	}
	
	@RequestMapping(method=RequestMethod.GET, value={"/header", "/header.*"})
	@ResourceDescription("Returns the header attributes for a SAM or BAM in the repository.")
	public MappedSAMHeader header(
			@RequestParam(value="callback", required=false) String callback, 
			@RequestParam("fileID") int fileID) 
	throws Exception {
		return sam.header(fileID);
	}
	
	@RequestMapping(method=RequestMethod.GET, value={"/sequences", "/sequences.*"})
	@ResourceDescription("Returns the sequences for a SAM or BAM in the repository.")
	public MappedSAMSequenceList sequences(
			@RequestParam(value="callback", required=false) String callback,
			@RequestParam("fileID") int fileID) throws Exception {
		return sam.sequence(fileID);
	}
	
	
	@RequestMapping(method=RequestMethod.GET, value={"/query", "/query.*"})
	@ResourceDescription("Returns the reads between a start and end position for a SAM or BAM in the repository.")
	public MappedQuery query(
			@RequestParam(value="callback", required=false) String callback,
			@RequestParam("fileID") int fileID, 
			@RequestParam("sequence") @ResourceDescription("The FASTA sequence name, as returned by the sequences query") String sequence,
			@RequestParam("start") int start,
			@RequestParam("end") int end,
			@RequestParam(value="contained", defaultValue="true", required=false) Boolean contained,
			@RequestParam(value="filter", defaultValue="0", required=false) Integer filter) throws Exception {
		
		return sam.query(fileID, sequence, start, end, contained, filter);
	}
	
	
	@RequestMapping(method=RequestMethod.GET, value={"/coverage", "/coverage.*"})
	@ResourceDescription("Computes the coverage count for a range, windowed in steps for a SAM or BAM in the repository.")
	public synchronized MappedCoverage coverage(
			@RequestParam(value="callback", required=false) String callback,
			@RequestParam("fileID") int fileID, 
			@RequestParam("sequence") String sequence,
			@RequestParam("start") int start,
			@RequestParam("end") int end,
			@RequestParam("window") int window,
			@RequestParam(value="filter", defaultValue="0", required=false) Integer filter) throws Exception {
		
		return sam.coverage(fileID, sequence, start, end, window, filter);
	}
	
	
	@ResourceDescription("Returns a list of SAM / BAM files in the repository.")
	@RequestMapping(method=RequestMethod.GET, value={"/list", "/list.*"})
	public FileInfoList list(@RequestParam(value="callback", required=false) String callback) {
		return sam.list();
	}
	
	@ResourceDescription("Returns a list of SAM / BAM files for a particular organism.")
	@RequestMapping(method=RequestMethod.GET, value={"/listfororganism", "/listfororganism.*"})
	public FileInfoList listfororganism( 
			@RequestParam(value="callback", required=false) String callback,
			@RequestParam("organism") String organism) throws CrawlException {
		
		FileInfoList matchedAlignments = new FileInfoList();
		
		Organism mappedOrganism = getOrganism(organisms, organism);
		
		if (mappedOrganism != null) {
			logger.debug(mappedOrganism.common_name);
			matchedAlignments = sam.listfororganism(mappedOrganism.common_name);
		}
		
		return matchedAlignments;
		
	}
	
	
}


