package org.genedb.crawl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.genedb.crawl.client.CrawlClient;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.Organism;


import junit.framework.TestCase;

/**
 * Tests a simple client. 
 * 
 * TODO We are not testing for IOExceptions here - just the ability to decode jsons into model beans. No mechanism exists (as yet) for  
 * invocation of the crawl server, so we can't guarantee that one is up. 
 */
public class ClientTest extends TestCase {
	
	private Logger logger = Logger.getLogger(ClientTest.class);
	
	String baseURL = "http://localhost:8080/services";
	
	public void testOrganisms() throws IOException  {
		
		CrawlClient client = new CrawlClient(baseURL); 
		
		try {
			
			List<Organism> organisms = client.request(Organism.class, "organisms", "list");
			
			for (Organism o : organisms) {
				logger.info(o.common_name);
			}
		} catch (IOException e) {
			logger.warn(e.getMessage());
		}
		
		
	}
	
	public void testOrganismsList() {
		
		CrawlClient client = new CrawlClient(baseURL); 
		
		try {
			
			Map<String, String[]> parameters = new HashMap<String, String[]>();
			parameters.put("region", new String[] {"Pf3D7_01"});
			parameters.put("start", new String[] {"100"});
			parameters.put("end", new String[] {"10000"});
			
			List<LocatedFeature> features = client.request(LocatedFeature.class, "regions", "locations", parameters);
			
			for (LocatedFeature o : features) {
				logger.info(o.uniqueName);
			}
			
		} catch (IOException e) {
			logger.warn(e.getMessage());
		}
		
		
		
	}
	
}
