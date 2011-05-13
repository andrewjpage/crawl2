package org.genedb.crawl.view;

import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.genedb.crawl.json.JsonIzer;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.View;

public class JacksonView extends BaseView implements View {
	
	private static final Logger logger = Logger.getLogger(JacksonView.class);
	
	private String contentType = "application/json";
	protected JsonIzer jsonIzer = JsonIzer.getJsonIzer();
	
	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public void render(Map<String, ?> map, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		response.setContentType(contentType);
		
		Writer writer = response.getWriter();
		String callback = request.getParameter("callback");
		
		if (callback!=null) {
			writer.append(callback + "( ");
		}
		
		for (Entry<String, ?> entry : map.entrySet()) {
			
			logger.debug(entry.getKey());
			//logger.debug(entry.getValue());
			
			Object value = entry.getValue();
			if (value instanceof BeanPropertyBindingResult) {
				continue;
			}
			
			jsonIzer.toJson(value, writer);
		}
		
		
		
		if (callback!=null) {
			writer.append(" )");
		}
		
	}
	
	
	


		
	
}
