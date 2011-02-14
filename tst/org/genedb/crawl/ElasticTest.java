package org.genedb.crawl;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import junit.framework.TestCase;

import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.get.GetResponse;



import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.action.get.GetRequestBuilder;
import org.elasticsearch.client.action.search.SearchRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.LocalTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.xcontent.QueryBuilders;
import org.elasticsearch.index.query.xcontent.XContentQueryBuilder;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.genedb.crawl.elasticsearch.LocalConnection;


public class ElasticTest extends TestCase {
	
	Client client;
	
	public void setUp() {
		Node node = nodeBuilder().local(true).node();
		client = node.client();
	}
	
	public static String toString(ToXContent tmp) {
	       try {
	           return
	           tmp.toXContent(JsonXContent.unCachedContentBuilder(),
	        		   ToXContent.EMPTY_PARAMS).
	                   prettyPrint().
	                   string();
	       } catch (Exception ex) {
	           return "<ERROR:" + ex.getMessage() + ">";
	       }
	   }
	
	public void test1() throws InterruptedException {
		
//		Settings settings = ImmutableSettings.settingsBuilder().put("node.local", "true").build();
//		TransportClient transportClient = new TransportClient(settings);
//		transportClient.addTransportAddress((TransportAddress) new LocalTransportAddress("1"));
//		
//		client = transportClient;
//		
//		System.out.println(client);
		
		Thread.sleep(10000);

		
		//sleep
		
		//client.admin().indices().prepareStatus("features");
		
		
		String uniqueName = "Pfalciparum_REP_45";
		
		GetRequestBuilder grb = client.prepareGet("features", "Feature", uniqueName);
		
		System.out.println(grb);
		
		GetResponse response = grb.execute().actionGet();
		
		System.out.print(response.sourceAsString());
		
		
		
		SearchRequestBuilder srb = client.prepareSearch("features")
			.setQuery( QueryBuilders.fieldQuery("uniqueName", uniqueName)).addField("uniqueName");
		
		System.out.println("??");
		System.out.println(toString(srb.internalBuilder()));
		
//		
//		
//		SearchResponse searchResponse = srb.execute().actionGet();
//		
//		if (searchResponse.getHits().totalHits() == 1) {
//			System.out.println("returning ");
//			
//			SearchHit hit = searchResponse.hits().getAt(0);
//			
//			System.out.println(hit.sourceAsString());
//			
//			System.out.println(hit.fields());
//			
//			System.out.println(hit.sourceAsString());
//			
//		}
		
		// can run this first if you want to make sure that uniqueNames are stored.
		
//		StringBuilder sb = new StringBuilder();
//		sb.append("{");
//		sb.append("		    \"Feature\" : {");
//		sb.append("		      \"properties\" : {");
//		sb.append("		        \"uniqueName\" : {");
//		sb.append("		          \"type\" : \"string\",");
//		sb.append("		          \"store\" : \"yes\"");
//		sb.append("		        }");
//		sb.append("		      }");
//		sb.append("		    }");
//		sb.append("		  }");
//		
//		String mappingSource = sb.toString();
//		
//		PutMappingRequest pmr = new PutMappingRequest("features");
//		pmr.source(mappingSource);
		
		
//		PutMappingResponse response = client.admin().indices().putMapping(pmr).actionGet();
//		
//		System.out.println(response.acknowledged());
		
		
//		
//		
//		
//		// client.admin().indices().pu
//		
//		
//		 SearchResponse searchResponse = client.prepareSearch("test")
//		 .setQuery(QueryBuilders.termQuery("child._id", "PFA0005w:mRNA"))
//		 .addFields("_parent")
//		 .execute().actionGet();
//		
//		
//		
//		
//		XContentQueryBuilder getFeatureQuery = QueryBuilders.termQuery("source.uniqueName", "PFA0005w:pep");
//		
//		SearchHits hitsObject = client.prepareSearch("features").setQuery(getFeatureQuery).execute().actionGet().getHits();
//		
//		System.out.println(getFeatureQuery.toString());
//		
//		SearchHit[] hits = hitsObject.hits();
//		
//		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//		
//		System.out.println(hits);
//		
//		for (SearchHit hit : hits) {
//			System.out.println(hit.getSource());
//		}
		
	}
	
}
