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
	
	private boolean converted = false;
		
	public void run() throws IOException, ParseException {
		
		init();
		
		if (refs != null) {
		    splitAlignments(refs);
		} else {
			String alignments = elasticSearchProperties.getProperty("alignments");
			if (alignments != null) {
			    splitAlignments(alignments);
			}
		}
		
		if (! converted) {
			logger.warn("Did not perform any conversions - please supply a reference JSON block " +
				"either on the command line (-r) or in the alignments section of the properties file.");
		}
		
		logger.debug("Complete");
		
	}
	
	private void splitAlignments(String alignments) throws JsonParseException, JsonMappingException, IOException, ParseException {
	    String[] split = alignments.split(",");
        for (String alignment : split) { 
            convertAlignments(alignment);
        }
	}
	
	private void convertAlignments(String alignmentsString) throws JsonParseException, JsonMappingException, IOException, ParseException {
		
		logger.info("Strying to read in " + alignmentsString);
		
		Alignments alignments = jsonIzer.fromStringOrFile(alignmentsString, Alignments.class);
		
		if (alignments.references != null) {
			for(Reference ref : alignments.references) {
				logger.info("Converting organism reference " + ref.file + " : " + ref.organism);
				organismsMapper.createOrUpdate(ref.organism);
				convertPath(ref.file,ref.organism);
				converted = true;
			}
		}
	}
	
	
	
	public static void main(String[] args) throws Exception {
		new ReferenceIndexBuilder().prerun(args);
	}
}
