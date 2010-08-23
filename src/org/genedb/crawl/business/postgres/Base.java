package org.genedb.crawl.business.postgres;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public abstract class Base {
	
	private Logger logger = Logger.getLogger(Base.class);
	
	protected NamedParameterJdbcTemplate jdbcTemplate;
	protected QueryMap queryMap;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
	    this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	    logger.info ("Data source set: " + dataSource);
	}
	
	@Autowired
	public void setQueryMap(QueryMap queryMap) {
		this.queryMap = queryMap;
		logger.info("Query map set: " + queryMap);
	}
	
}
