package org.genedb.crawl.elasticsearch.mappers;

import java.util.List;

import org.genedb.crawl.model.Cvterm;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.FeatureProperty;
import org.gmod.cat.FeatureMapper;
import org.springframework.stereotype.Component;

@Component
public class ElasticSearchFeatureMapper extends ElasticSearchBaseMapper implements FeatureMapper {

	@Override
	public Feature get(String uniqueName, String organism_id, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FeatureProperty> properties(Feature feature) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Cvterm> terms(Feature feature) {
		// TODO Auto-generated method stub
		return null;
	}

}
