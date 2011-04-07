package org.genedb.crawl.elasticsearch.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Properties;

import org.elasticsearch.client.Client;

import org.genedb.crawl.elasticsearch.Connection;
import org.genedb.crawl.elasticsearch.LocalConnection;
import org.genedb.crawl.elasticsearch.TransportConnection;
import org.genedb.crawl.json.JsonIzer;


import org.kohsuke.args4j.Option;

public abstract class IndexBuilder {
	
	@Option(name = "-h", aliases = {"--help"}, usage = "Print help")
	public boolean help;
	
	@Option(name = "-pe", aliases = {"--properties_elasticsearch"}, usage = "A properties file specifying elastic search connection details", required=true)
	public File elasticSearchPropertiesFile;
	
//	@Option(name = "-n", aliases = {"--no_features"}, usage = "Do not index features, just load organisms.", required = false)
//	public boolean noFeatures = false;
	
	private Properties elasticSearchProperties;
	
	protected JsonIzer jsonIzer = JsonIzer.getJsonIzer();
	protected Client client;
	protected Connection connection;
	
	
	protected void setupIndex() throws IOException {
		
		elasticSearchProperties = new Properties();
		elasticSearchProperties.load(new FileInputStream(elasticSearchPropertiesFile));
		
		// if transport connection
		if (elasticSearchProperties.getProperty("resource.elasticsearch.address.host") != null) {
			                                     
			TransportConnection tc = new TransportConnection(); 
			tc.setHost(elasticSearchProperties.getProperty("resource.elasticsearch.address.host"));
			
			if (elasticSearchProperties.getProperty("resource.elasticsearch.address.port") != null) {
				tc.setPort(Integer.parseInt(elasticSearchProperties.getProperty("resource.elasticsearch.address.port")));
			}
			
			tc.configure();
			connection = tc;
			
		} else {
			
			LocalConnection lc = new LocalConnection();
			lc.setPathData(elasticSearchProperties.getProperty("resource.elasticsearch.local.pathdata"));
			lc.setPathLogs(elasticSearchProperties.getProperty("resource.elasticsearch.local.pathlogs"));
			lc.configure();
			connection = lc;
			
		}
		
		connection.setIndex(elasticSearchProperties.getProperty("resource.elasticsearch.index"));
		connection.setFeatureType(elasticSearchProperties.getProperty("resource.elasticsearch.featureType"));
		connection.setOrganismType(elasticSearchProperties.getProperty("resource.elasticsearch.organismType"));
		connection.setRegionType(elasticSearchProperties.getProperty("resource.elasticsearch.regionType"));
		
		
		client = connection.getClient();
		
	}
	
	protected void closeIndex() {
		if (client != null) {
			client.close();
		}
	}
	
	
}