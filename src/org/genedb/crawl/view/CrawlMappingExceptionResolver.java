package org.genedb.crawl.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import org.apache.log4j.Logger;
import org.genedb.crawl.CrawlException;

import org.genedb.crawl.model.CrawlError;

public class CrawlMappingExceptionResolver extends SimpleMappingExceptionResolver {
	private Logger logger = Logger.getLogger(CrawlMappingExceptionResolver.class);
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		ModelAndView mav = new ModelAndView("service:");
		
		//BaseResult result = new BaseResult();
		
		CrawlError error = new CrawlError();
		
		if (ex instanceof CrawlException) {
			error.setException((CrawlException)ex);
		} else {
			error.message = ex.getMessage();
		}
		
		//result.addResult(error);
		
		ex.printStackTrace();
		
		mav.addObject("model" , error);
		return mav;
	}
	
}

