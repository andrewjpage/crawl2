package org.genedb.crawl.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.hazelcast.core.Hazelcast;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.genedb.crawl.hazelcast.HazelcastMonitor;

public class CrawlServletContextListener implements ServletContextListener {
    
    private Logger logger = Logger.getLogger(CrawlServletContextListener.class);
    
    HazelcastMonitor monitor = new HazelcastMonitor();
    
	@Override
	public void contextDestroyed(ServletContextEvent event) {
	    System.out.println("Context destroyed!");
	    monitor.clear();
		Hazelcast.shutdownAll();
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
	    logger.info("Context initialized!");
	    
	    // use this to inject the %X{webAppName} into the log4j.properties file
	    MDC.put("webAppName", event.getServletContext().getContextPath().substring(1).toUpperCase());
	    
	    logger.info("Starting crawl hazelcast monitor.");
		Hazelcast.addInstanceListener(monitor);
	}

}
