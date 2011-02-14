package org.genedb.crawl.elasticsearch.index;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.action.index.IndexRequestBuilder;
import org.elasticsearch.client.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.xcontent.BoolQueryBuilder;
import org.elasticsearch.index.query.xcontent.QueryBuilders;
import org.elasticsearch.index.query.xcontent.XContentQueryBuilder;
import org.genedb.crawl.elasticsearch.Connection;
import org.genedb.crawl.elasticsearch.LocalConnection;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.gff.Sequence;
import org.kohsuke.args4j.Option;

public abstract class IndexBuilder {
	
	private Logger logger = Logger.getLogger(IndexBuilder.class);
	
	@Option(name = "-h", aliases = {"--help"}, usage = "Print help")
	public boolean help;
	
	JsonIzer jsonIzer = new JsonIzer();
	Client client;
	
	Connection connection = new LocalConnection();
	
	public IndexBuilder() {
		super();
	}
	
	protected void setupIndex() throws IOException {
		client = connection.getClient();
	}
	
	protected void closeIndex() {
		if (client != null) {
			client.close();
		}
	}
	
	protected void sendSequencesToIndex(List<Sequence> sequences) throws IOException {
		
		for (Sequence sequence : sequences) {
			
			String json = jsonIzer.toJson(sequence);
			
			IndexResponse response = 
				client.prepareIndex("sequences", "Sequence", sequence.name)
					.setSource(json)
					.execute()
					.actionGet();
			
			logger.debug("Response: version..." + response.getVersion());
			
		}
		
	}
	
	protected void sendFeaturesToIndex(List<Feature> features) throws IOException {
		
		for (Feature feature : features) {
			
			logger.debug("Storing: " + feature.uniqueName);
			
			IndexRequestBuilder builder = client.prepareIndex("features", "Feature", feature.uniqueName);
			String json = jsonIzer.toJson(feature);
			
			
			logger.debug("Source:");
			logger.debug(json);
			
			builder.setSource(json);
			
			if (feature instanceof LocatedFeature) {
				LocatedFeature lFeature = (LocatedFeature) feature;
				if (lFeature.parent != null) {
					logger.debug(String.format("Setting %s as parent of %s!", lFeature.parent, feature.uniqueName));
					builder.setParent(lFeature.parent);
				}
			}
			
			
			
			builder.execute().actionGet();
			GetResponse response = client.prepareGet("features", "Feature", feature.uniqueName).execute().actionGet();
			
			logger.debug("Response:");
			logger.debug(response.sourceAsString());
			
			
			
			
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