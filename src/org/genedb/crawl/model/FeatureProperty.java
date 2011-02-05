package org.genedb.crawl.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class FeatureProperty {
	
	@XmlAttribute
	public String name;
	
	@XmlAttribute
	public String value;
	
	@XmlAttribute
	public int rank;
	
	@XmlElement
	public Cvterm type;
}
