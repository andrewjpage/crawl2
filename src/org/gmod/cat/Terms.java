package org.gmod.cat;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface Terms {
	List<Integer> getCvtermIDs(@Param("cv") String cv, @Param("cvterms") String[] cvterms);
	Integer getCvtermID(@Param("cv") String cv, @Param("cvterm") String cvterm);
}
