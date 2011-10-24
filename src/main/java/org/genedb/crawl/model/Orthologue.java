package org.genedb.crawl.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;


public class Orthologue extends Feature implements Serializable {
	
	@XmlAttribute(required=false)
	public String clusterName;
	
	@XmlAttribute(required=false)
	public String orthologyType;
	
	@XmlAttribute(required=false)
	public String program;
	
	@XmlElement
    public List<Feature> cluster;
	
	
}
