package org.genedb.crawl.model;

import javax.xml.bind.annotation.XmlAttribute;

public class Orthologue extends Feature {
	
	@XmlAttribute(required=false)
	public String clusterName;
	
	@XmlAttribute(required=false)
	public String orthologyType;
	
	@XmlAttribute(required=false)
	public String program;
	
	
	
	
}
