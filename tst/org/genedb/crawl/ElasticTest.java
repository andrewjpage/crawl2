package org.genedb.crawl;

import junit.framework.TestCase;

import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;



import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.xcontent.QueryBuilders;
import org.elasticsearch.index.query.xcontent.XContentQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;


public class ElasticTest extends TestCase {
	
	Client client = new TransportClient().addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
	
	public void test1() {
		
		// can run this first if you want to make sure that uniqueNames are stored.
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("		    \"Feature\" : {");
		sb.append("		      \"properties\" : {");
		sb.append("		        \"uniqueName\" : {");
		sb.append("		          \"type\" : \"string\",");
		sb.append("		          \"store\" : \"yes\"");
		sb.append("		        }");
		sb.append("		      }");
		sb.append("		    }");
		sb.append("		  }");
		
		String mappingSource = sb.toString();
		
		PutMappingRequest pmr = new PutMappingRequest("features");
		pmr.source(mappingSource);
		
		
		PutMappingResponse response = client.admin().indices().putMapping(pmr).actionGet();
		
		System.out.println(response.acknowledged());
		
		
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
