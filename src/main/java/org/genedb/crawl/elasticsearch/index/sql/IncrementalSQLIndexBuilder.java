package org.genedb.crawl.elasticsearch.index.sql;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;
import org.codehaus.jackson.type.TypeReference;
import org.genedb.crawl.CrawlException;
import org.genedb.crawl.elasticsearch.index.IndexBuilder;
import org.genedb.crawl.elasticsearch.mappers.ElasticSearchFeatureMapper;
import org.genedb.crawl.elasticsearch.mappers.ElasticSearchOrganismsMapper;
import org.genedb.crawl.elasticsearch.mappers.ElasticSearchRegionsMapper;
import org.genedb.crawl.json.JsonIzer;
import org.genedb.crawl.mappers.AuditMapper;
import org.genedb.crawl.mappers.FeatureMapper;
import org.genedb.crawl.mappers.FeaturesMapper;
import org.genedb.crawl.mappers.OrganismsMapper;
import org.genedb.crawl.mappers.RegionsMapper;
import org.genedb.crawl.mappers.TermsMapper;
import org.genedb.crawl.model.Cvterm;
import org.genedb.crawl.model.Organism;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.hazelcast.core.Hazelcast;



public class IncrementalSQLIndexBuilder extends IndexBuilder {
	
	private static Logger logger = Logger.getLogger(IncrementalSQLIndexBuilder.class);
	
	protected OrganismsMapper organismMapper;
	protected FeaturesMapper featuresMapper;
	protected FeatureMapper featureMapper;
	protected RegionsMapper regionsMapper;
	protected TermsMapper termsMapper;
	protected AuditMapper auditMapper;
	
	@Option(name = "-s", aliases = {"--since"}, usage = "The date formatted as yyyy-MM-dd", required = false)
	public String since;
	
	@Option(name = "-o", aliases = {"--organism"}, usage = "The organism common name", required = false)
	public String organismCommonName;
	
	@Option(name = "-r", aliases = {"--region"}, usage = "The region name", required = false)
	public String region;
	
	@Option(name = "-f", aliases = {"--features"}, usage = "Index the i.e. features of the supplied organism or region", required = false)
	public boolean features = false;
	
	@Option(name = "-pc", aliases = {"--properties_chado"}, usage = "A properties file specifying SQL connection details", required=true)
	public File chadoPropertiesFile;
	
	@Option(name = "-e", aliases = {"--exclude"}, usage = "Whether to exclude or include the supplied types", required=false)
	public boolean exclude = false;
	
	@Option(name = "-t", aliases = {"--types"}, usage = "The types to include or exclude, supplied as a JSON ['array', 'of', 'strings'].", required=false)
	public String types = defaultTypes;
		
	private static final String defaultTypes = "[\"gene\", \"pseudogene\", \"match_part\", \"repeat_region\", \"repeat_unit\", \"direct_repeat\", \"EST_match\", \"region\", \"polypeptide\", \"mRNA\", \"pseudogenic_transcript\", \"nucleotide_match\", \"exon\", \"pseudogenic_exon\", \"gap\", \"contig\", \"ncRNA\", \"tRNA\", \"five_prime_UTR\", \"three_prime_UTR\", \"polypeptide_motif\"]";
	
	private Properties chadoProperties;
	
	private static final String resource = "ibatis-datasourced.xml";
	private SqlSessionFactory sqlMapper = null;
	private SqlSession session ;
	
	private ElasticSearchOrganismsMapper esOrganismMapper;
	private ElasticSearchFeatureMapper esFeatureMapper;
	private ElasticSearchRegionsMapper esRegionsMapper;
	
	private List<Cvterm> relationships = new ArrayList<Cvterm>(); 
	
	private JsonIzer jsonIzer = new JsonIzer();
	
