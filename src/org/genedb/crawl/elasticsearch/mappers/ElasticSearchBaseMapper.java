package org.genedb.crawl.elasticsearch.mappers;

import org.genedb.crawl.elasticsearch.Connection;
import org.springframework.beans.factory.annotation.Autowired;

public class ElasticSearchBaseMapper {
	
	@Autowired
	Connection connection;
	
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
}
