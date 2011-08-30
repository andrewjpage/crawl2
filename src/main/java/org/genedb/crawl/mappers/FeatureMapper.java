package org.genedb.crawl.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.genedb.crawl.model.Coordinates;
import org.genedb.crawl.model.Cvterm;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.FeatureRelationship;
import org.genedb.crawl.model.Property;
import org.genedb.crawl.model.Gene;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.Pub;
import org.genedb.crawl.model.Synonym;
import org.genedb.crawl.model.Transcript;

public interface FeatureMapper {
	
	Feature get(
		@Param("uniqueName") String uniqueName,
		@Param("name") String name,
		@Param("organism_id") Integer organism_id);
	
	List<Property> properties(Feature feature);
	
	List<Cvterm> terms (Feature feature);
	
	List<Coordinates> coordinates(Feature feature);
	
	List<Synonym> synonyms(Feature feature);
	List<Pub> pubs (Feature feature);
	
	void delete(Feature feature);
	
	LocatedFeature getOfType(
			@Param("uniqueName") String uniqueName,
			@Param("organism_id") Integer organism_id,
			@Param("name") String name,
			@Param("type") String type);
	
	List<Transcript> transcripts(@Param("gene") Gene gene, @Param("exons") boolean exons);
	
	List<FeatureRelationship> parents(@Param("feature") Feature feature, @Param("relationships") List<Cvterm> relationships);
	List<FeatureRelationship> children(@Param("feature") Feature feature, @Param("relationships") List<Cvterm> relationships );
	
}
