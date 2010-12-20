package org.genedb.crawl.model;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("results")
public class RegionsInOrganism {
	
	public Organism organism;
	
	@XStreamAlias("regions")
	public List<String> regions = new ArrayList<String>();
}
