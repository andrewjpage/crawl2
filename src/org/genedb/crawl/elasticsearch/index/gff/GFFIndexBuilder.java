package org.genedb.crawl.elasticsearch.index.gff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
import org.genedb.crawl.elasticsearch.mappers.ElasticSearchRegionsMapper;
import org.genedb.crawl.model.Organism;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;


public class GFFIndexBuilder extends IndexBuilder {
	
	private static Logger logger = Logger.getLogger(GFFIndexBuilder.class);
	
	@Option(name = "-g", aliases = {"--gffs"}, usage = "The path to the GFF folder", required = false)
	public String gffs;
		
	@Option(name = "-o", aliases = {"--organism_common_name"}, usage = "The organism, expressed as a JSON.", required = true)
	public String organism;
	
		
	
	
	private ElasticSearchFeatureMapper featureMapper;
	private ElasticSearchOrganismsMapper organismsMapper;
	private ElasticSearchRegionsMapper regionsMapper;
	
	public void run() throws IOException, ParseException, SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		
		setupIndex();
		
		featureMapper = new ElasticSearchFeatureMapper();
		featureMapper.setConnection(connection);
		
		organismsMapper = new ElasticSearchOrganismsMapper();
		organismsMapper.setConnection(connection);
		
		regionsMapper = new ElasticSearchRegionsMapper();
		regionsMapper.setConnection(connection);
		
		Organism o = getAndPossiblyStoreOrganism();
		
		if (gffs != null) {
			convertPath(gffs, o);
		}
		
		logger.debug("Complete");
		
	}
	
	private Organism getAndPossiblyStoreOrganism() throws JsonParseException, JsonMappingException, IOException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		
		Organism userSuppliedOrganism = (Organism) jsonIzer.fromJson(organism, Organism.class);
		
		Organism organism = null;
		
		if (userSuppliedOrganism.ID != null) {
			logger.info("Getting by ID");
			try {
				organism = organismsMapper.getByID(userSuppliedOrganism.ID);
				logger.info("found!");
			} catch (Exception e) {
				logger.warn("Could not find an organism with this ID");
			}
		} 
		
		if (userSuppliedOrganism.common_name != null) {
			logger.info("Getting by common_name");
			try {
				organism = organismsMapper.getByCommonName(userSuppliedOrganism.common_name);
				logger.info("found!");
			} catch (Exception e) {
				logger.warn("Could not find an organism with this common_name.");
			}
		} 
		
		if (organism == null) {
			organism = userSuppliedOrganism;
			logger.warn("Could not find existing organism matching the one you supplied.");
			
			if (
					userSuppliedOrganism.common_name == null || 
					userSuppliedOrganism.ID == null || 
					userSuppliedOrganism.genus == null || 
					userSuppliedOrganism.species == null ||
					userSuppliedOrganism.translation_table == null ||
					userSuppliedOrganism.taxonID == null) {
				
				logger.error(String.format("Missing common_name? %s, ID %s, genus %s, species %s, translation_table %s, taxonID %s ",  
						userSuppliedOrganism.common_name == null, 
						userSuppliedOrganism.ID == null,  
						userSuppliedOrganism.genus == null, 
						userSuppliedOrganism.species == null, 
						userSuppliedOrganism.translation_table == null,
						userSuppliedOrganism.taxonID == null));
				
				throw new RuntimeException("The supplied organism must have all fields declared as it's not present in the repository.");
			}
			
		} else {
			
			if (userSuppliedOrganism.common_name != null) {
				organism.common_name = userSuppliedOrganism.common_name;
			}
			if (userSuppliedOrganism.ID != null) {
				organism.ID = userSuppliedOrganism.ID;
			}
			if (userSuppliedOrganism.genus != null) {
				organism.genus = userSuppliedOrganism.genus;
			}
			if (userSuppliedOrganism.species != null) {
				organism.species = userSuppliedOrganism.species;
			}
			if (userSuppliedOrganism.taxonID != null) {
				organism.taxonID = userSuppliedOrganism.taxonID;
			}
			if (userSuppliedOrganism.translation_table != null) {
				organism.translation_table = userSuppliedOrganism.translation_table;
			}
			
		}
		
		logger.info(String.format("Organism to be stored as : %s (%s %s) %s %s %s", organism.common_name, organism.genus, organism.species, organism.ID, organism.translation_table, organism.taxonID));
		
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
		new GFFAnnotatationAndFastaExtractor(reader, organism, featureMapper, regionsMapper);
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
