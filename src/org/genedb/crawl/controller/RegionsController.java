package org.genedb.crawl.controller;

import javax.servlet.http.HttpServletRequest;

import org.genedb.crawl.CrawlException;
import org.genedb.crawl.model.Locations;
import org.genedb.crawl.model.interfaces.Regions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/regions")
public class RegionsController extends BaseQueryController {
	
	@Autowired
	Regions regions;
	
	@RequestMapping(method=RequestMethod.GET, value={"/locations", "/locations.*"})
	public ModelAndView locations(HttpServletRequest request, @RequestParam("region") String region, @RequestParam("start") int start, @RequestParam("end") int end) throws CrawlException {
		ModelAndView mav = new ModelAndView("service:"); 
		Locations locations = regions.locations(region, start, end);
		mav.addObject("model", this.generateResponseWrapper(request,locations));
		return mav;
	}
	
	
	
}
