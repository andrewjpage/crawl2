package org.genedb.crawl.elasticsearch;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

public class LocalConnection implements Connection {

	Client client;
	
	public LocalConnection() {
		Node node = nodeBuilder().local(true).node();
		client = node.client();
	}
	
	public Client getClient() {
		return client;
	}
}
