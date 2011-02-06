package org.genedb.crawl.search.index;

import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

public class JsonIzer {
	
	private ObjectMapper mapper;
	
	public JsonIzer () {
		mapper = new ObjectMapper();
	    AnnotationIntrospector jaxbIntrospector = new JaxbAnnotationIntrospector();
	    
	    AnnotationIntrospector jacksonIntrospector = new JacksonAnnotationIntrospector();
	    AnnotationIntrospector introspector = AnnotationIntrospector.pair(jaxbIntrospector, jacksonIntrospector);
	    
	    mapper.getDeserializationConfig().setAnnotationIntrospector(introspector);
	    mapper.getSerializationConfig().setAnnotationIntrospector(introspector);
	}
	
	public Object fromJson(String string, Class cls) throws JsonParseException, JsonMappingException, IOException {
		
		Object obj = mapper.readValue(string, cls);
		return obj;
		
	}
	
	public String toJson(Object object) throws IOException {
		
		JsonFactory jFact = new JsonFactory();
		
		StringWriter writer = new StringWriter();
		
		JsonGenerator jGen = jFact.createJsonGenerator(writer);
		
		mapper.writeValue(jGen, object);
		
		
		return writer.toString();
	}
}
