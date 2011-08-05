package org.genedb.crawl.controller;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.genedb.crawl.CrawlException;
import org.genedb.crawl.annotations.ResourceDescription;
import org.genedb.crawl.bam.BioDataFileStoreInitializer;
import org.genedb.crawl.bam.Sam;

import org.genedb.crawl.mappers.OrganismsMapper;
import org.genedb.crawl.model.Alignment;
import org.genedb.crawl.model.MappedCoverage;
import org.genedb.crawl.model.MappedQuery;
import org.genedb.crawl.model.MappedSAMHeader;
import org.genedb.crawl.model.MappedSAMSequence;
import org.genedb.crawl.model.Organism;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@ResourceDescription("Provides methods for SAM/BAM alignment querying.")
@RequestMapping("/sams")
@WebService(serviceName="sams")
public class SamController extends BaseQueryController {

	private Sam sam = new Sam();

	@Autowired
	private OrganismsMapper organismsMapper;

	@Autowired
	@WebMethod(exclude=true)
	public void setBioDataFileStoreInitializer(
			BioDataFileStoreInitializer initializer) {
		sam.setAlignmentStore(initializer.getAlignments());
	}

	@RequestMapping(method = RequestMethod.GET, value = { "/header",
			"/header.*" })
	@ResourceDescription("Returns the header attributes for a SAM or BAM in the repository.")
	public MappedSAMHeader header(@RequestParam("fileID") int fileID)
			throws Exception {
		return sam.header(fileID);
	}

	@RequestMapping(method = RequestMethod.GET, value = { "/sequences",
			"/sequences.*" })
	@ResourceDescription("Returns the sequences for a SAM or BAM in the repository.")
	public List<MappedSAMSequence> sequences(@RequestParam("fileID") int fileID)
			throws Exception {
		return sam.sequence(fileID);
	}

	@RequestMapping(method = RequestMethod.GET, value = { "/query", "/query.*" })
	@ResourceDescription("Returns the reads between a start and end position for a SAM or BAM in the repository.")
	public MappedQuery query(
			@RequestParam("fileID") int fileID,
			@RequestParam("sequence") @ResourceDescription("The FASTA sequence name, as returned by the sequences query") String sequence,
			@RequestParam("start") int start,
			@RequestParam("end") int end,
			@RequestParam(value = "contained", defaultValue = "true", required = false) Boolean contained,
			@RequestParam(value = "filter", defaultValue = "0") int filter,
			@RequestParam(value = "properties", required = false) String[] properties)
			throws Exception {
		return sam.query(fileID, sequence, start, end, contained, properties,
				filter);
	}

	@RequestMapping(method = RequestMethod.GET, value = { "/coverage",
			"/coverage.*" })
	@ResourceDescription("Computes the coverage count for a range, windowed in steps for a SAM or BAM in the repository.")
	public synchronized MappedCoverage coverage(
			@RequestParam("fileID") int fileID,
			@RequestParam("sequence") String sequence,
			@RequestParam("start") int start,
			@RequestParam("end") int end,
			@RequestParam("window") int window,
			@RequestParam(value = "filter", defaultValue = "0", required = false) Integer filter)
			throws Exception {
		return sam.coverage(fileID, sequence, start, end, window, filter);
	}

	@ResourceDescription("Returns a list of SAM / BAM files in the repository.")
	@RequestMapping(method = RequestMethod.GET, value = { "/list", "/list.*" })
	public List<Alignment> list() {
		return sam.list();
	}

	@ResourceDescription("Returns a list of SAM / BAM files for a particular organism.")
	@RequestMapping(method = RequestMethod.GET, value = { "/listfororganism",
			"/listfororganism.*" })
	public List<Alignment> listfororganism(
			@RequestParam("organism") String organism) throws CrawlException {

		List<Alignment> matchedAlignments = new ArrayList<Alignment>();
		Organism mappedOrganism = getOrganism(organismsMapper, organism);
		if (mappedOrganism != null) {
			matchedAlignments = sam.listfororganism(mappedOrganism.common_name);
		}
		return matchedAlignments;
	}

	@ResourceDescription("Returns a list of SAM / BAM files for a particular sequence.")
	@RequestMapping(method = RequestMethod.GET, value = { "/listforsequence",
			"/listforsequence.*" })
	public List<Alignment> listforsequence(
			@RequestParam("sequence") String sequence) throws Exception {
		List<Alignment> matchedAlignments = sam.listforsequence(sequence);
		return matchedAlignments;
	}

}
