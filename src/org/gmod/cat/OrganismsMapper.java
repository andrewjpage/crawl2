package org.gmod.cat;


import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.genedb.crawl.CrawlException;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.OrganismProp;

public interface OrganismsMapper {
	
	List<Organism> list() throws CrawlException;
	Organism getByID(int ID) throws CrawlException;
	Organism getByTaxonID(String taxonID) throws CrawlException;
	Organism getByCommonName(String commonName) throws CrawlException;
	OrganismProp getOrganismProp(@Param("ID") int ID, @Param("cv") String cv, @Param("cvterm") String cvterm);
	
}
