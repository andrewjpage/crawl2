package org.genedb.crawl.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.genedb.crawl.model.ResponseWrapper;
import org.springframework.web.servlet.View;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;

public class XMLView extends BaseView implements View{

	private String contentType = "application/xml";
	
	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public void render(Map<String, ?> map, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		response.setContentType(contentType);
		
		XmlFriendlyReplacer replacer = new XmlFriendlyReplacer("_", "_");
		XStream xstream = new XStream(new DomDriver("UTF-8", replacer));
		xstream.autodetectAnnotations(true);
		
		ResponseWrapper wrapper = new ResponseWrapper(request, getFirstValidValue(map));
		
		String xml = xstream.toXML(wrapper);
		response.getWriter().append(xml);
		
	}
	
	
	
}
