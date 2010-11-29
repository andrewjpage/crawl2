package org.genedb.crawl.model;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("service")
public class Service {
	
	@XStreamAsAttribute()
	public String name;
	public String description;
	public List<Resource> resources = new ArrayList<Resource>();
}
