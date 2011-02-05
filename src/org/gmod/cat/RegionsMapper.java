package org.gmod.cat;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.genedb.crawl.model.LocationBoundaries;
import org.genedb.crawl.model.LocationRegion;

public interface RegionsMapper {
	
	public LocationBoundaries locationsMinAndMaxBoundaries(
			@Param("regionid") int regionid, 
			@Param("start") int start, 
			@Param("end") int end, 
			@Param("types") List<Integer> types);
	
	public List<LocationRegion> locations(
			@Param("regionid") int regionid, 
			@Param("start") int start, 
			@Param("end") int end,
			@Param("exclude") List<String> exclude);
	
	public String sequence(@Param("regionid") int regionid);
	public List<String> inorganism(@Param("organismid") int organismid);
}
