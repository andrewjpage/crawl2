package org.genedb.crawl.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import org.apache.log4j.Logger;
import org.genedb.crawl.CrawlErrorType;
import org.genedb.crawl.CrawlException;

import org.genedb.crawl.model.CrawlError;

public class CrawlMappingExceptionResolver extends SimpleMappingExceptionResolver {
	
	private Logger logger = Logger.getLogger(CrawlMappingExceptionResolver.class);
	
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, 
			Object handler, Exception ex) {
		
		ModelAndView mav = new ModelAndView("service:");
		CrawlError error = new CrawlError();
		int status = 500; /* default */

		if (ex instanceof CrawlException) {
			error.setException((CrawlException)ex);
		} else {
			error.setException(new CrawlException(ex.getMessage(), CrawlErrorType.MISC_ERROR));
		}
		
		logger.error(ex.getStackTrace());

		switch(error.type) {
			case DATA_NOT_FOUND:
			  status = 404;
			  break;
			case INVALID_DATE:
			case BAD_PARAMETER:
			case MISSING_PARAMETER:
              status = 400;
              break;
            default:
              break;
		}
		
		ex.printStackTrace();
		mav.addObject("error" , error);
		response.setStatus(status);

		return mav;
	}
	
}

