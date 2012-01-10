package org.genedb.crawl.elasticsearch.index.gff;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.admin.indices.flush.FlushResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.genedb.crawl.elasticsearch.index.NonDatabaseDataSourceIndexBuilder;
import org.genedb.crawl.model.Organism;
import org.kohsuke.args4j.Option;


public class GFFIndexBuilder extends NonDatabaseDataSourceIndexBuilder {
	
	static Logger logger = Logger.getLogger(GFFIndexBuilder.class);
	
	@Option(name = "-g", aliases = {"--gffs"}, usage = "The path to the GFF folder", required = false)
	public String gffs;
	
	@Option(name = "-o", aliases = { "--organism" }, usage = "The organism, expressed as a JSON.", required = false)
	public String organism;
	
	public final static String featureMapping = "org/genedb/crawl/model/Feature.json";
		
	public void run() throws IOException, ParseException, SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		
		init();
		
		IndicesAdminClient indexClient = connection.getClient().admin().indices();
		
		// the put mapping below needs an existing index
		IndicesExistsResponse exists = indexClient.exists(new IndicesExistsRequest(connection.getIndex())).actionGet();
		if (! exists.exists()) {
	        CreateIndexResponse created = indexClient.create(new CreateIndexRequest(connection.getIndex())).actionGet();
	        logger.info("Index set? " + created.acknowledged());
	        
	        if (! created.acknowledged())
	            throw new RuntimeException("Could not create index!");
	        
		}
		
		// apply the put mapping
		String featureMappingJson = getMapping(featureMapping);
        PutMappingResponse mapped = indexClient.preparePutMapping(
                connection.getIndex())
                .setSource(featureMappingJson)
                .setType(connection.getFeatureType())
                .execute()
                .actionGet();
		
        if (! mapped.acknowledged())
            throw new RuntimeException("Could not create mapping!");
        
		
		if (gffs != null) {
			
			if (organism == null) {
				throw new RuntimeException("Please supply an organism if loading a gff because GFF files do not specify their organism");
			}
			
			Organism o = getAndPossiblyStoreOrganism(organism);
			convertPath(gffs,o);
			
		}
		
		FlushResponse fr = indexClient.flush(new FlushRequest(this.connection.getIndex())).actionGet();
		logger.info(String.format("Flush! %s failed,  %s successful, %s total", fr.getFailedShards(), fr.getSuccessfulShards(), fr.getTotalShards()));
		
		logger.debug("Complete");
		
	}
	
	/*
	 * Returns the string for a resource file (to be used for mapping).
	 */
	private String getMapping(String mapping) throws IOException {
	    
	    // fetch the feature mapping definition
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(mapping);
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));
        
        StringBuffer sb = new StringBuffer();
        String line = null;
        while ((line = buf.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
        
	}
	
	public static void main(String[] args) throws Exception {
		new GFFIndexBuilder().prerun(args).closeIndex();
	}

}
