package org.genedb.crawl.elasticsearch.index.gff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.genedb.crawl.elasticsearch.index.IndexBuilder;
import org.genedb.crawl.elasticsearch.mappers.ElasticSearchFeatureMapper;
import org.genedb.crawl.elasticsearch.mappers.ElasticSearchOrganismsMapper;
import org.genedb.crawl.model.Organism;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;


public class GFFIndexBuilder extends IndexBuilder {
	
	private static Logger logger = Logger.getLogger(GFFIndexBuilder.class);
	
	@Option(name = "-g", aliases = {"--gffs"}, usage = "The path to the GFF folder", required = true)
	public String gffs;
		
	@Option(name = "-oc", aliases = {"--organism_common_name"}, usage = "The organism's common name", required = true)
	public String commonName;
	
	@Option(name = "-og", aliases = {"--organism_genus"}, usage = "The organism's genus")
	public String genus;
	
	@Option(name = "-os", aliases = {"--organism_species"}, usage = "The organism's species")
	public String species;
	
	@Option(name = "-ot", aliases = {"--organism_taxon_id"}, usage = "The organism's taxonID")
	public Integer taxonID;
	
	@Option(name = "-oid", aliases = {"--organism_id"}, usage = "The organism's common name")
	public Integer organismID;
	
	@Option(name = "-ott", aliases = {"--organism_translation_table"}, usage = "The organism's translation table")
	public Integer translationTable;
	
	
	
	private ElasticSearchFeatureMapper featureMapper;
	private ElasticSearchOrganismsMapper organismsMapper;
	
	public void run() throws IOException, ParseException, SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		
		setupIndex();
		
		featureMapper = new ElasticSearchFeatureMapper();
		featureMapper.setConnection(connection);
		
		organismsMapper = new ElasticSearchOrganismsMapper();
		organismsMapper.setConnection(connection);
		
		convertPath(gffs, getAndPossiblyStoreOrganism());
		
		logger.debug("Complete");
		
	}
	
	private Organism getAndPossiblyStoreOrganism() throws JsonParseException, JsonMappingException, IOException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		
		Organism organism = new Organism();
		organism.common_name = commonName;
		
		if (genus != null) {
			organism.genus = genus;
		}
		if (species != null) {
			organism.species = species;
		}
		if (taxonID != null) {
			organism.taxonID = taxonID;
		}
		if (translationTable != null) {
			organism.translation_table = translationTable;
		}
		if (organismID != null) {
			organism.ID = organismID;
		}
		
		organismsMapper.createOrUpdate(organism);
		
		return organism;
	}
	
	private void convertPath(String path, Organism organism) throws ParseException, IOException {
		
		File gffFile = new File(path);
		
		GFFFileFilter filter = new GFFFileFilter();
		filter.filter_set = GFFFileFilter.GFFFileExtensionSet.ALL;
		
		if (gffFile.isDirectory()) {
			for (File f : gffFile.listFiles(filter)) {
				convertFile(f, organism);
				f = null;
			}
		} else {
			convertFile(gffFile, organism);
			gffFile = null;
		}
		
	}
	
	private void convertFile(File gffFile, Organism organism) throws ParseException, IOException {
		BufferedReader reader = getReader(gffFile);
		new GFFAnnotatationAndFastaExtractor(reader, organism, featureMapper);
	}
	

	private BufferedReader getReader(File file) throws IOException {
		
		BufferedReader reader = null;
		
		FileInputStream fileStream = new FileInputStream(file);
		
		if (file.getName().endsWith("gz")) {
			logger.info("unzipping");
		    reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(fileStream)));
		} else {
			reader = new BufferedReader(new InputStreamReader(fileStream));
		}
		
		return reader;
	}
	
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws IllegalAccessException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 */
	public static void main(String[] args) throws IOException, ParseException, SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		GFFIndexBuilder gffIndexBuilder = new GFFIndexBuilder();
		CmdLineParser parser = new CmdLineParser(gffIndexBuilder);
		
		try {
			
			parser.parseArgument(args);
		
			if (gffIndexBuilder.help) {
				parser.setUsageWidth(80);
	            parser.printUsage(System.out);
	            System.exit(1);
			}
			
			gffIndexBuilder.run();
		
		} catch (CmdLineException e) {
			logger.error(e.getMessage());
            parser.setUsageWidth(80);
            parser.printUsage(System.out);
            System.exit(1);
		} finally {
			
			gffIndexBuilder.closeIndex();
		}
		 
	}

}
