package org.genedb.crawl.elasticsearch;

import java.util.EnumSet;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.client.Client;

public abstract class Connection {

	abstract public Client getClient();
	abstract public void close();
	abstract public void configure();

	private static Logger logger = Logger.getLogger(Connection.class);

	String index;
	String featureType;
	String regionType;
	String organismType;

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getFeatureType() {
		return featureType;
	}

	public void setFeatureType(String featureType) {
		this.featureType = featureType;
	}

	public String getRegionType() {
		return regionType;
	}

	public void setRegionType(String regionType) {
		this.regionType = regionType;
	}

	public String getOrganismType() {
		return organismType;
	}

	public void setOrganismType(String organismType) {
		this.organismType = organismType;
	}

	static public void waitForStatus(Client client,
			EnumSet<ClusterHealthStatus> acceptableStatuses) {
		ClusterHealthRequest clusterHealth = new ClusterHealthRequest();
		ClusterHealthResponse response;
		ClusterHealthStatus status = null;
		boolean ok = false;
		
		logger.info("Waiting for status..." + acceptableStatuses.toString());

		while (!ok) {
			response = client.admin().cluster().health(clusterHealth)
					.actionGet();
			status = response.getStatus();

			for (ClusterHealthStatus acceptableStatus : acceptableStatuses) {
				if (acceptableStatus.equals(status)) {
					ok = true;
				}
			}

			logger.debug(status);

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		logger.info("Status achieved: " + status + "!");
	}

}
