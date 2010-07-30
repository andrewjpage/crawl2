package org.genedb.crawl.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("sequence")
public class MappedSAMSequence extends BaseResult {
	public int index;
	public int length;
}
