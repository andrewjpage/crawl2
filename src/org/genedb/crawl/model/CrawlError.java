package org.genedb.crawl.model;

import org.genedb.crawl.CrawlErrorType;
import org.genedb.crawl.CrawlException;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("error")
public class CrawlError {
	
	@XStreamAsAttribute
	public String message;
	
	@XStreamAsAttribute
	public int code = typeCode(CrawlErrorType.MISC_ERROR);
	
	@XStreamAsAttribute
	public CrawlErrorType type = CrawlErrorType.MISC_ERROR;
	
	public void setErrorType(CrawlErrorType type) {
		this.type = type;
		this.code = typeCode(type);
	}
	
	public void setException(CrawlException e) {
		this.type = e.type;
		this.code = typeCode(e.type);
		this.message = e.getMessage();
	}
	
	private int typeCode(CrawlErrorType t) {
		return t.ordinal() + 1;
	}
	
}

