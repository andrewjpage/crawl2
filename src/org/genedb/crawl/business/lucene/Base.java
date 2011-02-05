package org.genedb.crawl.business.lucene;

import org.apache.log4j.Logger;
import org.genedb.crawl.business.AnnotationRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class Base {

	protected Logger logger = Logger.getLogger(RegionsQueries.class);
	
	@Autowired
	protected AnnotationRepository repo;

	public Base() {
		super();
	}

}