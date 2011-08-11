package org.genedb.crawl.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.genedb.crawl.model.Dbxref;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.Pub;

public interface FeatureCvtermMapper {
	public List<Pub> featureCvTermPubs(int feature_cvterm_id);
	public List<Dbxref> featureCvTermDbxrefs(int feature_cvterm_id);
	Integer countInOrganism(@Param("organism") Organism organism, @Param("cv") String cv, @Param("cvterm") String cvterm);
}
