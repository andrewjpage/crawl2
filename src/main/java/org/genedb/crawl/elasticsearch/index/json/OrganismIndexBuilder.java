package org.genedb.crawl.elasticsearch.index.json;

import java.io.IOException;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.genedb.crawl.elasticsearch.index.NonDatabaseDataSourceIndexBuilder;
import org.kohsuke.args4j.Option;

public class OrganismIndexBuilder extends NonDatabaseDataSourceIndexBuilder {

static Logger logger = Logger.getLogger(OrganismIndexBuilder.class);
	
	@Option(name = "-o", aliases = { "--organism" }, usage = "The organism, expressed as a JSON.", required = false)
	public String organism;
		
	public void run() throws IOException, ParseException, SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		
		init();
		getAndPossiblyStoreOrganism(organism);
		logger.debug("Complete");
		
	}
	
	public static void main(String[] args) throws Exception {
		new OrganismIndexBuilder().prerun(args).closeIndex();
	}

}
