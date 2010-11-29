package org.genedb.crawl.view;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.validation.BeanPropertyBindingResult;

public class BaseView {

	public BaseView() {
		super();
	}

	protected Object getFirstValidValue(Map<String, ?>map) {
		
		for (Entry<String, ?> e : map.entrySet()) {
			Object value = e.getValue();
			
			if (value instanceof BeanPropertyBindingResult)	 
	    		continue;
			
			return value;
		}
		
		return null;
		
	}

}