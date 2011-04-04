package org.genedb.crawl.elasticsearch;

import javax.annotation.PostConstruct;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

public class LocalConnection extends BaseConnection implements Connection {

	Client client;
	
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
	        .build();
		
		Node node = nodeBuilder().settings(settings).local(true).node();

		client = node.client();
	}
	
	public Client getClient() {
		return client;
	}
}
