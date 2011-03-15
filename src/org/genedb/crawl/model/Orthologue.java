package org.genedb.crawl.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;

public class Orthologue extends Feature implements Serializable {
	
	@XmlAttribute(required=false)
	public String clusterName;
	
	@XmlAttribute(required=false)
	public String orthologyType;
	
	@XmlAttribute(required=false)
	public String program;
	
	
	
	
}
