package org.genedb.crawl.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;

public class Dbxref implements Serializable {
	
	@XmlAttribute
	public String database;
	
	@XmlAttribute
	public String accession;
}
