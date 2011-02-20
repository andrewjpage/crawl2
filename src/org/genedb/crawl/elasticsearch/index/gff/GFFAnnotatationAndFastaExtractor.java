package org.genedb.crawl.elasticsearch.index.gff;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.genedb.crawl.elasticsearch.mappers.ElasticSearchFeatureMapper;
import org.genedb.crawl.model.ElasticSequence;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.SequenceType;

public class GFFAnnotatationAndFastaExtractor {
	
	private static Logger logger = Logger.getLogger(GFFAnnotatationAndFastaExtractor.class);
	
	class SequenceBuilder {
		private StringBuilder buffer = new StringBuilder();
		private ElasticSequence sequence = new ElasticSequence();
		
		public SequenceBuilder(String name, SequenceType sequenceType, int organism_id) {
			sequence = new ElasticSequence(name, sequenceType, organism_id);
		}
		
		public void addSequence(String line) {
			buffer.append(line);
		}
		
		public ElasticSequence getSequence() {
			sequence.sequence = buffer.toString();
			return sequence;
		}
	}
	
	public GFFAnnotatationAndFastaExtractor(BufferedReader buf, Organism organism, ElasticSearchFeatureMapper mapper) throws IOException {
		
		List<SequenceBuilder> sequences = new ArrayList<SequenceBuilder>();
		
		try {
			
			String line = "";
			boolean parsingAnnotations = true;
			SequenceBuilder sequence = null;
			
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
					mapper.createOrUpdate(feature);
					
					
				} else {
					
					if (line.startsWith(">")) {
						String sequenceName = line.substring(1);
						
						sequence = new SequenceBuilder(sequenceName, SequenceType.AMINO_ACID, organism.ID);
						logger.debug("Parsing sequence : " + sequenceName);
						sequences.add(sequence);
						
					} else if (sequence != null) {
						sequence.addSequence(line);
					}
					
				}
				
			}
			
			for (SequenceBuilder seq : sequences) {
				mapper.createOrUpdate(seq.getSequence());
			}
			
		} finally {
			buf.close();
		}
		
	}
	

	
	
}
