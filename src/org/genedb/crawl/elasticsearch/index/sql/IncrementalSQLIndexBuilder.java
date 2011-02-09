package org.genedb.crawl.elasticsearch.index.sql;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.genedb.crawl.CrawlException;
import org.genedb.crawl.elasticsearch.index.IndexBuilder;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.Organism;
import org.gmod.cat.FeatureMapper;
import org.gmod.cat.FeaturesMapper;
import org.gmod.cat.OrganismsMapper;
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
	
	void run() throws CrawlException, ParseException, IOException {
		
		setupIndex();
		setupSession();
		
		
		List<Feature> features = null;
		
		if (organismCommonName != null) {
			Organism o = organismMapper.getByCommonName(organismCommonName);
			features = featuresMapper.timelastmodified(getDate(since), o.ID);
		} else {
			features = featuresMapper.timelastmodified(getDate(since), null);
		}
		
		FeatureFiller filler = new FeatureFiller(featureMapper, features);
		filler.fill();
		
		this.sendFeaturesToIndex(features);
		
		
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


