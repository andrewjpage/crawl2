package org.genedb.crawl.model.interfaces;


import java.util.List;

import org.genedb.crawl.CrawlException;
import org.genedb.crawl.model.MappedOrganism;

public interface Organisms {
	
	List<MappedOrganism> list() throws CrawlException;
	MappedOrganism getByID(int ID) throws CrawlException;
	MappedOrganism getByTaxonID(String taxonID) throws CrawlException;
	MappedOrganism getByCommonName(String commonName) throws CrawlException;
	
}
