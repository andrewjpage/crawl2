package org.genedb.crawl.model;

import java.util.Hashtable;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("query")
public class MappedQuery extends BaseResult {
	
	public boolean contained;
	public int start;
	public int end;
	public String sequence;
	
	public Hashtable<String, List<Object>> records = new Hashtable<String, List<Object>>();
	
	
}
