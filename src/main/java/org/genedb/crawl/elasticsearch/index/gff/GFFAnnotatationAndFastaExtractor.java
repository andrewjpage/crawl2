package org.genedb.crawl.elasticsearch.index.gff;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.genedb.crawl.elasticsearch.mappers.ElasticSearchFeatureMapper;
import org.genedb.crawl.elasticsearch.mappers.ElasticSearchRegionsMapper;
import org.genedb.crawl.model.Coordinates;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.modelling.RegionFeatureBuilder;

public class GFFAnnotatationAndFastaExtractor {
	
	private static Logger logger = Logger.getLogger(GFFAnnotatationAndFastaExtractor.class);
	
	public GFFAnnotatationAndFastaExtractor(BufferedReader buf, Organism organism, ElasticSearchFeatureMapper featureMapper, ElasticSearchRegionsMapper regionsMapper) throws IOException {
		
		List<RegionFeatureBuilder> sequences = new ArrayList<RegionFeatureBuilder>();
		
		try {
			
			String line = "";
			boolean parsingAnnotations = true;
			RegionFeatureBuilder sequence = null;
			
			while ((line=buf.readLine())!=null) {
				// logger.debug(line);
				
				if (line.contains("##FASTA")) {
					parsingAnnotations = false;
				}
				
				if (line.startsWith("#")) {
					continue;
				}
				
				if (parsingAnnotations) {
					
					
					LocatedFeature feature = new FeatureBeanFactory(organism, line).getFeature();
					
					LocatedFeature existingFeature = featureMapper.get(feature.uniqueName);
					if (existingFeature != null) {
						
						if (feature.fmin != existingFeature.fmin 
								|| feature.fmax != existingFeature.fmax) {
							
							// we are currently assuming this is never null (because FeatureBeanFactory creates it) 
							List<Coordinates> existingCoordinates = existingFeature.coordinates;
							
							boolean coordinatesAlreadyPresent = false;
								
							for (Coordinates c : existingCoordinates) {
								if (c.fmin == feature.fmin && c.fmax == feature.fmax) {
									coordinatesAlreadyPresent = true;
								}
							}
							
							if (! coordinatesAlreadyPresent) {
								assert(feature.coordinates.get(0).fmin == feature.fmin);
								assert(feature.coordinates.get(0).fmax == feature.fmax);
								logger.warn(String.format("Adding extra %d %d coordinates for %s", feature.coordinates.get(0).fmin, feature.coordinates.get(0).fmax, feature.uniqueName));
								existingFeature.coordinates.add(feature.coordinates.get(0));
								// TODO we won't bother adding extra annotations on this for the time being
							} 
							
							feature = existingFeature;
							
						}
						
					}
					
					
					featureMapper.createOrUpdate(feature);
					
					
				} else {
					
					if (line.startsWith(">")) {
						String sequenceName = line.substring(1);
						
						/* we ignore everything after a space */
						int spacePos = sequenceName.indexOf(" ");
						if (spacePos != -1) {
							sequenceName = sequenceName.substring(0, spacePos);
						}
						
						sequence = new RegionFeatureBuilder(sequenceName, organism.ID);
						logger.debug("Parsing sequence : " + sequenceName);
						sequences.add(sequence);
						
					} else if (sequence != null) {
						sequence.addSequence(line);
					}
					
				}
				
			}
			
			for (RegionFeatureBuilder regionBuilder : sequences) {
				Feature region = regionBuilder.getRegion();
				regionsMapper.createOrUpdate(region);
			}
			
		} finally {
			buf.close();
		}
		
	}
	

	
	
}
