package org.genedb.crawl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.genedb.crawl.client.CrawlClient;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.Organism;

import junit.framework.TestCase;

/**
 * Tests a simple client.
 * 
 * 
 */
public class ClientTest extends TestCase {

    private Logger              logger  = Logger.getLogger(ClientTest.class);

    private static final String baseURL = "http://beta.genedb.org/services";

    public void testOrganisms() throws CrawlException, IOException {

        CrawlClient client = new CrawlClient(baseURL);

        List<Organism> organisms = client.request(List.class, Organism.class, "organisms", "list", null);
        
        assertTrue(organisms.size() > 0);
        
        

    }

    public void testListRegions() throws CrawlException, IOException {

        CrawlClient client = new CrawlClient(baseURL);

        List<Organism> organisms = client.request(List.class, Organism.class, "organisms", "list", null);

        for (Organism organism : organisms) {
            logger.info(" -" + organism.common_name);

            Map<String, String[]> inOrganismParameters = new HashMap<String, String[]>();
            inOrganismParameters.put("organism", new String[] { organism.common_name });

            List<Feature> regions = client.request(List.class, Feature.class, "regions", "inorganism", inOrganismParameters);
            
            int n = 0;
            for (Feature region : regions) {

                if (n == 0) {
                    
                    assertTrue(region.uniqueName != null);
                    
                    logger.info("  -" + region.uniqueName);

                    Map<String, String[]> locationsParameters = new HashMap<String, String[]>();
                    locationsParameters.put("region", new String[] { region.uniqueName });

                    locationsParameters.put("start", new String[] { "1" });
                    locationsParameters.put("end", new String[] { "1000000" });
                    locationsParameters.put("exclude", new String[] { "false" });
                    locationsParameters.put("types", new String[] { "gene" });
                    
                    List<LocatedFeature> features = client.<List> request(List.class, LocatedFeature.class, "regions", "locations", locationsParameters);

                    if (features.size() > 0) {
                        LocatedFeature feature = features.get(0);
                        
                        assertTrue(feature.uniqueName != null);
                        
                        Map<String, String[]> infoParameters = new HashMap<String, String[]>();
                        infoParameters.put("uniqueName", new String[] { feature.uniqueName });

                        Feature gene = (Feature) client.<Feature> request(Feature.class, "feature", "hierarchy", infoParameters);

                        logger.info("   -" + feature.uniqueName);

                        for (Feature child : gene.children) {
                            logger.info("    -" + child.uniqueName);
                        }
                    }

                }

                n++;

            }

        }

    }

}
