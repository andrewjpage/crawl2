package org.genedb.crawl.model.interfaces;

import org.genedb.crawl.CrawlException;
import org.genedb.crawl.model.MappedOrganismList;

public interface Organisms {
	MappedOrganismList list() throws CrawlException;
}
