package org.genedb.crawl.search.index;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.action.index.IndexRequestBuilder;
import org.elasticsearch.client.action.search.SearchRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.xcontent.BoolQueryBuilder;
import org.elasticsearch.index.query.xcontent.QueryBuilders;
import org.elasticsearch.index.query.xcontent.XContentQueryBuilder;
import org.genedb.crawl.model.Feature;
import org.kohsuke.args4j.Option;

public abstract class IndexBuilder {
	
	private Logger logger = Logger.getLogger(IndexBuilder.class);
	
	@Option(name = "-h", aliases = {"--help"}, usage = "Print help")
	public boolean help;
	
	JsonIzer jsonIzer = new JsonIzer();
	Client client;
	
	public IndexBuilder() {
		super();
	}
	
	void setupIndex() throws IOException {
		client = new TransportClient().addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
	}
	
	void closeIndex() {
		if (client != null) {
			client.close();
		}
	}
	
	void sendFeaturesToIndex(List<Feature> features) throws IOException {
		
		for (Feature feature : features) {
			
			logger.debug("Storing: " + feature.uniqueName);
			
			IndexRequestBuilder builder = client.prepareIndex("features", "Feature", feature.uniqueName);
			String json = jsonIzer.toJson(feature);
			
			logger.debug(json);
			
			builder.setSource(json).execute().actionGet();
			GetResponse response = client.prepareGet("features", "Feature", feature.uniqueName).execute().actionGet();
			
			logger.trace(response.sourceAsString());
			
			
			
			
		}
		
		
		
		
		SearchRequestBuilder srb = client.prepareSearch("feature");
		
		XContentQueryBuilder query1 = QueryBuilders.fieldQuery("uniqueName", features.get(0).uniqueName);
		XContentQueryBuilder query2 = QueryBuilders.fieldQuery("uniqueName", features.get(0).uniqueName);
		
		BoolQueryBuilder query3 = QueryBuilders.boolQuery();
		query3.must(query1);
		query3.must(query2);
		
		srb.setQuery(query3);
		
	}

}