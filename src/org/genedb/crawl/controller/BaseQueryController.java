package org.genedb.crawl.controller;

import javax.servlet.http.HttpServletRequest;

import org.genedb.crawl.model.BaseResult;
import org.genedb.crawl.model.ResponseWrapper;
import org.genedb.crawl.model.interfaces.QuerySource;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseQueryController {

	
	protected ResponseWrapper generateResponseWrapper(HttpServletRequest request, Object model) {
		ResponseWrapper wrapper = new ResponseWrapper();
		wrapper.name = request.getServletPath();
		wrapper.results = model;
		return wrapper;
		
	}
	
}