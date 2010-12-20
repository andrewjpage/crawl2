package org.genedb.crawl.controller;

import org.genedb.crawl.CrawlException;
import org.genedb.crawl.annotations.ResourceDescription;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.OrganismList;
import org.gmod.cat.Organisms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/organisms")
@ResourceDescription("Organism related queries")
public class OrganismsController extends BaseQueryController {
	
	private Organisms organisms;
	
	@Autowired
	public void setOrganisms(Organisms organisms) {
		this.organisms = organisms;
	}
	
	@ResourceDescription("List all the organisms in the repository")
	@RequestMapping(method=RequestMethod.GET, value={"/list", "/list.*"})
	public OrganismList list() throws CrawlException {
		OrganismList mol = new OrganismList();
		mol.organisms =  organisms.list();
		return mol;
	}
	
	@ResourceDescription("Get an organism using the organism id")
	@RequestMapping(method=RequestMethod.GET, value={"/getByID", "/getByID.*"})
	public Organism getByID(@RequestParam("ID") int id) throws CrawlException {
		return organisms.getByID(id);		
	}
	
	@ResourceDescription("Get an organism using its taxon ID")
	@RequestMapping(method=RequestMethod.GET, value={"/getByTaxonID", "/getByTaxonID.*"})
	public Organism getByTaxonID(@RequestParam("taxonID") int taxonID) throws CrawlException {
		return organisms.getByTaxonID(String.valueOf(taxonID));
	}
	
	@ResourceDescription("Get an organism by specifying its common name")
	@RequestMapping(method=RequestMethod.GET, value={"/getByCommonName", "/getByCommonName.*"})
	public Organism getByCommonName(@RequestParam("commonName") String commonName) throws CrawlException {
		return organisms.getByCommonName(commonName);
	}
	
	@ResourceDescription("Get an organism using a taxon ID, common name, or organism ID")
	@RequestMapping(method=RequestMethod.GET, value="/get")
	public Organism get(String organism) throws CrawlException {
		return getOrganism(organisms, organism);
	}
	
	
}
