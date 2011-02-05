package org.genedb.crawl.view;

import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.genedb.crawl.model.XMLResponseWrapper;
import org.springframework.web.servlet.View;



import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;


public class JacksonView extends BaseView implements View {
	
	private String contentType = "application/json";
	
	private ObjectMapper mapper;
	
	public JacksonView() {
		super();
		
		mapper = new ObjectMapper();
	    AnnotationIntrospector jaxbIntrospector = new JaxbAnnotationIntrospector();
	    
	    AnnotationIntrospector jacksonIntrospector = new JacksonAnnotationIntrospector();
	    AnnotationIntrospector introspector = AnnotationIntrospector.pair(jaxbIntrospector, jacksonIntrospector);
	    
	    mapper.getDeserializationConfig().setAnnotationIntrospector(introspector);
	    mapper.getSerializationConfig().setAnnotationIntrospector(introspector);
	    mapper.getSerializationConfig().setSerializationInclusion(Inclusion.NON_DEFAULT);
	    
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
		
		String callback = request.getParameter("callback");
		
		Writer writer = response.getWriter();
		
		if (callback!=null) {
			writer.append(callback + "( ");
		}
		
		JsonFactory jFact = new JsonFactory();
		
		JsonGenerator jGen = jFact.createJsonGenerator(writer);
		
	    
	    mapper.writeValue(jGen, wrapper);

		
		if (callback!=null) {
			writer.append(" )");
		}
		
		
		
	}
	
	
	


		
	
}
