package uk.ac.sanger.artemis.components.variant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
import org.genedb.crawl.elasticsearch.mappers.ElasticSearchFeatureMapper;
import org.genedb.crawl.elasticsearch.mappers.ElasticSearchRegionsMapper;
import org.genedb.crawl.json.JsonIzer;
import org.genedb.crawl.model.Gene;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.MappedVCFRecord;
import org.genedb.crawl.model.Sequence;
import org.genedb.crawl.model.Variant;

import uk.ac.sanger.artemis.components.variant.VariantFilterOption;
import uk.ac.sanger.artemis.components.variant.VariantFilterOptions;
import uk.ac.sanger.artemis.components.variant.VariantReaderAdapter;
import uk.ac.sanger.artemis.util.OutOfRangeException;
import junit.framework.TestCase;

public class VariantTest extends TestCase {
	
	private static Logger logger = Logger.getLogger(VariantTest.class);
	
	JsonIzer jsonIzer = new JsonIzer();
	MyGFFIndexBuilder builder;
	
	class MyGFFIndexBuilder extends GFFIndexBuilder {
		ElasticSearchRegionsMapper getRegionsMapper() {
			return regionsMapper;
		}
		ElasticSearchFeatureMapper getFeatureMapper() {
			return featureMapper;
		}
		public Client getClient() {
			return client;
		}
	}
	
	
	/*
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
		int end =   100000;
		
		logger.info(String.format("Running %s %s %s %s:%d-%d", gffFile, variantFile, organism, region, start, end));
		
		runQueryDirectly(variantFile,sequenceNameInVCF,start,end);
		run(gffFile, variantFile, organism, region, start, end);
		
	}
	*/
	
