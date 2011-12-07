package org.genedb.crawl.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

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
	
	@XmlAttribute
    public String strain;
	
	@XmlElement(name="property", required=false)
    @XmlElementWrapper(name="properties")
    public List<Property> properties;
	
}
