package org.genedb.crawl.elasticsearch.index.sql;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.genedb.crawl.CrawlException;
import org.genedb.crawl.elasticsearch.index.IndexBuilder;
import org.genedb.crawl.elasticsearch.mappers.ElasticSearchFeatureMapper;
import org.genedb.crawl.elasticsearch.mappers.ElasticSearchOrganismsMapper;
import org.genedb.crawl.model.Coordinates;
import org.genedb.crawl.model.ElasticSequence;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.OrganismProp;
import org.genedb.crawl.model.SequenceType;
import org.gmod.cat.FeatureMapper;
import org.gmod.cat.FeaturesMapper;
import org.gmod.cat.OrganismsMapper;
import org.gmod.cat.RegionsMapper;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;



public class IncrementalSQLIndexBuilder extends IndexBuilder {
	
	
	
	@Option(name = "-s", aliases = {"--since"}, usage = "The date formatted as yyyy-MM-dd", required = true)
	public String since;
	
	@Option(name = "-o", aliases = {"--organism"}, usage = "The organism common name", required = false)
	public String organismCommonName;
	
	
	private static final String resource = "sql/test.xml";
	
	private SqlSessionFactory sqlMapper = null;
	private SqlSession session ;
	
	private OrganismsMapper organismMapper;
	private FeaturesMapper featuresMapper;
	private FeatureMapper featureMapper;
	private RegionsMapper regionsMapper;
	
	private ElasticSearchOrganismsMapper esOrganismMapper;
	private ElasticSearchFeatureMapper esFeatureMapper;
	
	void run() throws CrawlException, ParseException, IOException {
		
		setupIndex();
		setupSession();
		
		esOrganismMapper = new ElasticSearchOrganismsMapper();
		esOrganismMapper.setConnection(connection);
		
		esFeatureMapper = new ElasticSearchFeatureMapper();
		esFeatureMapper.setConnection(connection);
		
		
		
		List<Feature> features = null;
		
		if (organismCommonName != null) {
			Organism o = organismMapper.getByCommonName(organismCommonName);
			features = featuresMapper.timelastmodified(getDate(since), o.ID);
		} else {
			features = featuresMapper.timelastmodified(getDate(since), null);
		}
		
		FeatureFiller filler = new FeatureFiller(featureMapper, features);
		filler.fill();
		
		generateAllOrganisms(features);
		generateAllSequences(features);
		
		for (Feature f : features) {
			esFeatureMapper.createOrUpdate(f);
		}
		
	}
	
	void generateAllSequences(List<Feature> features) {
		Set<String> regions = new HashSet<String>(); 
		
		List<LocatedFeature> lFeatures = new ArrayList<LocatedFeature>();
		
		for (Feature f : features) {
			if (f.coordinates != null) {
				if (f.coordinates.size() > 0) {
					Coordinates c = f.coordinates.get(0);
					regions.add(c.region);
				}
			}
		}
		for (String region : regions) {
			Feature f = featureMapper.get(region, null, null);
			if (f != null) {
				ElasticSequence sequence = new ElasticSequence();
				sequence.name = region;
				sequence.organism_id = f.organism_id;
				sequence.sequenceType = SequenceType.DNA;
				sequence.sequence = regionsMapper.sequence(region);
				esFeatureMapper.createOrUpdate(sequence);
			}
		}
	}
	
	void generateAllOrganisms(List<Feature> features) throws CrawlException {
		Set<Integer> ids = new HashSet<Integer>(); 
		for (Feature f : features) {
			ids.add(f.organism_id);
		}
		for (int id : ids) {
			Organism o = organismMapper.getByID(id);
			
			OrganismProp taxon = organismMapper.getOrganismProp(id, "genedb_misc", "taxonId");
			OrganismProp translation_table = organismMapper.getOrganismProp(id, "genedb_misc", "taxonId");
			
			if (taxon != null) {
				o.taxonID = Integer.parseInt(taxon.value);
			}
			
			if (taxon != null) {
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
	
	void setupSession() {
		Reader reader = null;

		try {
			reader = Resources.getResourceAsReader(resource);
			
			sqlMapper = new SqlSessionFactoryBuilder().build(reader, System.getProperties());
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		session = sqlMapper.openSession();
		
		organismMapper = session.getMapper(OrganismsMapper.class);
		featuresMapper = session.getMapper(FeaturesMapper.class);
		featureMapper = session.getMapper(FeatureMapper.class);
		regionsMapper = session.getMapper(RegionsMapper.class);
	}
	
	void closeSession() {
		if (session != null) {
			session.close();
		}
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


