package org.genedb.crawl;

public class CrawlException extends Exception {
	
	public CrawlErrorType type;
	
	public CrawlException (String message, CrawlErrorType type) {
		super(message);
		this.type = type;
	}
	
}
