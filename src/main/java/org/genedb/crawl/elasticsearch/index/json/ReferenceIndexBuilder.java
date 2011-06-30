package org.genedb.crawl.elasticsearch.index.json;

import java.io.IOException;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.genedb.crawl.elasticsearch.index.NonDatabaseDataSourceIndexBuilder;
import org.genedb.crawl.model.Alignments;
import org.genedb.crawl.model.Reference;
import org.kohsuke.args4j.Option;

public class ReferenceIndexBuilder extends NonDatabaseDataSourceIndexBuilder {
	
	static Logger logger = Logger.getLogger(OrganismIndexBuilder.class);
	
	@Option(name = "-r", aliases = {"--refs"}, usage = "The path a JSON file containing reference sequences", required = false)
	public String refs;
		
	public void run() throws IOException, ParseException {
		
		init();
		
		if (refs != null) {
			convertJson(refs);
		}
		
		logger.debug("Complete");
		
	}
	
	private void convertJson(String json) throws JsonParseException, JsonMappingException, IOException, ParseException {
		
		logger.info("Strying to read in " + json);
		
		Alignments store = jsonIzer.fromStringOrFile(json, Alignments.class);
		
		if (store.references != null) {
			for(Reference ref : store.references) {
				logger.info("Converting organism reference " + ref.file + " : " + ref.organism);
				organismsMapper.createOrUpdate(ref.organism);
				convertPath(ref.file,ref.organism);
			}
		}
	}
	
	
	
	public static void main(String[] args) throws Exception {
		new ReferenceIndexBuilder().prerun(args);
	}
}
