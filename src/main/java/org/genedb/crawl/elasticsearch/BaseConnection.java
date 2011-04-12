package org.genedb.crawl.elasticsearch;

public class BaseConnection {
	
	String index;
	String featureType;
	String regionType;
	String organismType;
	
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getFeatureType() {
		return featureType;
	}
	public void setFeatureType(String featureType) {
		this.featureType = featureType;
	}
	public String getRegionType() {
		return regionType;
	}
	public void setRegionType(String regionType) {
		this.regionType = regionType;
	}
	public String getOrganismType() {
		return organismType;
	}
	public void setOrganismType(String organismType) {
		this.organismType = organismType;
	}
	
	
	
}
