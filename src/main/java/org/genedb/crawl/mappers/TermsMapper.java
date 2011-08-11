package org.genedb.crawl.mappers;

import java.util.List;

import org.genedb.crawl.model.Organism;
import org.apache.ibatis.annotations.Param;

public interface TermsMapper {
	List<Integer> getCvtermIDs(@Param("cv") String cv, @Param("cvterms") String[] cvterms);
	Integer getCvtermID(@Param("cv") String cv, @Param("cvterm") String cvterm);
}
