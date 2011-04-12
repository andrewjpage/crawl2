package org.genedb.crawl.view;

import java.util.Map;

import org.genedb.crawl.model.XMLResponseWrapper;

public class BaseView {
	
	boolean showParameters = false;
	
	public void setShowParameters(boolean showParameters) {
		this.showParameters = showParameters;
	}
	
	protected XMLResponseWrapper wrap(String name, Map<String, ?>map, Map<String, ?>parameters) {
		
		XMLResponseWrapper wrapper = null;
		if (showParameters) {
			wrapper = new XMLResponseWrapper(name, map, parameters);
		} else {
			wrapper = new XMLResponseWrapper(name, map, null);
		}
		
		return wrapper;
		
	}
	
}