package org.genedb.crawl.model;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;



public class Analysis implements Serializable {
	
	@XmlAttribute
	public Integer analysis_id;
	@XmlAttribute
	public String name;
	@XmlAttribute
	public String description;
	@XmlAttribute
	public String program;
	@XmlAttribute
	public String programversion;
	@XmlAttribute
	public String algorithm;
	@XmlAttribute
	public String sourcename;
	@XmlAttribute
	public String sourceversion;
	@XmlAttribute
	public String sourceuri;
	@XmlAttribute
	public Date timeexecuted;
	
}
