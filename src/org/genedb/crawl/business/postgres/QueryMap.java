package org.genedb.crawl.business.postgres;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;

@Scope("singleton")
public class QueryMap {
	
	private Logger logger = Logger.getLogger(QueryMap.class);
	
	private Map<String, String> queries = new HashMap<String, String>();
	
	public void setSqlFolder(File dir) throws IOException {
		if (! dir.isDirectory()) {
			throw new RuntimeException(String.format("The supplied file %s is not a folder.", dir.getPath()));
		}
		this.storeSQLs(dir);
	}
	
	/*
	 * For use in a servlet environment. 
	 */
	public void setSqlPath(String path) throws Exception {
		URL pathURL = getClass().getClassLoader().getResource(path);
		logger.debug(pathURL);
		File dir = new File(pathURL.getPath());
		if (! dir.isDirectory()) {
			throw new RuntimeException(String.format("The supplied path %s is not a folder.", path));
		}
		storeSQLs(dir);
	}
	
	private void storeSQLs(File dir) throws IOException {
		
		for (File file : dir.listFiles()) {
			logger.debug(file.getAbsolutePath());
			String fileName = file.getName();
			if (! fileName.contains(".sql")) {
				continue;
			}
			String queryName = fileName.replace(".sql", "");
			storeQuery(queryName, file);
		}
		
	}
	
	public String getQuery(String name) {
		logger.debug(name);
		return queries.get(name);
	}
	
	
	private void storeQuery(String name, File file) throws IOException {
		
		logger.debug("Storing " + name + " from " + file.getAbsolutePath());
		
		StringBuffer sb = new StringBuffer();
		BufferedReader in = new BufferedReader(new FileReader(file));
		
		String line;
		while ((line = in.readLine()) != null) {
			sb.append(line + " \n");
		}
		
		logger.debug(sb.toString());
		queries.put(name, sb.toString());
	}
	
}
