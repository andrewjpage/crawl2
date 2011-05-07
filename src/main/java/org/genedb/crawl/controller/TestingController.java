package org.genedb.crawl.controller;

import org.apache.log4j.Logger;


import org.genedb.crawl.model.Service;
import org.genedb.crawl.model.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/testing")
public class TestingController {
	
	private Logger logger = Logger.getLogger(TestingController.class);
	
	private String[] resources = new String[] {
		"features", "organisms", "regions", "sams", "variants"	
	};
	
	@RequestMapping(method=RequestMethod.GET, value="/")
	public Service welcome() {
		Service s = new Service ();
		
		s.name = "index";
		
		for (String resource : resources) {
			Resource r = new Resource();
			r.name = resource;
			s.resources.add(r);
		}
		
		
		return s;
	}
	
}
