package org.genedb.crawl.model;

import javax.xml.bind.annotation.XmlAttribute;

public class Coordinates {
	
	@XmlAttribute
	public String region;
	
	@XmlAttribute
	public int fmin;
	
	@XmlAttribute
	public int fmax;
	
	@XmlAttribute
	public Integer phase;
	
	@XmlAttribute
	public int strand;
}
