package org.genedb.crawl.controller;

import java.io.IOException;
import java.util.List;

import javax.jws.WebService;

import org.genedb.crawl.CrawlException;
import org.genedb.crawl.annotations.ResourceDescription;
import org.genedb.crawl.dao.VariantDAO;
import org.genedb.crawl.model.MappedSAMSequence;
import org.genedb.crawl.model.MappedVCFRecord;
import org.genedb.crawl.model.Variant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.sanger.artemis.util.OutOfRangeException;

@Controller
@ResourceDescription("Provides methods for VCF/BCF variant querying.")
@RequestMapping("/variants")
@WebService(serviceName="variants")
public class VariantController extends BaseController implements VariantDAO {
	
	@Autowired
	VariantDAO dao;
	
	@Override
    @ResourceDescription("Returns the complete list of variant files.")
	@RequestMapping(method=RequestMethod.GET, value={"/list", "/list.*"})
	public List<Variant> list() throws IOException {
		return dao.list();
	}
	
	
	@Override
    @ResourceDescription("Returns a list of sequences in a variant file.")
	@RequestMapping(method=RequestMethod.GET, value={"/sequences", "/sequences.*"})
	public List<MappedSAMSequence> sequences(
			@RequestParam("fileID") int fileID) throws IOException {
		return dao.sequences(fileID);
	}
	
	
	@Override
    @ResourceDescription("Returns a list of variant files for a particular organism.")
	@RequestMapping(method=RequestMethod.GET, value={"/listfororganism", "/listfororganism.*"})
	public List<Variant> listfororganism( 
			@RequestParam("organism") String organism) throws IOException {
		return dao.listfororganism(organism);
	}
	
	
	@Override
    @ResourceDescription("Returns a list of variant files for a particular sequence.")
	@RequestMapping(method=RequestMethod.GET, value={"/listforsequence", "/listforsequence.*"})
	public List<Variant> listforsequence( 
			@RequestParam("sequence") String sequence) throws Exception {
	    return dao.listforsequence(sequence);
	}
	
	
	@Override
    @ResourceDescription("Queries a region of a variant file.")
	@RequestMapping(method=RequestMethod.GET, value={"/query", "/query.*"})
	public List<MappedVCFRecord> query(
			@RequestParam("fileID") int fileID, 
			@RequestParam("sequence") String sequence, 
			@RequestParam("start") int start, 
			@RequestParam("end") int end,
			@RequestParam(value="filter", required=false) Integer filter,
			@RequestParam(value="filters", required=false) List<String> filters) throws IOException, CrawlException, OutOfRangeException {
	    return dao.query(fileID, sequence, start, end, filter, filters);
	}
	
	
	
}
