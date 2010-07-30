package org.genedb.crawl;

import java.security.MessageDigest;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.genedb.crawl.model.SessionInfo;
import org.genedb.security.ChadoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.security.authentication.AuthenticationManager; 

@Controller
@RequestMapping("/sessions")
public class Sessions {
	
	private Logger logger = Logger.getLogger(Sessions.class);
	
	private JdbcTemplate jdbcTemplate;
	
//	@Autowired
//	private QueryMap queryMap;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
	    this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Autowired
	public ChadoSession chadoSession;
	
	//@Autowired @Qualifier("org.springframework.security.authenticationManager")
	//private AuthenticationManager authenticationManager;
	
//	@RequestMapping(method=RequestMethod.GET, value={"/logon", "/logon.*"})
//	public ModelAndView connect(HttpServletRequest request, @RequestParam("user") String user, @RequestParam("pass") String pass) {
//		logger.info(jdbcTemplate);
//		ModelAndView mav = new ModelAndView("service:");
//		
//		//logger.info(message)
//		
//		
//		
//		try {
//		
//		for (Cookie c : request.getCookies()) {
//			logger.info(c.getName());
//			logger.info(c.getPath());
//			logger.info(c.getValue());
//		}
//		
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error(e.getMessage());
//		}
//		
//		logger.info(user + ":" + pass);
//		
//		try {
//			
//			
//			
//			Authentication token = new UsernamePasswordAuthenticationToken(user, pass);
//		    Authentication result = authenticationManager.authenticate(token);
//		    SecurityContextHolder.getContext().setAuthentication(result);
//			
//		    logger.info(request);
//		    
//		    logger.info(result.getDetails());
//		    
//		    logger.info(result);
//		    
//		    mav.addObject("model", result.getDetails());
//	    
//		} catch(AuthenticationException e) {
//			logger.error("Authentication failed: " + e.getMessage());
//			mav.addObject("model", e.getMessage());
//		}
//		
//		mav.addObject("model", request.getSession().getId());
//		logger.info("done");
//		return mav;
//	}
//	
	
	
	@RequestMapping(method=RequestMethod.GET, value={"/logoff", "/logoff.*"})
	public ModelAndView disconnect(@RequestParam("token") String token) {
		logger.info(jdbcTemplate);
		ModelAndView mav = new ModelAndView("service:");
		
		try {
			chadoSession.logout(token);
			mav.addObject("model", "OK");
		} catch (RuntimeException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			mav.addObject("model", "NOT OK");
		}
		
		return mav;
	}
	
	@RequestMapping(method=RequestMethod.GET, value={"/logon", "/logon.*"})
	public ModelAndView connect(HttpServletRequest request, HttpServletResponse response, @RequestParam("username") String username, @RequestParam("password") String password) {
		logger.info(jdbcTemplate);
		ModelAndView mav = new ModelAndView("service:");
		try {
			String token = chadoSession.login(username, password);
			
			SessionInfo si = new SessionInfo();
			si.username = username;
			
			Cookie tokenCookie = new Cookie("token", token);
			
			// very important to set this to / so that it is valid for the entire site
			tokenCookie.setPath("/");
			
			response.addCookie(tokenCookie);
			
			mav.addObject("model", si);
		} catch (RuntimeException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			mav.addObject("model", "NOT OK");
		}
		return mav;
	}

	@RequestMapping(method=RequestMethod.GET, value={"/testloggedon", "/testloggedon.*"})
	public ModelAndView test(HttpServletRequest request, @RequestParam("token") String token) {
		logger.info(jdbcTemplate);
		ModelAndView mav = new ModelAndView("service:");
		
		for (Cookie cookie : request.getCookies()) {
			logger.info(cookie.getName() + " :: " + cookie.getValue());
			if (cookie.getName().equals("token")) {
				if (! cookie.getValue().equals(token)) {
					mav.addObject("ERROR", "TOKENS DO NOT MATCH");
				}
			}
		}
		
		boolean loggedon = false;
		
		try {
			
			loggedon = chadoSession.isAuthentic(token);
			
		} catch (RuntimeException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			
		}
		
		mav.addObject("model", loggedon);
		return mav;
	}
	
	@RequestMapping(method=RequestMethod.GET, value={"/schemaCreate", "/schemaCreate.*"})
	public ModelAndView schemaCreate() {
		ModelAndView mav = new ModelAndView("service:");
		
		chadoSession.schemaCreate();
		
		mav.addObject("model", "OK");
		return mav;
	}
	
	@RequestMapping(method=RequestMethod.GET, value={"/schemaDestroy", "/schemaDestroy.*"})
	public ModelAndView schemaDestroy() {
		ModelAndView mav = new ModelAndView("service:");
		
		chadoSession.schemaDestroy();
		
		mav.addObject("model", "OK");
		return mav;
	}
	
	
	@RequestMapping(method=RequestMethod.GET, value={"/exception", "/exception.*"})
	public ModelAndView testException() throws Exception {
		if (true) {
			throw new Exception("testing exception");
		}
		return new ModelAndView("service:");
	}
	
	@RequestMapping(method=RequestMethod.GET, value={"/userCreate", "/userCreate.*"})
	public ModelAndView userCreate(
			@RequestParam("username") String username, 
			@RequestParam("email") String email,
			@RequestParam("password") String password
			) {
		ModelAndView mav = new ModelAndView("service:");
		
		chadoSession.userCreate(username, email, password);
		
//		
//		try {
//			
//			byte[] defaultBytes = salt.getBytes();
//			
//			MessageDigest algorithm = MessageDigest.getInstance("MD5");
//			algorithm.reset();
//			algorithm.update(defaultBytes);
//			byte messageDigest[] = algorithm.digest();
//		            
//			StringBuffer hexString = new StringBuffer();
//			for (int i=0;i<messageDigest.length;i++) {
//				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
//			}
//			
//			System.out.println("salt "+ salt +" md5 version is " + hexString.toString());
//			
//			String sql = queryMap.getQuery("user_create");
//			logger.info(sql);
//			
//			jdbcTemplate.update(sql, new Object[] { username, email, new Integer("1") , salt, hexString.toString()  });
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error(e.getMessage());
//		}
		
		mav.addObject("model", "OK");
		return mav;
	}
	
	
	
	
}
