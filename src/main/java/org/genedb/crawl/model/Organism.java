package org.genedb.crawl.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;

public class Organism implements Serializable {
	
	@XmlAttribute
	public String genus;
	@XmlAttribute
	public String species;
	@XmlAttribute
	public String common_name;
	@XmlAttribute
	public Integer taxonID;
	@XmlAttribute
	public Integer translation_table;
	@XmlAttribute
	public String name;
	@XmlAttribute
	public Integer ID;
	
}
