package org.genedb.crawl.controller;

import java.util.List;

import javax.jws.WebService;

import org.genedb.crawl.CrawlException;
import org.genedb.crawl.annotations.ResourceDescription;
import org.genedb.crawl.dao.OrganismsDAO;

import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.Property;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/organisms")
@ResourceDescription("Organism related queries")
@WebService(serviceName="organisms")
public class OrganismsController extends BaseController implements OrganismsDAO {
	
    @Autowired
    OrganismsDAO dao;
    
	@Override
    @ResourceDescription(value="List all the organisms in the repository", type="Organism")
	@RequestMapping(method=RequestMethod.GET, value={"/list", "/list.*"})
	public List<Organism> listOrganisms() throws CrawlException {
	    return dao.listOrganisms();
	}
	
	@Override
    @ResourceDescription(value="Get an organism using the organism id", type="Organism")
	@RequestMapping(method=RequestMethod.GET, value={"/getByID", "/getByID.*"})
	public List<Organism> getByID(@RequestParam("ID") int id) throws CrawlException {
	    return dao.getByID(id);
	}
	
	@Override
    @ResourceDescription(value="Get an organism using its taxon ID", type="Organism")
	@RequestMapping(method=RequestMethod.GET, value={"/getByTaxonID", "/getByTaxonID.*"})
	public List<Organism> getByTaxonID(@RequestParam("taxonID") int taxonID) throws CrawlException {
	    return dao.getByTaxonID(taxonID);
	}
	
	@Override
    @ResourceDescription(value="Get an organism by specifying its common name", type="Organism")
	@RequestMapping(method=RequestMethod.GET, value={"/getByCommonName", "/getByCommonName.*"})
	public List<Organism> getByCommonName(@RequestParam("commonName") String commonName) throws CrawlException {
	    return dao.getByCommonName(commonName);
	}
	
	@Override
    @ResourceDescription(value="Get an organism using a taxon ID, common name, or organism ID", type="Organism")
	@RequestMapping(method=RequestMethod.GET, value="/get")
	public List<Organism> getByString(@RequestParam("organism") String organism) throws CrawlException {
	    return dao.getByString(organism);
	}
	
	@Override
    @ResourceDescription(value="Get an organism property", type="Organism")
    @RequestMapping(method=RequestMethod.GET, value="/property")
	public Property property(@RequestParam("organism") String organism, @RequestParam("term") String term, @RequestParam(value="cv", required=false) String cv) {
	    return dao.property(organism, term, cv);
	}
	
	@Override
    @ResourceDescription(value="Get an organism property", type="Organism")
    @RequestMapping(method=RequestMethod.GET, value="/properties")
    public List<Property> properties(@RequestParam("organism") String organism, @RequestParam(value="cv", required=false) String cv) {
	    return dao.properties(organism, cv);
    }
	
	
	
}
