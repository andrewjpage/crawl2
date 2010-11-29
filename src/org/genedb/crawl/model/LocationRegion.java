package org.genedb.crawl.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("features")
public class LocationRegion  {
	public String start;
	public String end;
	public String feature;
	public String is_obsolete;
	public String phase;
	public String strand;
	public String type;
	
	public String fmin_partial;
	public String fmax_partial;
	public String part_of;
}
