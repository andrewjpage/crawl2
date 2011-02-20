package org.genedb.crawl.model;

import javax.xml.bind.annotation.XmlAttribute;

public class Coordinates {
	
	@XmlAttribute(required=true)
	public String region;
	
	@XmlAttribute(required=true)
	public int fmin;
	
	@XmlAttribute(required=true)
	public int fmax;
	
	@XmlAttribute(required=true)
	public Integer phase;
	
	@XmlAttribute(required=true)
	public int strand;
}
