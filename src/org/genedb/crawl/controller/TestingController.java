package org.genedb.crawl.controller;

import org.apache.log4j.Logger;
import org.genedb.crawl.CrawlException;
import org.genedb.crawl.model.interfaces.Organisms;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/test")
public class TestingController {
	
	private Logger logger = Logger.getLogger(TestingController.class);
	
	private Organisms organisms;
	
	public void setOrganisms(Organisms organisms) {
		this.organisms = organisms;
	}
	
	@RequestMapping(method=RequestMethod.GET, value={"/list", "/list.*"})
	public ModelAndView list() throws CrawlException {
		ModelAndView mav = new ModelAndView();
		
		logger.info(organisms.list());
		
		return mav;
	}
	
}
