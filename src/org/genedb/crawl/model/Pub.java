package org.genedb.crawl.model;

import javax.xml.bind.annotation.XmlAttribute;

import org.simpleframework.xml.Root;

@Root
public class Pub {
	
	@XmlAttribute
	public String uniqueName;
	
	@XmlAttribute
	public String accession;
	
	@XmlAttribute
	public String database;
}
