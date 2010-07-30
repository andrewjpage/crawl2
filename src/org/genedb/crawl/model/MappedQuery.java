package org.genedb.crawl.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("query")
public class MappedQuery extends BaseResult {
	
	public boolean contained;
	public int start;
	public int end;
	public String sequence;
	
	public Map<String, List> records = new HashMap<String, List>();
	
}
