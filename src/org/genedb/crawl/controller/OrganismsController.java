package org.genedb.crawl.controller;

import java.util.List;

import org.genedb.crawl.CrawlException;
import org.genedb.crawl.annotations.ResourceDescription;
import org.genedb.crawl.model.Cvterm;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.OrganismProp;
import org.genedb.crawl.model.Results;
import org.gmod.cat.OrganismsMapper;
import org.gmod.cat.TermsMapper;
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
	public Results list(Results results) throws CrawlException {
		List<Organism> list = organismsMapper.list();
		
		for (Organism organism : list) {
			OrganismProp prop = organismsMapper.getOrganismProp(organism.ID, "genedb_misc", "translationTable");
			
			if (prop != null) {
				organism.translation_table = prop.value;
			}
			
		}
		
		results.organisms = list;
		return results;		
	}
	
	@ResourceDescription(value="Get an organism using the organism id", type="Organism")
	@RequestMapping(method=RequestMethod.GET, value={"/getByID", "/getByID.*"})
	public Results getByID(Results results, @RequestParam("ID") int id) throws CrawlException {
		results.addOrganism(organismsMapper.getByID(id));
		return results;
	}
	
	@ResourceDescription(value="Get an organism using its taxon ID", type="Organism")
	@RequestMapping(method=RequestMethod.GET, value={"/getByTaxonID", "/getByTaxonID.*"})
	public Results getByTaxonID(Results results, @RequestParam("taxonID") int taxonID) throws CrawlException {
		results.addOrganism(organismsMapper.getByTaxonID(String.valueOf(taxonID)));
		return results;
	}
	
	@ResourceDescription(value="Get an organism by specifying its common name", type="Organism")
	@RequestMapping(method=RequestMethod.GET, value={"/getByCommonName", "/getByCommonName.*"})
	public Results getByCommonName(Results results, @RequestParam("commonName") String commonName) throws CrawlException {
		results.addOrganism(organismsMapper.getByCommonName(commonName));
		return results;
	}
	
	@ResourceDescription(value="Get an organism using a taxon ID, common name, or organism ID", type="Organism")
	@RequestMapping(method=RequestMethod.GET, value="/get")
	public Results get(Results results, String organism) throws CrawlException {
		results.addOrganism(getOrganism(organismsMapper, organism));
		return results;
	}
	
	
}
