package org.genedb.crawl.model;

import java.util.ArrayList;
import java.util.List;

public class Resource {
	
	public String name;
	
	public String description;
	
	public List<Argument> args = new ArrayList<Argument>();
	
	public String returnType;
}
