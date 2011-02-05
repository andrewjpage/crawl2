package org.genedb.crawl.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;


public class MappedCoverage {
	
	@XmlElementWrapper(name="data")
	@XmlElement(name="d")
	public int[] data;
	
	@XmlAttribute
	public int start;
	
	@XmlAttribute
	public int end;
	
	@XmlAttribute
	public int window;
	
	@XmlAttribute
	public int max;
	
	@XmlAttribute
	public int bins;
	
	@XmlAttribute
	public String time;
	
}
