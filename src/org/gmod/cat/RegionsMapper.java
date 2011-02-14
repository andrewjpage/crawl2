package org.gmod.cat;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.LocationBoundaries;

public interface RegionsMapper {
	
	public LocationBoundaries locationsMinAndMaxBoundaries(
			@Param("region") String region, 
			@Param("start") int start, 
			@Param("end") int end, 
			@Param("types") List<Integer> types);
	
	public List<LocatedFeature> locations(
			@Param("region") String region, 
			@Param("start") int start, 
			@Param("end") int end,
			@Param("exclude") List<String> exclude);
	
	public String sequence(@Param("region") String region);
	public List<String> inorganism(@Param("organismid") int organismid);
}
