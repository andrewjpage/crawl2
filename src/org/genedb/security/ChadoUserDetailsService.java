package org.genedb.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;


public class ChadoUserDetailsService implements UserDetailsService {
	
	private Logger logger = Logger.getLogger(ChadoUserDetailsService.class);
	
	@Autowired
	private ChadoSession chadoSession;
	
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException, DataAccessException {
		logger.info("Fetching details for user " + username);
		return new ChadoUserDetails(chadoSession, username);
	}

}

class ChadoAuthenticationProvider implements AuthenticationProvider {
	
	private Logger logger = Logger.getLogger(ChadoAuthenticationProvider.class);
	
	@Autowired
	ChadoSession chadoSession;
	
	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		if ((! StringUtils.hasText((String) authentication.getPrincipal())) || 
				(! StringUtils.hasText((String) authentication.getCredentials()))) {	
			throw new BadCredentialsException("Invalid username/password");
		}
		
		logger.info(authentication);
		logger.info(authentication.getPrincipal());
		logger.info(authentication.getDetails());
		logger.info(authentication.getCredentials());
		
		if (authentication instanceof UsernamePasswordAuthenticationToken) {
			
			String username = (String) authentication.getPrincipal();
			String password = (String) authentication.getCredentials();
			
			if (chadoSession.isAuthentic(username, password)) {
				logger.info("user authenticated");
				ChadoSessionToken token = new ChadoSessionToken(chadoSession.userGet(username), chadoSession);
				token.setAuthenticated(true);
				logger.info("authorities:");
				for (GrantedAuthority auth : token.getAuthorities()) {
					logger.info(auth.getAuthority());
				}
				return token;
			}
			logger.warn("not authentic");
			
		}
		
		logger.warn("cannot handle authentication type :" + authentication.getClass());
		
		return null;
	}

	public boolean supports(Class<? extends Object> authentication) {
        return (Authentication.class.isAssignableFrom(authentication));
    }
	
}

class ChadoSessionToken implements Authentication {
	
	private ChadoUser user;
	private ChadoSession chadoSession;
	private boolean authenticated;
	
	public ChadoSessionToken(ChadoUser user, ChadoSession chadoSession) {
		this.user = user;
		this.chadoSession = chadoSession;
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		List<ChadoRole> roles = chadoSession.userRoleGet(user.username);
		for (ChadoRole role : roles) {
			authorities.add(new ChadoGrantedAuthority(role.rolename));
		}
		return authorities;
	}

	@Override
	public Object getCredentials() {
		return user.passhash;
	}

	@Override
	public Object getDetails() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return user.username;
	}

	@Override
	public boolean isAuthenticated() {
		return true;
	}

	@Override
	public void setAuthenticated(boolean authenticated) throws IllegalArgumentException {
		this.authenticated = authenticated;
		
	}

	@Override
	public String getName() {
		return user.username;
	}
	
	
	
}

class ChadoGrantedAuthority implements GrantedAuthority {
	
	private String authority;
	
	public ChadoGrantedAuthority(String authority) {
		this.authority = authority;
	}
	
	@Override
	public String getAuthority() {
		return authority;
	}
	
}

class ChadoUserDetails implements UserDetails {
	
	
	
	private ChadoSession chadoSession;
	private ChadoUser user;
	
	ChadoUserDetails(ChadoSession chadoSession, String username) {
		this.chadoSession = chadoSession;
		this.user = this.chadoSession.userGet(username);
	}
	
	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		List<ChadoRole> roles = chadoSession.userRoleGet(getUsername());
		for (ChadoRole role : roles) {
			authorities.add(new ChadoGrantedAuthority(role.rolename));
		}
		return authorities;
	}

	@Override
	public String getPassword() {
		return user.passhash;
	}

	@Override
	public String getUsername() {
		return user.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
}