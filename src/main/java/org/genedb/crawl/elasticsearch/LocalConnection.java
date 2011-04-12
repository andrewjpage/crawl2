package org.genedb.crawl.elasticsearch;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

public class LocalConnection extends BaseConnection implements Connection {

	private Logger logger = Logger.getLogger(LocalConnection.class);
	
	Client client;
	Node node;
	
	String pathData;
	String pathLogs;
	
	public void setPathData(String pathData) {
		this.pathData = pathData;
	}
	
	public void setPathLogs(String pathLogs) {
		this.pathLogs = pathLogs;
	}
	
	@PostConstruct
	public void configure () {
		Settings settings = ImmutableSettings.settingsBuilder()
	        .put("path.logs",pathLogs)
	        .put("path.data",pathData)
	        //.put("name", "creepy crawler")
	        //.put("cluster.name", "crawl")
	        .build();
		
		node = nodeBuilder().settings(settings).data(true).local(true).node();
		
		logger.info("Started local node at " + pathData + " settings " + node.settings().getAsMap());
		
		client = node.client();
		
		ClusterHealthRequest clusterHealth = new ClusterHealthRequest();
		//clusterHealth.waitForGreenStatus();
		ClusterHealthResponse response;
		
		boolean ok = false;
		
		while (! ok) {
			response = client.admin().cluster().health(clusterHealth).actionGet();
			ClusterHealthStatus status = response.getStatus();
			
			if (status.equals(ClusterHealthStatus.GREEN) || status.equals(ClusterHealthStatus.YELLOW)) {
				logger.info(status);
				logger.info("Active shards: " + response.activeShards());
				ok = true;
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		logger.info("Cluster initialized.");
		
	}
	
	
	public Client getClient() {
		return client;
	}

	public void close() {
		if (client != null) {
			client.close();
		}
		if (node != null) {
			node.close();	
		}
		
	}
}
