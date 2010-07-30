package org.genedb.security;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.genedb.crawl.QueryMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ChadoSession {

	private Logger logger = Logger.getLogger(ChadoSession.class);
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public QueryMap queryMap;
	
	private Random r = new Random();
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
	    this.jdbcTemplate = new JdbcTemplate(dataSource);
	    
	    // a quick and dirty way to check for an authentication schema
	    int count = jdbcTemplate.queryForInt("select count(*) from pg_tables where schemaname='authentication'");
	    logger.info("Number of tables in authentication schema : " + count);
	    
	    if (count == 0) {
	    	this.schemaCreate();
	    }
	}
	
	
	public String login(String username, String password) {
		boolean authentic = isAuthentic(username, password);
		if (! authentic) {
			throw new RuntimeException("Could not authenticate the user" + username);
		}
		String token = sessionCreate(username);
		return token;
	}
	
	public void logout(String token) {
		ChadoUserSession userSession = sessionGet(token);
		
		if (! isAuthentic(token)) {
			throw new RuntimeException("Could not authenticate the token" + token);
		}
		
		if (userSession == null) {
			throw new RuntimeException("There is no session for the token " + token);
		}
		
		sessionDestroy(token);
		
	}
	
	public boolean isAuthentic(String username, String password) {
		ChadoUser user = userGet(username);
		String dbPasshashed = user.passhash;
		String suppliedPasshashed = getMd5Digest(user.salt  + password);
		
		logger.info("username " + username);
		logger.info("password " + password);
		logger.info(dbPasshashed + " -- " + suppliedPasshashed);
		
		if (dbPasshashed.equals(suppliedPasshashed)) {
			return true;
		}
		return false;
	}
	
	public boolean isAuthentic(String token) {
		ChadoUserSession userSession = sessionGet(token);
		if (userSession != null) {
			
			String sql = this.queryMap.getQuery("session_update");
			logger.info(sql);
			
			Timestamp refreshed = new Timestamp(System.currentTimeMillis());
			logger.info(refreshed);
			int result = jdbcTemplate.update(sql, new Object[] {refreshed, userSession.token});
			logger.info(result);
			
			return true;
		}
		return false;
	}
	
	public ChadoUser userCreate(String username, String email, String password) {
		
		String salt = this.randomAlphanumeric();
		
		String passhashed = getMd5Digest(salt  + password);
		int result = jdbcTemplate.update(queryMap.getQuery("user_create"),
			new Object[] { username, email, salt, passhashed }
		);
		logger.info(result);
		return userGet(username);
	}
	
	public void schemaCreate() {
		try {
			String sql = queryMap.getQuery("session_create_schema");
			logger.info(sql);
			jdbcTemplate.execute(sql);
			
			this.roleAdd("USER_ADMIN");
			this.userCreate("admin", "admin@genedb.org", "admin");
			this.userRoleAdd("admin", "USER_ADMIN");
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}
	
	public void schemaDestroy() {
		try {
			String sql = queryMap.getQuery("session_destroy_schema");
			logger.info(sql);
			jdbcTemplate.execute(sql);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}
	
	public ChadoRole roleAdd(String rolename) {
		ChadoRole role = null;
		try {
			role = roleGet(rolename);
		} catch (Exception e) {
			logger.warn(e.getMessage());
			logger.warn("Assuming role does not exist, creating...");
			String sql = "INSERT INTO authentication.role (rolename) VALUES (?) ";
			jdbcTemplate.update(sql, new Object[] {rolename});
			role = roleGet(rolename);
		}
		return role;
	}
	
	public ChadoUser userGet(String username) {
		logger.info("user_get");
		logger.info(queryMap.getQuery("user_get"));
		return jdbcTemplate.queryForObject( queryMap.getQuery("user_get"), new RowMapper<ChadoUser>() {
	        public ChadoUser mapRow(ResultSet rs, int rowNum) throws SQLException {
	        	ChadoUser u = new ChadoUser ();
				u.username = rs.getString("username");
				u.user_id = rs.getInt("user_id");
				u.email = rs.getString("email");
				u.passhash = rs.getString("passhash");
				u.salt = rs.getString("salt");
				return u;
	        }
		}, new Object[]{username});
	}
	
	private ChadoRole roleGet(String rolename) {
		return jdbcTemplate.queryForObject( queryMap.getQuery("role_get"), new RowMapper<ChadoRole>() {
			public ChadoRole mapRow(ResultSet rs, int rowNum) throws SQLException {
				ChadoRole role = new ChadoRole();
				role.role_id = rs.getInt("role_id");
				role.rolename = rs.getString("rolename");
				return role;
			}
		}, new Object[]{rolename});
	}
	
	
	
	
	private void userRoleAdd(String username, String rolename) {
		
		ChadoRole role;
		ChadoUser user;
		
		try {
			role = roleGet(rolename);
		} catch (Exception e) {
			throw new RuntimeException("Could not find role " + rolename, e);
		}
		
		try {
			user = userGet(username);
		} catch (Exception e) {
			throw new RuntimeException("Could not find user " + username, e);
		}
		
		List<ChadoRole> roles = userRoleGet(username);
		
		for (ChadoRole r : roles) {
			if (r.rolename.equals(rolename)) {
				return;
			}
		}
		
		String sql = " INSERT INTO authentication.user_role (user_id, role_id) VALUES (?, ?) ";
		jdbcTemplate.update(sql, new Object[] {user.user_id, role.role_id} );
		
	}
	
	public List<ChadoRole> userRoleGet(String username) {
		String sql = " SELECT r.role_id, r.rolename FROM authentication.role r  " +
				" JOIN authentication.user_role ur ON ur.role_id = r.role_id " +
				" JOIN authentication.user u ON u.user_id = ur.user_id " +
				" WHERE u.username = ? ";
		
		List<ChadoRole> roles = jdbcTemplate.query(sql, new RowMapper<ChadoRole>() {
			public ChadoRole mapRow(ResultSet rs, int rowNum) throws SQLException {
				ChadoRole role = new ChadoRole();
				role.role_id = rs.getInt("role_id");
				role.rolename = rs.getString("rolename");
				return role;
			}
		}, new Object[] {username} );
		
		return roles;
	}
	
	private ChadoUserSession sessionGet(String token) {
		return jdbcTemplate.queryForObject( queryMap.getQuery("session_get"), new RowMapper<ChadoUserSession>() {
	        public ChadoUserSession mapRow(ResultSet rs, int rowNum) throws SQLException {
	        	ChadoUserSession us = new ChadoUserSession ();
	        	us.session_id = rs.getInt("session_id");
	        	us.token = rs.getString("token");
	        	us.user_id = rs.getInt("user_id");
	        	us.started = rs.getTimestamp("started");
	        	us.refreshed = rs.getTimestamp("refreshed");
				return us;
	        }
		}, new Object[]{token});
	}
	
	private boolean hasSessionWithToken(String token) {
		List <Map<String, Object>> results = jdbcTemplate.queryForList(
				queryMap.getQuery("session_get"), 
				new Object[]{token}
		);
		
		for (Map<String, Object> map : results) {
			logger.info("token :: " + map.get("token"));
		}
		
		if (results.size() > 0) {
			return true;
		}
		return false;
	}
	
	private String sessionCreate(String username) {
		String sql = queryMap.getQuery("session_new");
		
		ChadoUser user = userGet(username);
		//int token =  r.nextInt(100000000);
		
		String token = randomAlphanumeric();
		
		while ( hasSessionWithToken(token) == true ) {
			token = randomAlphanumeric();
		}
		
		logger.info(username + " " + token);
		
		
		Timestamp started = new Timestamp(System.currentTimeMillis());
		Timestamp refreshed = started;
		logger.info(refreshed);
		
		int result = jdbcTemplate.update(sql, new Object[] { user.user_id, token, started, refreshed  });
		logger.info(result);
		
		return token;
	}
	
	private void sessionDestroy(String token) {
		ChadoUserSession session = sessionGet(token);
		if (session != null) {
			String sql = queryMap.getQuery("session_delete");
			int result = jdbcTemplate.update(sql, new Object[] {session.token});
			logger.info(result);
		}
	}
	
	
	
	
	
	static String getMd5Digest(String input) 
    {
        try
        {
            MessageDigest md = (MessageDigest) MessageDigest.getInstance("MD5").clone();
            md.reset();

            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1,messageDigest);
            return number.toString(16);
        }
        catch(NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
        catch (CloneNotSupportedException ee) {
        	throw new RuntimeException(ee);
        }
    }
	
	
	private final int tokenLength = 32;
	private final String tokenCharacters = "abcdefghijklmnopqrstuvwxyz0123456789";
	
	private String randomAlphanumeric() {
		final StringBuffer token = new StringBuffer();
		while (token.length() < tokenLength) {
			token.append(tokenCharacters.charAt(r.nextInt(tokenCharacters.length())));
		}
		return token.toString();
	}
	
}






