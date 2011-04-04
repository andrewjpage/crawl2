package org.genedb.crawl.model;

import java.io.Serializable;

public class Sequence implements Serializable {
	public int start;
	public int end;
	public Integer length;
	public String dna;
	public String region;
	public Integer organism_id;
}
