package org.genedb.crawl.controller;

import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.genedb.crawl.business.AlignmentStore;
import org.genedb.crawl.business.Sam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/sam")
public class SamController extends BaseQueryController {
	
	private Logger logger = Logger.getLogger(SamController.class);
	
	private AlignmentStore alignmentStore;
	private Sam sam = new Sam();
	
	@Autowired
	public void setAlignmentStore(AlignmentStore alignmentStore) {
		this.alignmentStore = alignmentStore;
		sam.alignmentStore = alignmentStore;
	}
	
	@RequestMapping(method=RequestMethod.GET, value={"/header", "/header.*"})
	public ModelAndView header(HttpServletRequest request, @RequestParam("fileID") int fileID) throws Exception {
		ModelAndView mav = new ModelAndView("service:");
		mav.addObject("model", generateResponseWrapper(request, sam.header(fileID)));
		return mav;
	}
	
	@RequestMapping(method=RequestMethod.GET, value={"/sequences", "/sequences.*"})
	public ModelAndView sequences(HttpServletRequest request, @RequestParam("fileID") int fileID) throws Exception {
		ModelAndView mav = new ModelAndView("service:");
		mav.addObject("model", generateResponseWrapper(request, sam.sequence(fileID)));
		return mav;
	}
	
	@RequestMapping(method=RequestMethod.GET, value={"/query", "/query.*"})
	public ModelAndView query(
			HttpServletRequest request,
			@RequestParam("fileID") int fileID, 
			@RequestParam("sequence") String sequence,
			@RequestParam("start") int start,
			@RequestParam("end") int end,
			@RequestParam("contained") boolean contained,
			@RequestParam("filter") int filter) throws Exception {
		ModelAndView mav = new ModelAndView("service:");
		mav.addObject("model", generateResponseWrapper(request, sam.query(fileID, sequence, start, end, contained, filter)));
		return mav;
	}
	
	
	@RequestMapping(method=RequestMethod.GET, value={"/coverage", "/coverage.*"})
	public synchronized ModelAndView coverage(
			HttpServletRequest request,
			@RequestParam("fileID") int fileID, 
			@RequestParam("sequence") String sequence,
			@RequestParam("start") int start,
			@RequestParam("end") int end,
			@RequestParam("window") int window) throws Exception {
		
		ModelAndView mav = new ModelAndView("service:");
		mav.addObject("model", generateResponseWrapper(request, sam.coverage(fileID, sequence, start, end, window)));
		return mav;
	}
	
	
	@RequestMapping(method=RequestMethod.GET, value={"/list", "/list.*"})
	public ModelAndView list(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView("service:");
		mav.addObject("model", generateResponseWrapper(request, sam.list()));
		return mav;
	}
	
	@RequestMapping(method=RequestMethod.GET, value={"/listfororganism", "/listfororganism.*"})
	public ModelAndView listfororganism(HttpServletRequest request, @RequestParam("organism") String organism) {
		ModelAndView mav = new ModelAndView("service:");
		mav.addObject("model", generateResponseWrapper(request, sam.list()));
		return mav;
	}
	
	
}


