package org.gmod.cat;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.genedb.crawl.model.Cvterm;

public interface TermsMapper {
	List<Integer> getCvtermIDs(@Param("cv") String cv, @Param("cvterms") String[] cvterms);
	Integer getCvtermID(@Param("cv") String cv, @Param("cvterm") String cvterm);
	//Cvterm getCvterm(@Param("cv") String cv, @Param("cvterm") String cvterm);
}
