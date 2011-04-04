package org.genedb.crawl.elasticsearch;

import org.elasticsearch.client.Client;

public interface Connection {
	
	public Client getClient();
	
	public String getIndex();
	public String getFeatureType();
	public String getRegionType();
	public String getOrganismType();
	
	public void setIndex(String index);
	public void setFeatureType(String featureType);
	public void setRegionType(String regionType);
	public void setOrganismType(String organismType);
	
}
