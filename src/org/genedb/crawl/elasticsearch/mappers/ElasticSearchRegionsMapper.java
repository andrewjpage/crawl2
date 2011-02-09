package org.genedb.crawl.elasticsearch.mappers;

import java.util.List;

import org.genedb.crawl.model.LocationBoundaries;
import org.genedb.crawl.model.LocationRegion;
import org.gmod.cat.RegionsMapper;
import org.springframework.stereotype.Component;

@Component
public class ElasticSearchRegionsMapper extends ElasticSearchBaseMapper implements RegionsMapper {

	@Override
	public LocationBoundaries locationsMinAndMaxBoundaries(String region,
			int start, int end, List<Integer> types) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<LocationRegion> locations(String region, int start, int end,
			List<String> exclude) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sequence(String region) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> inorganism(int organismid) {
		// TODO Auto-generated method stub
		return null;
	}

}
