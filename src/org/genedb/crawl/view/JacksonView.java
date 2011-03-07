package org.genedb.crawl.view;

import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.genedb.crawl.elasticsearch.json.JsonIzer;
import org.genedb.crawl.model.XMLResponseWrapper;
import org.springframework.web.servlet.View;

public class JacksonView extends BaseView implements View {
	
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
		
		XMLResponseWrapper wrapper = wrap(request.getServletPath(), map, request.getParameterMap());
		
		Writer writer = response.getWriter();
		String callback = request.getParameter("callback");
		
		if (callback!=null) {
			writer.append(callback + "( ");
		}
		
		jsonIzer.toJson(wrapper, writer);
		
		if (callback!=null) {
			writer.append(" )");
		}
		
	}
	
	
	


		
	
}
