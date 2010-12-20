package org.genedb.crawl.model;

import java.util.List;

public class Cvterm {
	
	public Cv cv;
	public String name;
	
	public String accession;
	
	/**
	 *  This is not a primitive boolean so it can be nullable (or else the GSON sets it to false if unset).
	 */
	public Boolean is_not;
	
	public List<Dbxref> dbxrefs;
	public List<CvtermProp> props;
	public List<Pub> pubs;
	
	
	
}
