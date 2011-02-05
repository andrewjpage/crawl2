package org.genedb.crawl.search.index;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;

public class GoGetter {
	
	String goFileURL = "http://www.geneontology.org/ontology/obo_format_1_2/gene_ontology_ext.obo";
	
	public class Go {
		String id;
		String namespace;
		String name;
	}
	
	public Hashtable <String, Go> gos = new Hashtable<String, Go>();
	
	public void GoGetter() throws IOException {
		HttpURLConnection connection = null;
		BufferedReader reader = null;
		
		try {
			
			URL u=new URL(goFileURL);
	        
	        connection = (HttpURLConnection) u.openConnection();

	        connection.setRequestMethod("GET");
	        connection.setDoOutput(true);
	        connection.setReadTimeout(10000);
	                  
	        connection.connect();
	        
	        reader  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        
	        String line;
	        Go go = null;
	        
	        while ((line = reader.readLine()) != null)
	        {
	            if (line.startsWith("id:")) {
	            	
	            	go = new Go();
	            	go.id = line.replace("id: ", "");
	            	gos.put(go.id, go);
	            	
	            } else if (go != null) {
	            	
	            	
	            	if (line.startsWith("name:")) {
	            		go.name = line.replace("name: ", "");
	            	} else if (line.startsWith("namespace:")) {
	            		go.namespace = line.replace("namespace: ", "");
	            	}
	            	
	            	
	            }
	        	
	        	
	        	
	        }
	        
		} finally {
			reader = null;
			connection.disconnect();
			connection = null;
		}
		
		
		
	}
	
	public Go get(String goID) {
		return gos.get(goID);
	}
	
}
