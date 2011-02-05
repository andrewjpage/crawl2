package org.genedb.crawl.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("features")
public class LocationRegion  {
	
	public Integer start;
	public Integer end;
	public String feature;
	public Boolean is_obsolete;
	public Integer phase;
	public int strand;
	public String type;
	
	public Boolean fmin_partial;
	public Boolean fmax_partial;
	public String part_of;
	
}
