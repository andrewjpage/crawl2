package org.genedb.crawl.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("argument")
public class Argument {
	
	@XStreamAsAttribute()
	public String name;
	
	@XStreamAsAttribute()
	public String type;
	
	@XStreamAsAttribute()
	public String defaultValue;
	
	public String description;
}
