package org.genedb.crawl.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.genedb.crawl.model.Coordinates;
import org.genedb.crawl.model.Cvterm;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.FeatureProperty;

public interface FeatureMapper {
	
	Feature get(
		@Param("uniqueName") String uniqueName,
		@Param("organism_id") String organism_id,
		@Param("name") String name);
	
	List<FeatureProperty> properties(Feature feature);
	
	List<Cvterm> terms (Feature feature);
	
	List<Coordinates> coordinates(Feature feature);
	
	void delete(Feature feature);
	
}
