package org.genedb.crawl.model;

public class ResponseWrapper {
	
	public Object response;
	
	public String name;
	
	public ResponseWrapper (String name, Object model) {
		this.name = name;
		this.response = model;
	}
}
