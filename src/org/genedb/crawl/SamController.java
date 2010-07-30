package org.genedb.crawl;

import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.genedb.crawl.business.HeirarchyIndex;
import org.genedb.crawl.business.Sam;
import org.genedb.crawl.model.BaseResult;
import org.genedb.crawl.model.FileInfoList;
import org.genedb.crawl.model.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/sam")
public class SamController {
	
	private Logger logger = Logger.getLogger(SamController.class);
	private HeirarchyIndex heirarchyIndex;
	private Sam sam = new Sam();
	
	@Autowired
	public void setHeirarchyIndex(HeirarchyIndex heirarchyIndex) {
		this.heirarchyIndex = heirarchyIndex;
		sam.heirarchyIndex = heirarchyIndex;
	}
	
	@RequestMapping(method=RequestMethod.GET, value={"/header", "/header.*"})
	public ModelAndView header(HttpServletRequest request, @RequestParam("fileID") int fileID) throws Exception {
		ModelAndView mav = new ModelAndView("service:");
		mav.addObject("model", sam.header(fileID));
		return mav;
	}
	
	@RequestMapping(method=RequestMethod.GET, value={"/sequences", "/sequences.*"})
	public ModelAndView sequences(HttpServletRequest request, @RequestParam("fileID") int fileID) throws Exception {
		ModelAndView mav = new ModelAndView("service:");
		mav.addObject("model", sam.sequence(fileID));
		return mav;
	}
	
	@RequestMapping(method=RequestMethod.GET, value={"/query", "/query.*"})
	public ModelAndView query(
			@RequestParam("fileID") int fileID, 
			@RequestParam("sequence") String sequence,
			@RequestParam("start") int start,
			@RequestParam("end") int end,
			@RequestParam("contained") boolean contained) throws Exception {
		ModelAndView mav = new ModelAndView("service:");
		mav.addObject("model", sam.query(fileID, sequence, start, end, contained));
		return mav;
	}
	
	
	@RequestMapping(method=RequestMethod.GET, value={"/coverage", "/coverage.*"})
	public synchronized ModelAndView coverage(
			@RequestParam("fileID") int fileID, 
			@RequestParam("sequence") String sequence,
			@RequestParam("start") int start,
			@RequestParam("end") int end,
			@RequestParam("window") int window) throws Exception {
		
		ModelAndView mav = new ModelAndView("service:");
		mav.addObject("model", sam.coverage(fileID, sequence, start, end, window));
		return mav;
	}
	
	
	@RequestMapping(method=RequestMethod.GET, value={"/list", "/list.*"})
	public ModelAndView list() {
		ModelAndView mav = new ModelAndView("service:");
		
		BaseResult model = new BaseResult();
		FileInfoList list= new FileInfoList();
		model.addResult(list);
		
		for (FileInfo file : heirarchyIndex) {
			list.addResult(file);
			logger.info(file);
			System.out.println(file);
		}
		
		mav.addObject("model", model);
		
		return mav;
	}
	
	
}


