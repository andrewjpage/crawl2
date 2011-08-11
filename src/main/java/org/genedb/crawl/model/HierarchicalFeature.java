package org.genedb.crawl.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class HierarchicalFeature extends Feature {
	
	@XmlElement(name="child")
	public List<HierarchicalFeature> children = new ArrayList<HierarchicalFeature>();
	
	@XmlElement(name="parent")
	public List<HierarchicalFeature> parents = new ArrayList<HierarchicalFeature>();	
	
	@XmlAttribute
	public String relationship;
	
	@XmlAttribute
	public String relationshipType;
	
	@XmlAttribute
    public String type;
	
}
