package org.genedb.crawl.controller;

import java.util.List;

import org.genedb.crawl.CrawlException;
import org.genedb.crawl.annotations.ResourceDescription;
import org.genedb.crawl.mappers.OrganismsMapper;

import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.OrganismProp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/organisms")
@ResourceDescription("Organism related queries")
public class OrganismsController extends BaseQueryController {
	
	@Autowired
	private OrganismsMapper organismsMapper;
	
	
	@ResourceDescription(value="List all the organisms in the repository", type="Organism")
	@RequestMapping(method=RequestMethod.GET, value={"/list", "/list.*"})
	public List<Organism> list() throws CrawlException {
		List<Organism> list = organismsMapper.list();
		
		for (Organism organism : list) {
			OrganismProp prop = organismsMapper.getOrganismProp(organism.ID, "genedb_misc", "translationTable");
			
			if (prop != null) {
				organism.translation_table = Integer.parseInt(prop.value);
			}
			
		}
		
		return list;		
	}
	
	@ResourceDescription(value="Get an organism using the organism id", type="Organism")
	@RequestMapping(method=RequestMethod.GET, value={"/getByID", "/getByID.*"})
	public List<Organism> getByID(@RequestParam("ID") int id) throws CrawlException {
		List<Organism> list = organismsMapper.list();
		list.add(organismsMapper.getByID(id));
		return list;
	}
	
	@ResourceDescription(value="Get an organism using its taxon ID", type="Organism")
	@RequestMapping(method=RequestMethod.GET, value={"/getByTaxonID", "/getByTaxonID.*"})
	public List<Organism> getByTaxonID(@RequestParam("taxonID") int taxonID) throws CrawlException {
		List<Organism> list = organismsMapper.list();
		list.add(organismsMapper.getByTaxonID(String.valueOf(taxonID)));
		return list;
	}
	
	@ResourceDescription(value="Get an organism by specifying its common name", type="Organism")
	@RequestMapping(method=RequestMethod.GET, value={"/getByCommonName", "/getByCommonName.*"})
	public List<Organism> getByCommonName(@RequestParam("commonName") String commonName) throws CrawlException {
		List<Organism> list = organismsMapper.list();
		list.add(organismsMapper.getByCommonName(commonName));
		return list;
	}
	
	@ResourceDescription(value="Get an organism using a taxon ID, common name, or organism ID", type="Organism")
	@RequestMapping(method=RequestMethod.GET, value="/get")
	public List<Organism> get(@RequestParam("organism") String organism) throws CrawlException {
		List<Organism> list = organismsMapper.list();
		list.add(getOrganism(organismsMapper, organism));
		return list;
	}
	
	
}
