package org.genedb.crawl.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("synonym")
public class Synonym {
	
	@XStreamAsAttribute
	public String synonym;
	
	@XStreamAsAttribute
	public String synonymtype;
	
	@XStreamAsAttribute
	public boolean is_current;
}
