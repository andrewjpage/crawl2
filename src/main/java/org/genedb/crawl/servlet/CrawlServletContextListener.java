package org.genedb.crawl.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.hazelcast.core.Hazelcast;

import org.apache.log4j.MDC;

public class CrawlServletContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		Hazelcast.shutdownAll();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
	    // use this to inject the %X{webAppName} into the log4j.properties file
	    MDC.put("webAppName", arg0.getServletContext().getContextPath().substring(1).toUpperCase());
		Hazelcast.addInstanceListener(new HazelcastMonitor());
	}

}
