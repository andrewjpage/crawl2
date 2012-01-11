package org.genedb.crawl.modelling;

import java.util.ArrayList;

import org.genedb.crawl.model.Cvterm;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.Property;

public class RegionFeatureBuilder {
	
	private Feature region = new Feature();
	
	public RegionFeatureBuilder(String uniquename, int organism_id) {
		region.uniqueName = uniquename;
		region.organism_id = organism_id;
		region.type = new Cvterm();
		region.type.name = "region";
		region.properties = new ArrayList<Property>();
		
		//region.topLevel = true;
	}
	
	public void setSequenceFile(String filePath) {
	    Property prop = new Property();
	    prop.name = "file";
	    prop.value = filePath;
	    region.properties.add(prop);
	}
	
	public Feature getRegion() {
		return region;
	}
}