package org.genedb.crawl.model;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("response")
public class XMLResponseWrapper {
	
	public List<Object> results = new ArrayList<Object>();
	
	@XStreamAsAttribute
	public String name;
	
	public XMLResponseWrapper (String name, Object model) {
		this.name = name;
		results.add(model);
	}
}

