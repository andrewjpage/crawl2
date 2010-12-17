package org.genedb.crawl.model;

import java.util.List;

public class Cvterm {
	
	Cv cv;
	String name;
	
	String accession;
	boolean is_not;
	
	List<Dbxref> dbxrefs;
	List<CvtermProp> props;
	List<Pub> pubs;
	
	
}
