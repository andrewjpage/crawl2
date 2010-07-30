package org.genedb.crawl.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("session")
public class SessionInfo extends BaseResult {
	
	public String token;
	public String username;
	
}
