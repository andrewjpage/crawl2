package org.genedb.crawl.mappers;


import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.genedb.crawl.CrawlException;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.OrganismProp;

public interface OrganismsMapper {
	
	List<Organism> list() ;
	Organism getByID(int ID) ;
	Organism getByTaxonID(String taxonID) ;
	Organism getByCommonName(String commonName) ;
	OrganismProp getOrganismProp(@Param("ID") int ID, @Param("cv") String cv, @Param("cvterm") String cvterm);
	
}
