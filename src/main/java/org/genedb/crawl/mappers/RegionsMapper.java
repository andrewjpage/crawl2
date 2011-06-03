package org.genedb.crawl.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.genedb.crawl.model.Cvterm;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.LocationBoundaries;
import org.genedb.crawl.model.Sequence;

public interface RegionsMapper {
	
	public LocationBoundaries locationsMinAndMaxBoundaries(
			@Param("region") String region, 
			@Param("start") int start, 
			@Param("end") int end, 
			@Param("types") List<String> types);
	
	public List<LocatedFeature> locations(
			@Param("region") String region, 
			@Param("start") int start, 
			@Param("end") int end,
			@Param("exclude") boolean exclude,
			@Param("types") List<String> types);
	
//	public List<LocatedFeature> locationsPaged(
//			@Param("region") String region, 
//			@Param("limit") int limit, 
//			@Param("offset") int offset,
//			@Param("exclude") boolean exclude,
//			@Param("types") List<String> types);
	
	public Sequence sequence(@Param("region") String region);
	
	public Sequence sequenceLength(@Param("region") String region);
	
	public Sequence sequenceTrimmed(
			@Param("region") String region,
			@Param("start") Integer start, 
			@Param("end") Integer end);
	
	public List<Feature> inorganism(
			@Param("organismid") int organismid,
			@Param("limit") Integer limit, 
			@Param("offset") Integer offset,
			@Param("type") String type);
	
	public List<Cvterm> typesInOrganism(
			@Param("organismid") int organismid);
	
}
