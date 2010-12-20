package org.gmod.cat;


import java.util.List;

import org.genedb.crawl.CrawlException;
import org.genedb.crawl.model.Organism;

public interface Organisms {
	
	List<Organism> list() throws CrawlException;
	Organism getByID(int ID) throws CrawlException;
	Organism getByTaxonID(String taxonID) throws CrawlException;
	Organism getByCommonName(String commonName) throws CrawlException;
	
}