	void run() throws CrawlException, ParseException, IOException {
		
		setupIndex();
		setupSession();
		
		// SQL mappers
		organismMapper = session.getMapper(OrganismsMapper.class);
		featuresMapper = session.getMapper(FeaturesMapper.class);
		featureMapper = session.getMapper(FeatureMapper.class);
		regionsMapper = session.getMapper(RegionsMapper.class);
		termsMapper = session.getMapper(TermsMapper.class);
		auditMapper= session.getMapper(AuditMapper.class);
		
		relationships.add(CvtermUtil.makeTerm(termsMapper, "derives_from", "sequence"));
		relationships.add(CvtermUtil.makeTerm(termsMapper, "part_of", "relationship"));
		
		// ES mappers
		
		esOrganismMapper = new ElasticSearchOrganismsMapper();
		esOrganismMapper.setConnection(connection);
		
		esFeatureMapper = new ElasticSearchFeatureMapper();
		esFeatureMapper.setConnection(connection);
		
		esRegionsMapper = new ElasticSearchRegionsMapper();
		esRegionsMapper.setConnection(connection);
		
		SQLIndexer indexer = new SQLIndexer();
		indexer.featureMapper = featureMapper;
		indexer.featuresMapper = featuresMapper;
		indexer.regionsMapper = regionsMapper;
		indexer.organismMapper = organismMapper;
		indexer.termsMapper = termsMapper;
		indexer.esFeatureMapper = esFeatureMapper;
		indexer.esOrganismMapper = esOrganismMapper;
		indexer.esRegionsMapper = esRegionsMapper;
		indexer.relationships = relationships;
		indexer.auditMapper = auditMapper;
		 
		indexer.exclude = exclude;
		
		logger.debug("Setting types : " + types);
		indexer.types = (List<String>) jsonIzer.fromJson(types,  new TypeReference<List<String>>() {} );
		
		logger.info(String.format("Exclude? %s, Types: %s", indexer.exclude, indexer.types));
		
		Organism o = null;
		if (organismCommonName != null) {
			o = organismMapper.getByCommonName(organismCommonName);
		}
		
		if (region != null) {
			if (features) {
				indexer.indexRegionContents(region);
			} else {
				indexer.indexRegion(region);
			}
			
		} else if (since != null) {
			indexer.indexFeaturesSince(getDate(since), o);
		} else {
			// only generate the organisms...
			if (o == null) {
				if (features) {
					throw new RuntimeException("Will not index the contents of all the organisms at once.");
				} else {
					indexer.indexOrganisms();
				}
			} else {
				if (features) {
					indexer.indexOrganismContents(o);
				} else {
					indexer.indexOrganism(o);
				}
			}
		}
		
	}
	
	protected void setupSession() throws IOException {
		
		chadoProperties = new Properties();
		chadoProperties.load(new FileInputStream(chadoPropertiesFile));
		
		Reader reader = null;
		reader = Resources.getResourceAsReader(resource);
		sqlMapper = new SqlSessionFactoryBuilder().build(reader, chadoProperties);
		session = sqlMapper.openSession();
		session.clearCache();
	}
	
	protected void closeSession() {
		if (session != null) {
			session.close();
			Hazelcast.shutdownAll();
		}
	}
	
	
	Date getDate(String since) throws ParseException {
		
		Date sinceDate = Calendar.getInstance().getTime();
		if (since != null)
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
            sinceDate = dateFormat.parse(since);
        }
		return sinceDate;
		
	}
	
	
	
	/**
	 * @param args
	 * @throws ParseException 
	 * @throws CrawlException 
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 */
	public static void main(String[] args) throws CrawlException, ParseException, IOException {
		
		IncrementalSQLIndexBuilder incrementalBuilder = new IncrementalSQLIndexBuilder();
		CmdLineParser parser = new CmdLineParser(incrementalBuilder);
		
		try {
			
			parser.parseArgument(args);
		
			if (incrementalBuilder.help) {
				parser.setUsageWidth(80);
	            parser.printUsage(System.out);
	            System.exit(1);
			}
			
			incrementalBuilder.run();
		
		} catch (CmdLineException e) {
			System.out.println(e.getMessage());
            parser.setUsageWidth(80);
            parser.printUsage(System.out);
            System.exit(1);
		} finally {
			
			incrementalBuilder.closeIndex();
			incrementalBuilder.closeSession();
			
		}
		
	}
	
	
	
	
	
}