	public void testBCFLocal() throws IOException, SecurityException, IllegalArgumentException, ParseException, NoSuchFieldException, IllegalAccessException, OutOfRangeException {
        String gffFile = "./src/test/resources/data/Streptococcus_pneumoniae_ATCC_700669_v1.gff";
        String variantFile= "./src/test/resources/data/4882_6_10_variant.bcf";
        
        String region = "FM211187";
        String sequenceNameInVCF = "S_pneumoniae_Spanish_23F.dna";
        String organism = "{\"ID\":999,\"common_name\":\"Spneumoniae\",\"genus\":\"Streptococcus\",\"species\":\"pneumoniae\",\"translation_table\":1,\"taxonID\":1313}";
        int start = 1;
        int end =   278965;
        
        logger.info(String.format("Running %s %s %s %s:%d-%d", gffFile, variantFile, organism, region, start, end));
        
        runQueryDirectly(variantFile,sequenceNameInVCF,start,end);
        
        startIndex();
        List<LocatedFeature> exonFeatures = buildIndex(gffFile, organism, region, start, end);
        logger.info("how many gene features? " + exonFeatures.size());
        
        ElasticSearchRegionsMapper regionsMapper = builder.getRegionsMapper();
        Sequence regionSequence = regionsMapper.sequence(region);
        
        logger.info(regionSequence.dna);
        
        VariantReaderAdapter vreader = VariantReaderAdapter.getReader(variantFile);
        
        logger.info("generating cds features");
        List<CDSFeature> cdsFeatures = vreader.makeCDSFeatures(exonFeatures, regionSequence);
        logger.info("how many cds features? " + cdsFeatures.size());
        
        
        File artemisResults = new File("src/test/resources/data/artemis_vcf_filter_results.txt");
        
        FileInputStream fileStream = new FileInputStream(artemisResults);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream));
        
        //Map<Integer,VCFRecord> artemisResultRecords = new HashMap<>
        
        List<MappedVCFRecord> records = null;
        String line;
        int filter = 0;
        logger.info("parsing...");
        while ((line = reader.readLine()) != null) {
            
            logger.info(line);
            
            if (line.startsWith("#"))
                continue;
            
            if (line.startsWith("HIDE") || line.startsWith("SHOW")) {
                
                boolean show = (line.startsWith("HIDE")) ? false : true;
                // SHOW : S_pneumoniae_Spanish_23F.dna   11203   .   C   T   99.0    .   DP=103;AF1=1;CI95=1,1;DP4=0,0,43,60;MQ=60   PL:DP:SP:GT:GQ  255,255,0:103:0:1/1:99
                String recordString = line.substring(line.indexOf(":") + 2);
                logger.info(recordString);
                VCFRecord record = VCFRecord.parse(recordString);
                
                boolean found = false;
                for (MappedVCFRecord mappedRecord : records) {
                    logger.info(String.format("%s = %s , %s = %s", record.getPos() , mappedRecord.pos , record.getQuality(), mappedRecord.quality));
                    if (record.getPos() == mappedRecord.pos && record.getQuality() == mappedRecord.quality) {
                        logger.info("found!");
                        found = true;
                    }
                }
                
                if (show) {
                    assertTrue("line not found when should be : " + line, found);
                } else {
                    assertFalse("line found when shouldn't be : " + line, found);
                }
                
                continue;
            }
            // showSynonymous, selfshowNonSynonymous, selfshowDeletions, selfshowInsertions, selfshowMultiAlleles, selfshowNonOverlappings,selfshowNonVariantsself
            String[] columns=line.split("\t");
            String[] coords = columns[0].split("-");
            int sbeg = Integer.parseInt(coords[0]);
            int send = Integer.parseInt(coords[1]);
            
            boolean showSynonymous= Boolean.parseBoolean(columns[1]);
            boolean showNonSynonymous= Boolean.parseBoolean(columns[2]);
            boolean showDeletions= Boolean.parseBoolean(columns[3]);
            boolean showInsertions= Boolean.parseBoolean(columns[4]);
            boolean showMultiAlleles= Boolean.parseBoolean(columns[5]);
            boolean showNonOverlappings= Boolean.parseBoolean(columns[6]);
            boolean showNonVariants= Boolean.parseBoolean(columns[7]);
            
            logger.info(
                    String.format(
                    "%s-%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s",
                    sbeg, 
                    send,
                    showSynonymous, 
                    showNonSynonymous, 
                    showDeletions, 
                    showInsertions, 
                    showMultiAlleles, 
                    showNonOverlappings,
                    showNonVariants));
            
            filter = 0;
            
            if (showSynonymous)
                filter += VariantFilterOption.SHOW_SYNONYMOUS.index();
            if (showNonSynonymous)
                filter += VariantFilterOption.SHOW_NON_SYNONYMOUS.index();
            if (showDeletions)
                filter += VariantFilterOption.SHOW_DELETIONS.index();
            if (showInsertions)
                filter += VariantFilterOption.SHOW_INSERTIONS.index();
            if (showMultiAlleles)
                filter += VariantFilterOption.SHOW_MULTI_ALLELES.index();
            if (showNonOverlappings)
                filter += VariantFilterOption.SHOW_NON_OVERLAPPINGS.index();
            if (showNonVariants)
                filter += VariantFilterOption.SHOW_NON_VARIANTS.index();
            
            
            VariantFilterOptions options = new VariantFilterOptions(filter);
            records = vreader.query(sequenceNameInVCF, start, end, cdsFeatures, options);
            
            jsonIzer.setPretty(true);
            logger.info(jsonIzer.toJson(records));
            logger.info("records length : " + records.size());
            
        }
        
        
        closeIndex();
        
    }
	
	
	private void runQueryDirectly(String variantFile, String region, int start, int end) throws IOException {
		logger.info("runQueryDirectly");
		VariantReaderAdapter reader = VariantReaderAdapter.getReader(variantFile);
		logger.info(reader.getSeqNames());
		List<?> records = reader.unFilteredQuery(region, start, end);
		for (Object record : records) {
			logger.info(record);
		}
	}
	
	
	
	private List<LocatedFeature> buildIndex (
	        String gffFile, 
	        String organism, 
	        String region, 
	        int start, 
	        int end) throws SecurityException, IllegalArgumentException, IOException, ParseException, NoSuchFieldException, IllegalAccessException {
	    
	    
		//logger.info("bui");
//		builder = new MyGFFIndexBuilder();
//		builder.elasticSearchPropertiesFile = new File("resource-elasticsearch-local.properties");
//		
		
		
		
		builder.gffs = gffFile;
		builder.organism = organism;
		
		builder.run();
		
		ElasticSearchRegionsMapper regionsMapper = builder.getRegionsMapper();
		ElasticSearchFeatureMapper featureMapper = builder.getFeatureMapper();
		
		Connection.waitForStatus(builder.getClient(), EnumSet.of(ClusterHealthStatus.YELLOW, ClusterHealthStatus.GREEN));;
		
		//String sequence = region;
		
//		Sequence regionSequence = regionsMapper.sequence(sequence);
		List<LocatedFeature> features = VariantController.getExons(region, start, end, regionsMapper, featureMapper);
		
//		
		for (LocatedFeature f : features) {
			logger.info("gene : " + f.uniqueName + " / "  + f.region+ ":" + f.fmin + "-" + f.fmax);
		}
		
		return features;
//		
//		//VariantFilterOptions opts = new VariantFilterOptions(EnumSet.allOf(VariantFilterOption.class));
//		logger.info(opts);
		
		
		
//		BioDataFileStoreInitializer initializer = new BioDataFileStoreInitializer();
//		initializer.setAlignmentFiles(new File("./etc/alignments.json"));
//		
//		BioDataFileStore<Variant> variantStore = initializer.getVariants();
//		
//		String alignmentName = variantStore.getAlignmentFromName(sequence);
//		
//		VariantReaderAdapter reader = VariantReaderAdapter.getReader(variantFile);
//        logger.info(reader.getSeqNames());
//		
//		List<MappedVCFRecord> records = reader.query(sequenceNameInVCF, start, end, geneFeatures, opts, regionSequence);
////		
//		jsonIzer.setPretty(true);
//		String result = jsonIzer.toJson(records);
////		
////		assertTrue(records.size() > 0);
////		assertNotNull(result != null);
////		
//		logger.info(result);
		
		
		//List<VCFRecord> recordsUnfiltered = reader.unFilteredQuery(region, start, end);
		
	}
	
	
	
    void startIndex() {
        builder = new MyGFFIndexBuilder();
        builder.elasticSearchPropertiesFile = new File(
                "resource-elasticsearch-local.properties");
    }
	
	
	void closeIndex() {
	    builder.closeIndex();
	}
	
}
