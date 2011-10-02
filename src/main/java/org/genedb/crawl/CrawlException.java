package org.genedb.crawl;

import org.genedb.crawl.model.CrawlError;

public class CrawlException extends RuntimeException {
	
	public CrawlErrorType type;
	
	public CrawlException (String message, CrawlErrorType type) {
		super(message);
		this.type = type;
	}
	
	public CrawlException (CrawlError error) {
	    super(error.message);
	    this.type = error.type;
	}
	
}
