package org.genedb.crawl.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("response")
public class ResponseWrapper {
	
	public Object results;
	
	@XStreamAsAttribute
	public String name;
}
