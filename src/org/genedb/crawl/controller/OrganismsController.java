package org.genedb.crawl.controller;

import org.apache.log4j.Logger;
import org.genedb.crawl.CrawlException;
import org.genedb.crawl.model.MappedOrganismList;
import org.genedb.crawl.model.interfaces.Organisms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/organisms")
public class OrganismsController extends BaseQueryController {
	
	private Logger logger = Logger.getLogger(OrganismsController.class);
	
	@Autowired
	Organisms organisms;
	
	@RequestMapping(method=RequestMethod.GET, value={"/list", "/list.*"})
	public ModelAndView list() throws CrawlException {
		ModelAndView mav = new ModelAndView("service:"); 
		MappedOrganismList organismsList = organisms.list();
		mav.addObject("model", organismsList);
		return mav;
	}
	
	
	
	
}
