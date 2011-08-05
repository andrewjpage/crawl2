package org.genedb.crawl.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.log4j.Logger;
import org.genedb.crawl.CrawlException;
import org.genedb.crawl.annotations.ResourceDescription;
import org.genedb.crawl.mappers.OrganismsMapper;

import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.OrganismProp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/organisms")
@ResourceDescription("Organism related queries")
@WebService(serviceName="organisms")
public class OrganismsController extends BaseQueryController {
	
    private static Logger logger = Logger.getLogger(OrganismsController.class);
    
    @Autowired
	private OrganismsMapper organismsMapper;
	
	
	@ResourceDescription(value="List all the organisms in the repository", type="Organism")
	@RequestMapping(method=RequestMethod.GET, value={"/list", "/list.*"})
	public List<Organism> listOrganisms() throws CrawlException {
	    logger.info("????");
	    logger.info(this);
	    logger.info(organismsMapper == null);
	    logger.info(organismsMapper.getClass());
	    logger.info("????");
		List<Organism> list = organismsMapper.list();
		return addProps(list);
	}
	
	@ResourceDescription(value="Get an organism using the organism id", type="Organism")
	@RequestMapping(method=RequestMethod.GET, value={"/getByID", "/getByID.*"})
	public List<Organism> getByID(@RequestParam("ID") int id) throws CrawlException {
	    ArrayList<Organism> list = new ArrayList<Organism>();
		list.add(organismsMapper.getByID(id));
		return addProps(list);
	}
	
	@ResourceDescription(value="Get an organism using its taxon ID", type="Organism")
	@RequestMapping(method=RequestMethod.GET, value={"/getByTaxonID", "/getByTaxonID.*"})
	public List<Organism> getByTaxonID(@RequestParam("taxonID") int taxonID) throws CrawlException {
	    ArrayList<Organism> list = new ArrayList<Organism>();
		list.add(organismsMapper.getByTaxonID(String.valueOf(taxonID)));
		return addProps(list);
	}
	
	@ResourceDescription(value="Get an organism by specifying its common name", type="Organism")
	@RequestMapping(method=RequestMethod.GET, value={"/getByCommonName", "/getByCommonName.*"})
	public List<Organism> getByCommonName(@RequestParam("commonName") String commonName) throws CrawlException {
	    ArrayList<Organism> list = new ArrayList<Organism>();
		list.add(organismsMapper.getByCommonName(commonName));
		return addProps(list);
	}
	
	@ResourceDescription(value="Get an organism using a taxon ID, common name, or organism ID", type="Organism")
	@RequestMapping(method=RequestMethod.GET, value="/get")
	public List<Organism> getByString(@RequestParam("organism") String organism) throws CrawlException {
	    ArrayList<Organism> list = new ArrayList<Organism>();
		list.add(getOrganism(organismsMapper, organism));
		return addProps(list);
	}
	
	private List<Organism> addProps(List<Organism> list) {
		for (Organism organism : list) {
			OrganismProp prop = organismsMapper.getOrganismProp(organism.ID, "genedb_misc", "translationTable");
			if (prop != null) {
				organism.translation_table = Integer.parseInt(prop.value);
			}
		}
		return list;
	}
	
	
}
