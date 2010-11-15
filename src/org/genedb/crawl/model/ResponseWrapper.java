package org.genedb.crawl.model;

import javax.servlet.http.HttpServletRequest;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("response")
public class ResponseWrapper {
	
	public Object response;
	
	@XStreamAsAttribute
	public String name;
	
	public ResponseWrapper (HttpServletRequest request, Object model) {
		name = request.getServletPath();
		response = model;
	}
}
