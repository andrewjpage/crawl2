package org.genedb.crawl.elasticsearch.index.gff;

import java.io.IOException;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.genedb.crawl.elasticsearch.index.NonDatabaseDataSourceIndexBuilder;
import org.genedb.crawl.model.Organism;
import org.kohsuke.args4j.Option;


public class GFFIndexBuilder extends NonDatabaseDataSourceIndexBuilder {
	
	static Logger logger = Logger.getLogger(GFFIndexBuilder.class);
	
	@Option(name = "-g", aliases = {"--gffs"}, usage = "The path to the GFF folder", required = false)
	public String gffs;
	
	@Option(name = "-o", aliases = { "--organism" }, usage = "The organism, expressed as a JSON.", required = false)
	public String organism;
		
	public void run() throws IOException, ParseException, SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		
		init();
		
		if (gffs != null) {
			
			if (organism == null) {
				throw new RuntimeException("Please supply an organism if loading a gff because GFF files do not specify their organism");
			}
			
			Organism o = getAndPossiblyStoreOrganism(organism);
			convertPath(gffs,o);
		}
		
		logger.debug("Complete");
		
	}
	
	public static void main(String[] args) throws Exception {
		new GFFIndexBuilder().prerun(args);
	}

}
