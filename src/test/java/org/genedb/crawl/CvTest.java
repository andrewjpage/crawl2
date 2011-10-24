package org.genedb.crawl;

import java.io.File;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.genedb.crawl.elasticsearch.index.cv.CvIndexBuilder;

import junit.framework.TestCase;

public class CvTest extends TestCase {

    private Logger logger = Logger.getLogger(CvTest.class);

    public void testLoad() throws Exception {
        
        CvIndexBuilder builder = new CvIndexBuilder();
        builder.cvFiles = Arrays.asList(new String[] {"src/test/resources/cv/gene_ontology_ext.obo"});
        builder.elasticSearchPropertiesFile = new File("resource-elasticsearch-local.properties");
        builder.namespaces = Arrays.asList(new String[] {"biological_process", "molecular_function", "cellular_component"} );
        builder.vocabularyName = "go";
        
        logger.info("Starting test");
        builder.run();
        logger.info("Test complete");
    }

}
