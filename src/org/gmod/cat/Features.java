package org.gmod.cat;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.genedb.crawl.model.HierarchyGeneFetchResult;
import org.genedb.crawl.model.HierarchyRelation;
import org.genedb.crawl.model.RegionCoordinates;
import org.genedb.crawl.model.RegionCoordinatesList;

public interface Features {
	int getFeatureID(String uniquename);
	List<HierarchyGeneFetchResult> getGeneForFeature(@Param("features") List<String> features );
	List<HierarchyRelation> getRelationshipsParents(@Param("feature") String feature, @Param("relationships") List<Integer> relationships );
	List<HierarchyRelation> getRelationshipsChildren(@Param("feature") String feature, @Param("relationships") List<Integer> relationships );
	RegionCoordinatesList coordinates(@Param("features") List<String> features, @Param("region") String region );
}
