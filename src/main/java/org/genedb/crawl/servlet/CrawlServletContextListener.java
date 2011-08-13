package org.genedb.crawl.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.hazelcast.core.Hazelcast;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

public class CrawlServletContextListener implements ServletContextListener {
    
    private Logger logger = Logger.getLogger(CrawlServletContextListener.class);
    
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		Hazelcast.shutdownAll();
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
	    //System.out.println("!Starting up context listener.");
	    logger.info("Starting up context listener.");
	    logger.info("Reconfiguring log4j appender...");
	    // use this to inject the %X{webAppName} into the log4j.properties file
	    MDC.put("webAppName", event.getServletContext().getContextPath().substring(1).toUpperCase());
	    
	    logger.info("Starting crawl hazelcast monitor.");
		Hazelcast.addInstanceListener(new HazelcastMonitor());
	}

}
