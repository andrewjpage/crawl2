package org.genedb.crawl.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;

public class Pub implements Serializable {
	
	@XmlAttribute
	public String uniqueName;
	
	@XmlAttribute
	public String accession;
	
	@XmlAttribute
	public String database;
}
