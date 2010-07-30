package org.genedb.crawl.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("coverage")
public class MappedCoverage extends BaseResult {
	
	public int[] coverage;
	public int start;
	public int end;
	public int window;
	public int max;
	public long time;
	public int bins;
	
}
