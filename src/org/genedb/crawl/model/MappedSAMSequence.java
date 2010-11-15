package org.genedb.crawl.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("sequence")
public class MappedSAMSequence {
	
	@XStreamAsAttribute
	public int index;
	
	@XStreamAsAttribute
	public int length;
	
	@XStreamAsAttribute
	public String name;
}
