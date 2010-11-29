package org.genedb.crawl.model;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("response")
public class Locations   {
	
	@XStreamAsAttribute()
	public int actual_start;
	
	@XStreamAsAttribute()
	public int actual_end;
	
	public String[] exclude;
	
	@XStreamAsAttribute()
	public String region;
	
	@XStreamAsAttribute()
	public int request_start;
	
	@XStreamAsAttribute()
	public int request_end;
	
	public List<LocationRegion> features = new ArrayList<LocationRegion>();
	
	
	
}

