package org.genedb.crawl.model;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * A model class designed for returning web service views. It defines 4 basic kinds of 
 * property : a Result, an Error, a Service and a ParameterList, also model classes.  
 * 
 * */

@XmlRootElement(name="response") // the root element's name is set to response for JAXB XML marshaling, but is ignored by Jackson JSON. 
public class XMLResponseWrapper {
	
	public XMLResponseWrapper () {
		// deliberately empty constructor (for JAXB)
	}
	
	public XMLResponseWrapper (String name, Map<String, ?>map, Map<String, ?>parameters) {
		
		for (Entry<String, ?> entry : map.entrySet()) {
			
			Object value = entry.getValue();
			
			if (value instanceof ResultsSAM) {
				response = (ResultsSAM) value;
				response.name = name;
			} else if (value instanceof ResultsRegions) {
				response = (ResultsRegions) value;
				response.name = name;
			}
			else if (value instanceof Results) {
				response = (Results) value;
				response.name = name;
			}
			else if (value instanceof CrawlError) {
				error = (CrawlError) value;
			}
			else if (value instanceof Service) {
				service = (Service) value;
			}
			
		}
		
		if (parameters != null) {
			setParameters(parameters);
		}
		
	}
	
	// Explicitly define all the different kinds of Results subclass, so that JAXB maps them properly.
	@XmlElements({
        @XmlElement(name="results", type=Results.class),
        @XmlElement(name="results", type=ResultsRegions.class),
        @XmlElement(name="results", type=ResultsSAM.class)
	})
	// This property has to be called "response", so that the Jackson mapper writes this element out with that name.  
	public Results response;
	
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
			
//			if (value instanceof List) {
//				List<?> list = (List<?>) value;
//				
//				for (Object v : list) {
//					pList.values.add(v.toString());
//				}
//			} else 
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



