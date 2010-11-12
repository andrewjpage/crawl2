package org.genedb.crawl.model;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("response")
public class Locations extends BaseResult  {
	
	@XStreamAsAttribute()
	public String name;
	
	public int actual_start;
	public int actual_end;
	public String[] exclude;
	public String region;
	
	public int request_start;
	public int request_end;
	
	public List<LocationRegion> features = new ArrayList<LocationRegion>();
	
	
	
}

