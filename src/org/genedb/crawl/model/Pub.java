package org.genedb.crawl.model;

import javax.xml.bind.annotation.XmlAttribute;

public class Pub {
	
	@XmlAttribute
	public String uniqueName;
	
	@XmlAttribute
	public String accession;
	
	@XmlAttribute
	public String database;
}
