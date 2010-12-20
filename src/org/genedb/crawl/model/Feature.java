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
	public List<Dbxref> dbxrefs;
	public List<Cvterm> terms;
	
	public Cvterm type;
	
	public List<FeatureRelationship> relationships;
	
	public Organism organism;
	
	public List<Feature> orthologues;
	public AnalysisFeature analysisFeature;
	public List<String> products;
	
	public String relationship;
	
	
}
