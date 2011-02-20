package org.genedb.crawl.elasticsearch.index;

import java.io.IOException;

import org.elasticsearch.client.Client;
import org.genedb.crawl.elasticsearch.Connection;
import org.genedb.crawl.elasticsearch.LocalConnection;
import org.kohsuke.args4j.Option;

public abstract class IndexBuilder {
	
	@Option(name = "-h", aliases = {"--help"}, usage = "Print help")
	public boolean help;
	
	protected JsonIzer jsonIzer = new JsonIzer();
	protected Client client;
	
	protected Connection connection = new LocalConnection();
	
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
	

}