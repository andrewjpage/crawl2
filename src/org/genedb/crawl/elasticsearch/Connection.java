package org.genedb.crawl.elasticsearch;

import org.elasticsearch.client.Client;

public interface Connection {
	
	public Client getClient();
	
}
