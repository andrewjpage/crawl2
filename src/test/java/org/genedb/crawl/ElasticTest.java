package org.genedb.crawl;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.elasticsearch.ElasticSearchException;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.action.search.SearchRequestBuilder;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.FieldQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.genedb.crawl.elasticsearch.LocalConnection;


/*
public class ElasticTest extends TestCase {



	private Logger logger = Logger.getLogger(ElasticTest.class);

	Client client;
	LocalConnection connection;
	List<RegionMap> regions = new ArrayList<RegionMap>();

	final int organismID = 1;

	class RegionMap {
		String region;
		int organismID;
		String sequence;
		RegionMap(String region, int organismID, String sequence) {
			this.region = region;
			this.organismID = organismID;
			this.sequence = sequence;
		}
	}


	private static String toString(ToXContent tmp) {
		try {
			return tmp
					.toXContent(JsonXContent.unCachedContentBuilder(),
							ToXContent.EMPTY_PARAMS).prettyPrint().string();
		} catch (Exception ex) {
			return "<ERROR:" + ex.getMessage() + ">";
		}
	}

	void index(String key, int organism_id, String sequence) throws ElasticSearchException, IOException {
		client.prepareIndex("annotations", "region", key)
        .setSource(jsonBuilder()
                    .startObject()
                        .field("uniqueName", key)
                        .field("organism_id", organism_id)
                        .field("sequence", sequence)
                    .endObject()
                  )
        .execute()
        .actionGet();
	}

	SearchHits search(int organism_id, String[] fields) {

		FieldQueryBuilder organismQuery =
			QueryBuilders.fieldQuery("organism_id", organism_id);

		SearchRequestBuilder srb = client
			.prepareSearch("annotations")
			.setTypes("region")
			.setQuery(organismQuery);

		// if any fields are specified, the source is not returned
		if (fields != null) {
			srb.addFields(fields);
		}

		System.out.println(toString(srb.internalBuilder()));

		SearchResponse response = srb.execute()
			.actionGet();

		SearchHits hits = response.getHits();

		return hits;

	}


	public void setUp() {
		connection = new LocalConnection();
		connection.setPathData("/tmp/es/data");
		connection.setPathLogs("/tmp/es/logs");
		connection.configure();

		client = connection.getClient();
	}

	public void tearDown() {
		connection.close();
	}


	public void test1() throws InterruptedException, ElasticSearchException, IOException {

		regions.add(new RegionMap("region1", organismID, "atgc"));
		regions.add(new RegionMap("region2", organismID, "atgcatgc"));
		regions.add(new RegionMap("region3", organismID, "atgcatgcatgc"));

		for (RegionMap regionMap : regions) {
			index(regionMap.region, regionMap.organismID, regionMap.sequence);
		}

		logger.info("Waiting for green");
		ClusterHealthRequest health = new ClusterHealthRequest();
		health.waitForGreenStatus();
		logger.info(client.admin().cluster().health(health).actionGet().getStatus());

		SearchHits search = search(organismID, new String[] {"sequence"} );

		assertEquals(regions.size(), search.getTotalHits());

		for (SearchHit hit : search) {
			logger.info(hit.id());
			assertTrue(hit.field("sequence") != null);
		}



	}

}
*/