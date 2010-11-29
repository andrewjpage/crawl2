package org.genedb.crawl.model;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("resource")
public class Resource {
	
	@XStreamAsAttribute()
	public String name;
	
	public String description;
	
	public List<Argument> args = new ArrayList<Argument>();
	
	@XStreamAsAttribute()
	public String returnType;
}
