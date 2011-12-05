package org.genedb.crawl.elasticsearch;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class TransportConnection extends Connection {
	
	private Logger logger = Logger.getLogger(TransportConnection.class);
	
	TransportClient client;
	private String host;
	private int port = 9300;
	private String clusterName;
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
	
	@Override
	@PostConstruct
	public void configure () {
		
		Settings settings = ImmutableSettings.settingsBuilder()
        	.put("cluster.name",clusterName)
        	.build();
		
		client = new TransportClient(settings);
		client.addTransportAddress(new InetSocketTransportAddress(host, port));
		
		logger.info(String.format("Setup transport client %s : %d, cluster name %s", host, port, settings.get("cluster.name")));
	}
	
	@Override
	public Client getClient() {
		return (Client) client;
	}
	
	@Override
	public void close() {
		if (client != null) {
			client.close();
		}
	}
	
	@Override
    public String toString() {
        return String.format("%s, Host: %s, Port: %s, Cluster: %s", super.toString(), host, port, clusterName);
    }
	
}
