package org.genedb.crawl.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.hazelcast.core.Hazelcast;

public class CrawlServletContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		//Hazelcast.shutdownAll();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// nothing to except relax and enjoy the music
	}

}
