package org.genedb.crawl.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.genedb.crawl.model.ResponseWrapper;
import org.springframework.web.servlet.View;

import com.google.gson.Gson;


public class GsonView implements View {
	
	private Logger logger = Logger.getLogger(GsonView.class);
	
	private String contentType = "application/json";
	
	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public void render(Map<String, ?> map, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		response.setContentType(contentType);
		
		Gson gson = new Gson();
		
		ResponseWrapper wrapper = new ResponseWrapper(request, map.get("model"));
		
		String json = gson.toJson(wrapper);
		
		String callback = request.getParameter("callback");
		
		if (callback!=null) {
			json = callback + "( " + json + " )";
		}
		
		response.getWriter().append(json);
		
	}
	


		
	
}
