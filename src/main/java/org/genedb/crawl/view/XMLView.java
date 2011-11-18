package org.genedb.crawl.view;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;


import org.apache.log4j.Logger;
import org.genedb.crawl.model.XMLResponseWrapper;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.View;

public class XMLView extends BaseView implements View{
	
	private static final Logger logger = Logger.getLogger(XMLView.class);

	private String contentType = "application/xml";
	private JAXBContext jaxbContext;
	
	
	@Override
	public String getContentType() {
		return contentType;
	}
	
	public void setJaxbContext(JAXBContext jaxbContext) throws JAXBException {
	    this.jaxbContext = jaxbContext;
	    
	}
	
	
	@Override
	public void render(Map<String, ?> map, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		response.setContentType(contentType);
		
		logger.debug(String.format("rendering %s", contentType));
		
		XMLResponseWrapper wrapper = new XMLResponseWrapper();
		
		if (showParameters) {
			wrapper.setParameters (request.getParameterMap());
		}
		
		for (Entry<String, ?> entry : map.entrySet()) {
			
			logger.debug(entry.getKey());
			//logger.debug(entry.getValue());
			
			Object value = entry.getValue();
			
			if (value instanceof BeanPropertyBindingResult) {
				continue;
			}
			
			if (value instanceof List) {
				wrapper.results = (List) value;
			} else {
				wrapper.results = new ArrayList<Object>();
				wrapper.results.add(value);
			}
			
		}
		
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.marshal(wrapper, response.getWriter());
			
	}
	
	
	
}
