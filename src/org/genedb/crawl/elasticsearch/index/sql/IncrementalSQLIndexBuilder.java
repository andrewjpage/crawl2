package org.genedb.crawl.elasticsearch.index.sql;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;
import org.genedb.crawl.CrawlException;
import org.genedb.crawl.controller.MapperUtil;
import org.genedb.crawl.elasticsearch.LocatedFeatureUtil;
import org.genedb.crawl.elasticsearch.index.IndexBuilder;
import org.genedb.crawl.elasticsearch.mappers.ElasticSearchFeatureMapper;
import org.genedb.crawl.elasticsearch.mappers.ElasticSearchOrganismsMapper;
import org.genedb.crawl.elasticsearch.mappers.ElasticSearchRegionsMapper;
import org.genedb.crawl.model.Coordinates;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.HierarchyGeneFetchResult;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.OrganismProp;
import org.genedb.crawl.model.Sequence;
import org.gmod.cat.FeatureMapper;
import org.gmod.cat.FeaturesMapper;
import org.gmod.cat.OrganismsMapper;
import org.gmod.cat.RegionsMapper;
import org.gmod.cat.TermsMapper;
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
	
	@Option(name = "-s", aliases = {"--since"}, usage = "The date formatted as yyyy-MM-dd", required = false)
	public String since;
	
	@Option(name = "-o", aliases = {"--organism"}, usage = "The organism common name", required = false)
	public String organismCommonName;
	
	@Option(name = "-r", aliases = {"--region"}, usage = "The region name", required = false)
	public String region;
	
	@Option(name = "-pc", aliases = {"--properties_chado"}, usage = "A properties file specifying SQL connection details", required=true)
	public File chadoPropertiesFile;
	
	private Properties chadoProperties;
	
	private static final String resource = "sql/test.xml";
	private SqlSessionFactory sqlMapper = null;
	private SqlSession session ;
	
	private ElasticSearchOrganismsMapper esOrganismMapper;
	private ElasticSearchFeatureMapper esFeatureMapper;
	private ElasticSearchRegionsMapper esRegionsMapper;
	
	void run() throws CrawlException, ParseException, IOException {
		
		setupIndex();
		setupSession();
		
		organismMapper = session.getMapper(OrganismsMapper.class);
		featuresMapper = session.getMapper(FeaturesMapper.class);
		featureMapper = session.getMapper(FeatureMapper.class);
		regionsMapper = session.getMapper(RegionsMapper.class);
		termsMapper = session.getMapper(TermsMapper.class);
		
		
		esOrganismMapper = new ElasticSearchOrganismsMapper();
		esOrganismMapper.setConnection(connection);
		
		esFeatureMapper = new ElasticSearchFeatureMapper();
		esFeatureMapper.setConnection(connection);
		
		esRegionsMapper = new ElasticSearchRegionsMapper();
		esRegionsMapper.setConnection(connection);
		
		List<LocatedFeature> features = null;
		
		if (region != null) {
			
			makeRegion(region);
			
			int start = 0;
			int end = regionsMapper.sequence(region).length;
			features = regionsMapper.locations(region, start, end, null);
			
			for (LocatedFeature f : features) {
				f.region = region;
			}
			
			
		} else if (since != null) {
			
			List<Feature> modifiedFeatures = null;
			
			if (organismCommonName != null) {
				Organism o = organismMapper.getByCommonName(organismCommonName);
				modifiedFeatures = featuresMapper.timelastmodified(getDate(since), o.ID);
			} else {
				modifiedFeatures = featuresMapper.timelastmodified(getDate(since), null);
			}
			
			List<String> modifiedFeatureNames = new ArrayList<String>(); 
			for (Feature f : modifiedFeatures) {
				modifiedFeatureNames.add(f.uniqueName);
			}
			
			int chunk = 20;
			features = new ArrayList<LocatedFeature>();
			
			for (int i = 0; i <= modifiedFeatureNames.size(); i += chunk) {
				
				int ii = i + chunk;
				if (ii > modifiedFeatureNames.size()) {
					ii = modifiedFeatureNames.size();
				}
				
				List<String> sublist = modifiedFeatureNames.subList(i, ii);
				
				List<Feature> featuresWithCoordinates = featuresMapper.coordinates(sublist, null);
				
				for (Feature f : featuresWithCoordinates) {
					LocatedFeature lf = LocatedFeatureUtil.fromFeature(f);
					features.add(lf);
				}
				
			}
			
			
		} 
		
		
		
		
		FeatureFiller filler = new FeatureFiller(featureMapper, featuresMapper, termsMapper, features);
		filler.fill();
		
		
		//List<LocatedFeature> locatedFeatures = filler.getLocatedFeatures();
		
		generateAllOrganisms(features);
		generateAllSequences(features);
		
		for (Feature f : features) {
			esFeatureMapper.createOrUpdate(f);
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
	
	void generateAllSequences(List<LocatedFeature> features) {
		Set<String> regions = new HashSet<String>(); 
		
		for (Feature f : features) {
			if (f.coordinates != null) {
				if (f.coordinates.size() > 0) {
					
					logger.info(f.uniqueName + " has coordinates on " + f.coordinates.get(0).region + ", top level:: " + f.coordinates.get(0).toplevel );
					
					if (f.coordinates.get(0).toplevel == null) {
						continue;
					}
					
					Coordinates c = f.coordinates.get(0);
					regions.add(c.region);
				}
			}
		}
		for (String region : regions) {
			makeRegion(region);
		}
	}
	
	void makeRegion(String region) {
		Feature f = featureMapper.get(region, null, null);
		
		if (f != null) {
			
			logger.info("Generating region : " + f.uniqueName);

			Sequence s = regionsMapper.sequence(region);
			
			f.residues = s.dna;

			esRegionsMapper.createOrUpdate(
					connection.getIndex(), 
					connection.getRegionType(), 
					f.uniqueName, 
					f);
		}
	}
	
	void generateAllOrganisms(List<LocatedFeature> features) throws CrawlException {
		Set<Integer> ids = new HashSet<Integer>(); 
		for (Feature f : features) {
			ids.add(f.organism_id);
		}
		for (int id : ids) {
			
			
			
			Organism o = organismMapper.getByID(id);
			
			OrganismProp taxon = organismMapper.getOrganismProp(id, "genedb_misc", "taxonId");
			OrganismProp translation_table = organismMapper.getOrganismProp(id, "genedb_misc", "translationTable");
			
			logger.debug("Setting organism " + o.common_name);
			
			if (taxon != null) {
				o.taxonID = Integer.parseInt(taxon.value);
			}
			
			if (translation_table != null) {
				logger.debug("Setting translation table " + translation_table.value);
				o.translation_table = Integer.parseInt(translation_table.value);
			} 
			
			esOrganismMapper.createOrUpdate(o);
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


