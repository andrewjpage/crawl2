package org.genedb.crawl.model;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("feature")
public class HierarchicalFeature {
	
	public List<HierarchicalFeature> children = new ArrayList<HierarchicalFeature>();
	public List<HierarchicalFeature> parents = new ArrayList<HierarchicalFeature>();
	
	@XStreamAsAttribute
	public String relationship;
	
	@XStreamAsAttribute
	public String type;
	
	@XStreamAsAttribute
	public String uniqueName;
	
	@XStreamAsAttribute
	public String name;
}
