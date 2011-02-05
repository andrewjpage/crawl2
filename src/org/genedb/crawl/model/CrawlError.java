package org.genedb.crawl.model;

import javax.xml.bind.annotation.XmlAttribute;

import org.apache.log4j.Logger;
import org.genedb.crawl.CrawlErrorType;
import org.genedb.crawl.CrawlException;

public class CrawlError {
	
	private Logger logger = Logger.getLogger(CrawlError.class);
	
	@XmlAttribute
	public String message;
	
	@XmlAttribute
	public Integer code = typeCode(CrawlErrorType.MISC_ERROR);
	
	@XmlAttribute
	public CrawlErrorType type = CrawlErrorType.MISC_ERROR;
	
	public void setErrorType(CrawlErrorType type) {
		this.type = type;
		this.code = typeCode(type);
	}
	
	public void setException(CrawlException e) {
		type = e.type;
		code = typeCode(e.type);
		message = e.getMessage();
		logger.error(String .format("Error, type: %s, code: %s, message: %s", type, code, message));
	}
	
	private int typeCode(CrawlErrorType t) {
		return t.ordinal() + 1;
	}
	
}

