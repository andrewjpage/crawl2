package org.genedb.crawl.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Dbxref implements Serializable {
	
	@XmlElement
	public Db db;
	
	@XmlAttribute
	public String accession;
	
	@XmlAttribute
    public String version;
	
	@XmlAttribute
    public String description;
}
