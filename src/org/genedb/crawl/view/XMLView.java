package org.genedb.crawl.view;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;


import org.genedb.crawl.model.XMLResponseWrapper;
import org.springframework.web.servlet.View;

public class XMLView extends BaseView implements View{

	private String contentType = "application/xml";
	private JAXBContext jc;
	private Marshaller m;
	
	public XMLView() throws JAXBException {
		super();
		jc = JAXBContext.newInstance("org.genedb.crawl.model");
		m = jc.createMarshaller();
	}
	
	@Override
	public String getContentType() {
		return contentType;
	}
	
	
	@Override
	public void render(Map<String, ?> map, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		response.setContentType(contentType);
		
		XMLResponseWrapper wrapper = wrap(request.getServletPath(), map, request.getParameterMap());
		
		m.marshal(wrapper, response.getWriter());
			
	}
	
	
	
}
