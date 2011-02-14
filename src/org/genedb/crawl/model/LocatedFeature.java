package org.genedb.crawl.model;

import javax.xml.bind.annotation.XmlAttribute;

public class LocatedFeature extends Feature {
	
	@XmlAttribute(required=false)
	public Integer fmin;
	
	@XmlAttribute(required=false)
	public Integer fmax;
	
	@XmlAttribute(required=false)
	public String region;
	
	@XmlAttribute(required=false)
	public String parent;
	
	@XmlAttribute(required=false)
	public String parentRelationshipType;
	
	@XmlAttribute
	public Integer phase;
	
	@XmlAttribute
	public int strand;
	
	public Boolean fmin_partial;
	public Boolean fmax_partial;
	
}
