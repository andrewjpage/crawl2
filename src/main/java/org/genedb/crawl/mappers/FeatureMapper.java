package org.genedb.crawl.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.genedb.crawl.model.Coordinates;
import org.genedb.crawl.model.Cvterm;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.FeatureProperty;
import org.genedb.crawl.model.Gene;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.Transcript;

public interface FeatureMapper {
	
	Feature get(
		@Param("uniqueName") String uniqueName,
		@Param("organism_id") String organism_id,
		@Param("name") String name);
	
	List<FeatureProperty> properties(Feature feature);
	
	List<Cvterm> terms (Feature feature);
	
	List<Coordinates> coordinates(Feature feature);
	
	void delete(Feature feature);
	
	LocatedFeature getOfType(
			@Param("uniqueName") String uniqueName,
			@Param("organism_id") String organism_id,
			@Param("name") String name,
			@Param("type") String type);
	
	List<Transcript> transcripts(@Param("gene") Gene gene, @Param("exons") boolean exons);
	
}
