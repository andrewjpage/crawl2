package org.genedb.crawl.model;

import java.util.Hashtable;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamAlias("query")
public class MappedQuery {
	
	public boolean contained;
	public int start;
	public int end;
	public String sequence;
	public int count;
	public String time;
	public int filter;
	
	@XStreamConverter(value = HashConverter.class)
	public Hashtable<String, List<Object>> records = new Hashtable<String, List<Object>>();
	
}
