package org.genedb.crawl.model;

public class FeatureRelationship {
	
	public Integer feature_relationship_id;
	
	public Feature subject;
	public Feature object;
	public String value;
	public Cvterm type;
	public Integer rank;
}
