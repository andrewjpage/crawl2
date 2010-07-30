package org.genedb.crawl.model;

import org.springframework.jdbc.core.JdbcTemplate;

public class AuthenticationSetup {
	
	private JdbcTemplate jdbcTemplate;
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	private String check = "select * from pg_tables where schemaname = 'authentication'";
	
	
	
	public void checkUp() {
		
		
		
	}
	
}
