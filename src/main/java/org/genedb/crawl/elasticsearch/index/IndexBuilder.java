package org.genedb.crawl.elasticsearch.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.elasticsearch.client.Client;

import org.genedb.crawl.elasticsearch.Connection;
import org.genedb.crawl.elasticsearch.LocalConnection;
import org.genedb.crawl.elasticsearch.TransportConnection;
import org.genedb.crawl.json.JsonIzer;


import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public abstract class IndexBuilder {
	
	static Logger logger = Logger.getLogger(IndexBuilder.class);
	
	@Option(name = "-h", aliases = {"--help"}, usage = "Print help")
	public boolean help;
	
	@Option(name = "-pe", aliases = {"--properties_elasticsearch"}, usage = "A properties file specifying elastic search connection details", required=true)
	public File elasticSearchPropertiesFile;
	
	protected Properties elasticSearchProperties;
	
	protected JsonIzer jsonIzer = new JsonIzer();
	protected Client client;
	protected Connection connection;
	
	public abstract void run() throws Exception;
	
	protected IndexBuilder prerun (String[] args) throws Exception {
		
		CmdLineParser parser = new CmdLineParser(this);
		
		try {
			
			parser.parseArgument(args);
		
			if (this.help) {
				parser.setUsageWidth(80);
	            parser.printUsage(System.out);
	            System.exit(1);
			}
			
			this.run();
		
		} catch (CmdLineException e) {
			logger.error(e.getMessage());
            parser.setUsageWidth(80);
            parser.printUsage(System.out);
            System.exit(1);
		} /*finally {
			closeIndex();
		} */
		
		return this;
	}
	
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
			
			tc.setClusterName(elasticSearchProperties.getProperty("resource.elasticsearch.cluster.name"));
			
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
		connection.setOntologyIndex(elasticSearchProperties.getProperty("resource.elasticsearch.ontologyIndex"));
		connection.setFeatureType(elasticSearchProperties.getProperty("resource.elasticsearch.featureType"));
		connection.setOrganismType(elasticSearchProperties.getProperty("resource.elasticsearch.organismType"));
		connection.setRegionType(elasticSearchProperties.getProperty("resource.elasticsearch.regionType"));
		
		
		client = connection.getClient();
		
	}
	
	public void closeIndex() {
		if (connection != null) {
			connection.close();
		}
	}
	
	
}