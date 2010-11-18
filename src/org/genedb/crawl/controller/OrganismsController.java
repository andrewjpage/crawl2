package org.genedb.crawl.controller;


import javax.servlet.http.HttpServletRequest;

import org.genedb.crawl.CrawlException;
import org.genedb.crawl.model.MappedOrganism;
import org.genedb.crawl.model.MappedOrganismList;
import org.gmod.cat.Organisms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/organisms")
public class OrganismsController extends BaseQueryController {
	
	private Organisms organisms;
	
	@Autowired
	public void setOrganisms(Organisms organisms) {
		this.organisms = organisms;
	}
	
	@RequestMapping(method=RequestMethod.GET, value={"/list", "/list.*"})
	public ModelAndView list(HttpServletRequest request) throws CrawlException {
		ModelAndView mav = new ModelAndView("service:"); 
		
		MappedOrganismList mol = new MappedOrganismList();
		mol.organisms =  organisms.list();
		
		mav.addObject("model", mol);
		return mav;
	}
	
	
	@RequestMapping(method=RequestMethod.GET, value={"/getByID", "/getByID.*"})
	public ModelAndView getByID(HttpServletRequest request, @RequestParam("ID") int id) throws CrawlException {
		ModelAndView mav = new ModelAndView("service:"); 
		
		MappedOrganism mo = organisms.getByID(id);
		
		mav.addObject("model", mo);
		return mav;
	}
	
	
	
	@RequestMapping(method=RequestMethod.GET, value={"/getByTaxonID", "/getByTaxonID.*"})
	public ModelAndView getByTaxonID(HttpServletRequest request, @RequestParam("taxonID") int taxonID) throws CrawlException {
		ModelAndView mav = new ModelAndView("service:"); 
		
		MappedOrganism mo = organisms.getByTaxonID(String.valueOf(taxonID));
		
		mav.addObject("model", mo);
		return mav;
	}
	
	@RequestMapping(method=RequestMethod.GET, value={"/getByCommonName", "/getByCommonName.*"})
	public ModelAndView getByCommonName(HttpServletRequest request, @RequestParam("commonName") String commonName) throws CrawlException {
		ModelAndView mav = new ModelAndView("service:"); 
		
		MappedOrganism mo = organisms.getByCommonName(commonName);
		
		mav.addObject("model", mo);
		return mav;
	}
	
	
}
