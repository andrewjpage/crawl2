package org.genedb.crawl.elasticsearch.index;

import org.genedb.crawl.model.Cvterm;
import org.genedb.crawl.model.Feature;

public class RegionFeatureBuilder {
	private StringBuilder buffer = new StringBuilder();
	private Feature region = new Feature();
	
	public RegionFeatureBuilder(String uniquename, int organism_id) {
		region.uniqueName = uniquename;
		region.organism_id = organism_id;
		region.type = new Cvterm();
		region.type.name = "region";
		//region.topLevel = true;
	}
	
	public void addSequence(String line) {
		buffer.append(line);
	}
	
	public Feature getRegion() {
		region.residues = buffer.toString();
		return region;
	}
}