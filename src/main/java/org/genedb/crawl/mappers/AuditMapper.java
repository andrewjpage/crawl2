package org.genedb.crawl.mappers;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.genedb.crawl.model.Feature;

public interface AuditMapper {
	
	Boolean exists();
	List<Feature> deleted(@Param("date") Date date);
}
