package org.genedb.crawl.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class FeatureRelationship {
	
	@XmlAttribute
	public Integer feature_relationship_id;
	
	@XmlElement
	public Feature subject;
	
	@XmlElement
	public Feature object;
	
	@XmlAttribute
	public String value;
	
	@XmlElement
	public Cvterm type;
	
	@XmlAttribute
	public Integer rank;
}
