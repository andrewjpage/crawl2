package org.genedb.crawl.model;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * A model class designed for returning web service views. It defines 4 basic kinds of 
 * property : a Result, an Error, a Service and a ParameterList, also model classes.  
 * 
 * */

@XmlRootElement(name="response") // the root element's name is set to response for JAXB XML marshaling, but is ignored by Jackson JSON. 
public class XMLResponseWrapper {
	
	@XmlElementWrapper(name="results")
	@XmlElement(name="result")
	public List<Object> results;
	
	@XmlElement(name="error")
	public CrawlError error;
	
	@XmlElement(name="service")
	public Service service;
	
	@XmlElementWrapper(name="parameters")
	@XmlElement(name="parameter", type=ParameterList.class)
	public List<ParameterList> parameters;
	
	public void setParameters(Map<String, ?> map) {
		
		parameters = new ArrayList<ParameterList>();
		
		if (map == null) {
			return;
		}
		
		for (Entry<String, ?> entry : map.entrySet()) {
			ParameterList pList = new ParameterList();
			pList.name = entry.getKey();
			
			Object value = entry.getValue();
			
			if (value instanceof String[]) {
				
				String[] strings = (String[]) value;
				
				for (Object v : strings) {
					pList.values.add(v.toString());
				}
				
			}
			else {
				pList.values.add(value.toString());
			}
			
			parameters.add(pList);
			
		}
		
		
	}
	
	static class ParameterList {
		
		@XmlAttribute
		public String name;
		
		@XmlElementWrapper(name="values")
		@XmlElement(name="value")
		public List<String> values = new ArrayList<String>();
	}
	
	
}



