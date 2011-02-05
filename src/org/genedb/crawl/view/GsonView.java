package org.genedb.crawl.view;

import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.genedb.crawl.model.XMLResponseWrapper;
import org.springframework.web.servlet.View;

import com.google.gson.Gson;

public class GsonView extends BaseView implements View {
	
	private String contentType = "application/json";
	
	private Gson gson;
	
	
	
	public GsonView() {
		super();
		gson = new Gson();
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
		
		String json = gson.toJson(wrapper);
		writer.append(json);
		
		
		if (callback!=null) {
			writer.append(" )");
		}
		
		
		
	}
	
	
	


		
	
}
