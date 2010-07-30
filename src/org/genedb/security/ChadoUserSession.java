package org.genedb.security;

import java.sql.Timestamp;

public class ChadoUserSession {
	public String token;
	public int user_id;
	public int session_id;
	public Timestamp started;
	public Timestamp refreshed;
}
