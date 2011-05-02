package org.genedb.crawl.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;

public class Resource {
	
	@XmlAttribute
	public String name;
	
	public String description;
	
	public List<Argument> args = new ArrayList<Argument>();
	
	public String returnType;
}
