package org.genedb.crawl.model;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("hierarchy")
public class HierarchicalFeatureList {
	
	@XStreamImplicit
	public List<HierarchicalFeature> hierarchy = new ArrayList<HierarchicalFeature>();
}
