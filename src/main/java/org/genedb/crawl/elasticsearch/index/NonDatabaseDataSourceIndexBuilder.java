package org.genedb.crawl.elasticsearch.index;

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
import org.genedb.crawl.elasticsearch.index.gff.GFFAnnotatationAndFastaExtractor;
import org.genedb.crawl.elasticsearch.index.gff.GFFFileFilter;
import org.genedb.crawl.elasticsearch.mappers.ElasticSearchFeatureMapper;
import org.genedb.crawl.elasticsearch.mappers.ElasticSearchOrganismsMapper;
import org.genedb.crawl.elasticsearch.mappers.ElasticSearchRegionsMapper;
import org.genedb.crawl.model.Organism;

public abstract class NonDatabaseDataSourceIndexBuilder extends IndexBuilder {
	
	private static Logger logger = Logger.getLogger(NonDatabaseDataSourceIndexBuilder.class);
	
	protected ElasticSearchFeatureMapper featureMapper;
	protected ElasticSearchOrganismsMapper organismsMapper;
	protected ElasticSearchRegionsMapper regionsMapper;

	public NonDatabaseDataSourceIndexBuilder() {
		super();
	}
	
	public void init() throws IOException {
		setupIndex();
		
		featureMapper = new ElasticSearchFeatureMapper();
		featureMapper.setConnection(connection);
		
		organismsMapper = new ElasticSearchOrganismsMapper();
		organismsMapper.setConnection(connection);
		
		regionsMapper = new ElasticSearchRegionsMapper();
		regionsMapper.setConnection(connection);
	}
	
	protected void convertPath(String path, Organism organism) throws ParseException, IOException {
		
		File gffFile = new File(path);
		
		GFFFileFilter filter = new GFFFileFilter();
		filter.filter_set = GFFFileFilter.GFFFileExtensionSet.ALL;
		
		if (gffFile.isDirectory()) {
			for (File f : gffFile.listFiles(filter)) {
				convertFile(f, organism);
				f = null;
			}
		} else {
			if (! gffFile.isFile() ) {
				throw new IOException("File " + path + " does not exist");
			}
			convertFile(gffFile, organism);
			gffFile = null;
		}
		
	}
	
	protected void convertFile(File gffFile, Organism organism) throws ParseException, IOException {
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
	
	protected Organism getAndPossiblyStoreOrganism(String organismString) throws JsonParseException,
			JsonMappingException, IOException, SecurityException,
			NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {
				
				Organism userSuppliedOrganism = (Organism) jsonIzer.fromStringOrFile(organismString, Organism.class);
				
				Organism organism = null;
				
				if (userSuppliedOrganism.ID != null) {
					logger.info("Getting by ID " + userSuppliedOrganism.ID );
					try {
						organism = organismsMapper.getByID(userSuppliedOrganism.ID);
						logger.info("found!");
					} catch (Exception e) {
						logger.warn(e.getMessage());
						//e.printStackTrace();
						logger.warn("Could not find an organism with this ID");
					}
				} 
				
				if (userSuppliedOrganism.common_name != null) {
					logger.info("Getting by common_name: " + userSuppliedOrganism.common_name);
					try {
						organism = organismsMapper.getByCommonName(userSuppliedOrganism.common_name);
						logger.info("found!");
					} catch (Exception e) {
						logger.warn(e.getMessage());
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

}