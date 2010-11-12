package org.genedb.crawl.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("organism")
public class MappedOrganism {
	public String genus;
	public String species;
	public String common_name;
	public String taxonID;
	public String translation_table;
	public String name;
	public String ID;
}
