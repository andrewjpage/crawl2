package org.genedb.crawl.model;

import javax.xml.bind.annotation.XmlAttribute;

public class Dbxref {
	
	@XmlAttribute
	public String database;
	
	@XmlAttribute
	public String accession;
}
