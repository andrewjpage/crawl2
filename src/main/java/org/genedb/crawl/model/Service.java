package org.genedb.crawl.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Service {
	
	@XmlAttribute
	public String name;
	
	public String description;
	
	@XmlElement
	public List<Resource> resources = new ArrayList<Resource>();
}
