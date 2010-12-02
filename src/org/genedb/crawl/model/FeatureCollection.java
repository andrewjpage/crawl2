package org.genedb.crawl.model;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("features")
public class FeatureCollection {
	
	@XStreamImplicit
	public List<Feature> results;
}
