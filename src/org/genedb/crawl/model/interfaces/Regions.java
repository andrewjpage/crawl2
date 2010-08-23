package org.genedb.crawl.model.interfaces;

import org.genedb.crawl.CrawlException;
import org.genedb.crawl.model.Locations;

public interface Regions {
	Locations locations(String region, int start, int end) throws CrawlException;
}
