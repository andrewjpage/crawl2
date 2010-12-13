package org.gmod.cat;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.HierarchyGeneFetchResult;
import org.genedb.crawl.model.HierarchyRelation;

public interface Features {
	
	int getFeatureID(String uniquename);
	
	List<HierarchyGeneFetchResult> getGeneForFeature(@Param("features") List<String> features );
	List<HierarchyRelation> getRelationshipsParents(@Param("feature") String feature, @Param("relationships") List<Integer> relationships );
	List<HierarchyRelation> getRelationshipsChildren(@Param("feature") String feature, @Param("relationships") List<Integer> relationships );
	
	List<Feature> coordinates(@Param("features") List<String> features, @Param("region") String region );
	List<Feature> synonyms(@Param("features") List<String> features, @Param("types") List<String> types );
	List<Feature> synonymsLike(@Param("term") String term, @Param("regex") boolean regex, @Param("region") String region);
	List<Feature> featuresLike(@Param("term") String term, @Param("regex") boolean regex, @Param("region") String region);
	List<Feature> properties(@Param("features") List<String> features);
	List<Feature> pubs (@Param("features") List<String> features);
}
