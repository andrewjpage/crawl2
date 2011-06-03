package org.genedb.crawl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.EnumSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.client.Client;
import org.genedb.crawl.bam.BioDataFileStore;
import org.genedb.crawl.bam.BioDataFileStoreInitializer;
import org.genedb.crawl.controller.VariantController;
import org.genedb.crawl.elasticsearch.Connection;
import org.genedb.crawl.elasticsearch.index.gff.GFFIndexBuilder;
import org.genedb.crawl.elasticsearch.mappers.ElasticSearchRegionsMapper;
import org.genedb.crawl.json.JsonIzer;
import org.genedb.crawl.model.MappedVCFRecord;
import org.genedb.crawl.model.Sequence;
import org.genedb.crawl.model.Variant;

import uk.ac.sanger.artemis.components.variant.GeneFeature;
import uk.ac.sanger.artemis.components.variant.VariantFilterOption;
import uk.ac.sanger.artemis.components.variant.VariantFilterOptions;
import uk.ac.sanger.artemis.components.variant.VariantReaderAdapter;
import junit.framework.TestCase;

public class VariantTest extends TestCase {
	
	private Logger logger = Logger.getLogger(VariantTest.class);
	
	JsonIzer jsonIzer = new JsonIzer();
	MyGFFIndexBuilder builder;
	
	class MyGFFIndexBuilder extends GFFIndexBuilder {
		ElasticSearchRegionsMapper getRegionsMapper() {
			return regionsMapper;
		}
		public Client getClient() {
			return client;
		}
	}
	
	
	
	public void testVCF() throws IOException, SecurityException, IllegalArgumentException, ParseException, NoSuchFieldException, IllegalAccessException {
		String gffFile = "./src/test/resources/data/Pf3D7_01.gff.gz";
		String variantFile = "./src/test/resources/data/Pf3D7_01.vcf.gz";
		String region = "Pf3D7_01";
		String sequenceNameInVCF = "MAL1";
		String organism = "{    \"ID\":27,    \"common_name\":\"Pfalciparum\",    \"genus\":\"Plasmodium\",    \"species\":\"falciparum\",    \"translation_table\":11,    \"taxonID\":5833}";
		int start = 1;
		int end = 100000;
		
		logger.info(String.format("Running %s %s %s %s:%d-%d", gffFile, variantFile, organism, region, start, end));
		
		runQueryDirectly(variantFile,sequenceNameInVCF,start,end);
		run(gffFile, variantFile, organism, region, start, end);
		
	}
	
	public void testBCF() throws IOException, SecurityException, IllegalArgumentException, ParseException, NoSuchFieldException, IllegalAccessException {
		String gffFile = "./src/test/resources/data/Spn23f.gff";
		String variantFile= "http://www.genedb.org/artemis/NAR/Spneumoniae/4882_6_10_variant.bcf";
		String region = "Spn23F";
		String sequenceNameInVCF = "S_pneumoniae_Spanish_23F.dna";
		String organism = "{\"ID\":999,\"common_name\":\"Spneumoniae\",\"genus\":\"Streptococcus\",\"species\":\"pneumoniae\",\"translation_table\":1,\"taxonID\":1313}";
		int start = 1;
		int end = 10000000;
		
		logger.info(String.format("Running %s %s %s %s:%d-%d", gffFile, variantFile, organism, region, start, end));
		
		runQueryDirectly(variantFile,sequenceNameInVCF,start,end);
		run(gffFile, variantFile, organism, region, start, end);
		
	}
	
	
	private void runQueryDirectly(String variantFile, String region, int start, int end) throws IOException {
		
		VariantReaderAdapter reader = VariantReaderAdapter.getReader(variantFile);
		logger.info(reader.getSeqNames());
		List<?> records = reader.unFilteredQuery(region, start, end);
		for (Object record : records) {
			logger.info(record);
		}
	}
	
	
	
	private void run (String gffFile, String variantFile, String organism, String region, int start, int end) throws SecurityException, IllegalArgumentException, IOException, ParseException, NoSuchFieldException, IllegalAccessException {
		
		builder = new MyGFFIndexBuilder();
		builder.elasticSearchPropertiesFile = new File("resource-elasticsearch-local.properties");
		
		VariantReaderAdapter reader = VariantReaderAdapter.getReader(variantFile);
		logger.info(reader.getSeqNames());
		
		
		builder.gffs = gffFile;
		builder.organism = organism;
		
		builder.run();
		
		ElasticSearchRegionsMapper regionsMapper = builder.getRegionsMapper();
		
		Connection.waitForStatus(builder.getClient(), EnumSet.of(ClusterHealthStatus.YELLOW, ClusterHealthStatus.GREEN));;
		
		String sequence = region;
		
		Sequence regionSequence = regionsMapper.sequence(sequence);
		List<GeneFeature> geneFeatures = VariantController.getGenesAt(sequence, start, end, regionsMapper);
		
		for (GeneFeature f : geneFeatures) {
			logger.debug("gene : " + f.uniqueName + f.region+ ":" + f.fmin + "-" + f.fmax);
		}
		
		VariantFilterOptions opts = new VariantFilterOptions(EnumSet.allOf(VariantFilterOption.class));
		logger.info(opts);
		
		BioDataFileStoreInitializer initializer = new BioDataFileStoreInitializer();
		initializer.setAlignmentFiles(new File("./etc/alignments.json"));
		
		BioDataFileStore<Variant> variantStore = initializer.getVariants();
		
		String alignmentName = variantStore.getAlignmentFromName(sequence);
		
		List<MappedVCFRecord> records = reader.query(alignmentName, start, end, geneFeatures, opts, regionSequence);
		
		
		String result = jsonIzer.toJson(records);
		
		assertTrue(records.size() > 0);
		assertNotNull(result != null);
		
		logger.info(result);
		
		builder.closeIndex();
	}
	
}
