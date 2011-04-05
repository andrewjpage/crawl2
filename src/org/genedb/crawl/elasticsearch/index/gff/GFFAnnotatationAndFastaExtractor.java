package org.genedb.crawl.elasticsearch.index.gff;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.genedb.crawl.elasticsearch.mappers.ElasticSearchFeatureMapper;
import org.genedb.crawl.elasticsearch.mappers.ElasticSearchRegionsMapper;
import org.genedb.crawl.model.Cvterm;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.Organism;

public class GFFAnnotatationAndFastaExtractor {
	
	private static Logger logger = Logger.getLogger(GFFAnnotatationAndFastaExtractor.class);
	
	class RegionFeatureBuilder {
		private StringBuilder buffer = new StringBuilder();
		private Feature region = new Feature();
		
		public RegionFeatureBuilder(String uniquename, int organism_id) {
			region.uniqueName = uniquename;
			region.organism_id = organism_id;
			region.type = new Cvterm();
			region.type.name = "region";
			//region.topLevel = true;
		}
		
		public void addSequence(String line) {
			buffer.append(line);
		}
		
		public Feature getRegion() {
			region.residues = buffer.toString();
			return region;
		}
	}
	
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
					featureMapper.createOrUpdate(feature);
					
					
				} else {
					
					if (line.startsWith(">")) {
						String sequenceName = line.substring(1);
						
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
