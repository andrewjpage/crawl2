package org.genedb.crawl.elasticsearch.index.das;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.genedb.crawl.elasticsearch.index.NonDatabaseDataSourceIndexBuilder;
import org.genedb.crawl.model.Coordinates;
import org.genedb.crawl.model.Cvterm;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.Organism;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import uk.ac.ebi.das.jdas.adapters.features.FeatureAdapter;
import uk.ac.ebi.das.jdas.exceptions.ValidationException;
import uk.ac.ebi.das.jdas.schema.entryPoints.SEGMENT;

import org.genedb.crawl.model.Cv;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.FeatureProperty;
import org.genedb.crawl.modelling.RegionFeatureBuilder;


public class DASIndexBuilder extends NonDatabaseDataSourceIndexBuilder {
	
	private static Logger logger = Logger.getLogger(DASIndexBuilder.class);
	
	@Option(name = "-u", aliases = {"--url"}, usage = "A url to a DAS registry.", required = true)
	public URL url;
	
	@Option(name = "-s", aliases = {"--source"}, usage = "The name of the DAS source.", required = true)
	public String source;
	
	@Option(name = "-r", aliases = {"--region"}, usage = "The name of the DAS source.", required = false)
	public String region;
	
	@Option(name = "-c", aliases = {"--create_regions"}, usage = "If true, an attempt will be made to create/update regions from the DAS source.", required = false)
	public boolean createRegions = false;
	
	@Option(name = "-i", aliases = {"--interbase"}, usage = "If true, assumes the DAS source coordinates are interbase (default is true). If false, it will subtract 1 from the start position.", required = false)
	public boolean interbase = true;
	
	
	void run () throws IOException, JAXBException, SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException, ValidationException {
		
		init();
		
		Organism o = getAndPossiblyStoreOrganism();
		
		DasFetcher fetcher = new DasFetcher(url, source);
		
		for (SEGMENT segment : fetcher.getEntryPoints()) {
			logger.info(segment);
			
			if (region != null) {
				if (! region.equals(segment.getId())) {
					continue;
				}
			}
			
			if (createRegions) {
				String sequence = fetcher.getSequence(segment, segment.getStart(), segment.getStop());
				logger.debug(sequence);
				
				RegionFeatureBuilder rfb = new RegionFeatureBuilder(segment.getId(), o.ID);
				rfb.addSequence(sequence);
				Feature region = rfb.getRegion();
				regionsMapper.createOrUpdate( region);
				
				logger.debug(String.format("Indexing region : %s (%d)", region.uniqueName, region.residues.length()));
				
			}
			
			List<FeatureAdapter> features = fetcher.getFeatures(segment, segment.getStart(), segment.getStop());
			
			for (FeatureAdapter featureAdapter : features) { 
				
				LocatedFeature feature = new LocatedFeature();
				
				int fmin = interbase ? featureAdapter.getStart() : featureAdapter.getStart() -1;
				int fmax = featureAdapter.getEnd();
				
				feature.uniqueName = featureAdapter.getId();
				feature.fmin = fmin;
				feature.fmax = fmax;
				feature.organism_id = o.ID;
				
				feature.region = segment.getId();
				feature.type = new Cvterm();
				feature.type.name = featureAdapter.getType().getId();
				
				Coordinates coordinates = new Coordinates();
				feature.coordinates = new ArrayList<Coordinates>();
				feature.coordinates.add(coordinates);
				coordinates.region =feature.region;
				coordinates.fmin = fmin;
				coordinates.fmax = fmax;
				
				
				
				feature.properties =new ArrayList<FeatureProperty>();
				
				FeatureProperty prop = new FeatureProperty();
				prop.name = "comment";
				prop.value = String.format("Pulled in from %s/%s/%s", url, source, segment.getId());
				
//				prop.type = new Cvterm();
//				prop.type.name = "comment";
//				prop.type.cv = new Cv();
//				prop.type.cv.name = "cvterm_property_type";
				
				feature.properties.add(prop);
				
				featureMapper.createOrUpdate(feature);
				
				logger.debug(String.format("Indexing feature: %s %s %s %s", featureAdapter.getId(), featureAdapter.getType().getId(), featureAdapter.getStart(), featureAdapter.getEnd()));
			}
		}
		
	}
	
	
	
	public static void main(String[] args) throws SecurityException, IllegalArgumentException, IOException, JAXBException, NoSuchFieldException, IllegalAccessException, ValidationException {
		DASIndexBuilder builder = new DASIndexBuilder();
		CmdLineParser parser = new CmdLineParser(builder);
		
		try {
			
			parser.parseArgument(args);
			builder.run();
			
		} catch (CmdLineException e) {
			logger.error(e.getMessage());
            parser.setUsageWidth(80);
            parser.printUsage(System.out);
            System.exit(1);
		} finally {
			
			builder.closeIndex();
		}
		
	}
	
}
