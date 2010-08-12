package org.genedb.crawl;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.genedb.crawl.business.AnnotationRepository;
import org.genedb.crawl.business.GetLocations;
import org.genedb.crawl.model.Locations;

import com.google.gson.Gson;
//import com.thoughtworks.xstream.XStream;
//import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
//import com.thoughtworks.xstream.io.xml.DomDriver;
//import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;

import junit.framework.TestCase;

public class GetLocationsTest extends TestCase {
	
	private static Logger logger = Logger.getLogger(GetLocationsTest.class);
	
	//private int start = 419720;
	//private int end = 419840;
	
	private static int start = 419720;
	private static int end = 419840;
	private static String region = "Pf3D7_01";
	
	static AnnotationRepository repo;
	
	static {
		logger.info(String.format("Region: \t %s \t Start: \t %s \t End: \t %s", region, start, end));
		repo = new AnnotationRepository ();
		try {
			repo.setLuceneDirectory("/Users/gv1/Documents/Data/lucene");
			repo.setTabixDirectory("/Users/gv1/Documents/Data/out");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public GetLocationsTest(String name) {
		super(name);
	}
	
	public void test1() throws IOException {
		logger.info("TABIX");
		
		final long t1 = System.currentTimeMillis();
		
		GetLocations getLocations = new GetLocations();
		getLocations.setRepo(repo);
		
		Locations locations = getLocations.query(region, start, end);
		
		//displayResults(locations);
		
		final long t2 = System.currentTimeMillis();
		
		
		logger.info(t2 - t1 + " ms");
		displayResults(locations);
	}
	
	public void test2() throws IOException {
		logger.info("LUCENE");
		
		final long t1 = System.currentTimeMillis();
		
		GetLocations getLocations = new GetLocations();
		getLocations.setRepo(repo);
		
		Locations locations = getLocations.queryWithLucene(region, start, end);
		
		final long t2 = System.currentTimeMillis();
		
		
		logger.info(t2 - t1 + " ms");
		
		//logger.info("LUCENE");
		displayResults(locations);
		
		
	}
	
	private void displayResults (Locations locations) {
//		final long t0_1 = System.currentTimeMillis();
//		
//		XmlFriendlyReplacer replacer = new XmlFriendlyReplacer("_", "_");
//		XStream xstream = new XStream(new DomDriver("UTF-8", replacer));
//		
//		xstream.autodetectAnnotations(true);
//		
		//System.out.println(xstream.toXML(locations));
		
//		final long t0_2 = System.currentTimeMillis();
//		
//		
//		final long t1_1 = System.currentTimeMillis();
//		XStream xstream2 = new XStream(new JsonHierarchicalStreamDriver());
//		
//		xstream2.autodetectAnnotations(true);
//		
//		//System.out.println(xstream2.toXML(locations));
//		
//		final long t1_2 = System.currentTimeMillis();
//		
//		//System.out.println(xstream.toXML(locations));
//		
		//final long t2_1 = System.currentTimeMillis();
//		
		Gson gson = new Gson();
		String json = gson.toJson(locations);
		
		logger.info(json);
		
		//final long t2_2 = System.currentTimeMillis();
		
//		logger.info(t0_2 - t0_1);
//		logger.info(t1_2 - t1_1);
		//logger.info(t2_2 - t2_1);
	}
	
}
