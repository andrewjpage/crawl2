package org.genedb.crawl.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("coverage")
public class MappedCoverageItemOld extends BaseResult {
	public int start = 0;
	public int end = 0;
	public int count = 0;
	//public int count2 = 0;
}