package org.genedb.crawl.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlType;

@XmlType(name="TheSequence")
public class Sequence implements Serializable {
	public Integer start;
	public Integer end;
	public Integer length;
	public String dna;
	public String region;
	public Integer organism_id;
}
