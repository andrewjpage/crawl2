package org.genedb.crawl.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="TheProperty")
public class Property implements Serializable {
	
	@XmlAttribute
	public String name;
	
	@XmlAttribute
	public String value;
	
	@XmlAttribute
	public int rank;
	
	@XmlElement
	public Cvterm type;
}
