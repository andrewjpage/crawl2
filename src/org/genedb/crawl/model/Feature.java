package org.genedb.crawl.model;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("feature")
public class Feature {
	
	@XStreamAsAttribute
	public String uniqueName;
	
	@XStreamAsAttribute
	public String name;
	
	public List<Synonym> synonyms;
	public List<Coordinates> coordinates;
	public List<FeatureProperty> properties;
	public List<Pub> pubs;
	
}
