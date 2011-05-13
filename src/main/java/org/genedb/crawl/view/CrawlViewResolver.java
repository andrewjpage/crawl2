package org.genedb.crawl.view;

import org.apache.log4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class CrawlViewResolver implements ViewResolver, Ordered {

	private Logger logger = Logger.getLogger(CrawlViewResolver.class);

	private Map<String, View> viewMap;

	public void setViewMap(Map<String, View> viewMap) {
		this.viewMap = viewMap;
	}

	private int order;

	@Override
	public View resolveViewName(String viewName, Locale locale)
			throws Exception {

		logger.info(viewName);

		String[] viewSplit = viewName.split(":");

		if (viewSplit.length < 1) {
			return null;
		}

		String prefix = viewSplit[0];
		logger.info(String.format("prefix: '%s'", prefix));

		String extensionViewName = "";
		if (viewSplit.length == 2) {
			extensionViewName = viewSplit[1];
		} else {
			extensionViewName = getExtension();
		}

		View view = viewMap.get(extensionViewName);
		logger.info(String.format("Returning view of type '%s'",
				view.getClass()));
		return view;
	}

	/**
	 * Generates and appropriate extension based on the existing HTTP request.
	 * 
	 * @param request
	 * @return
	 */
	private String getExtension() {

		RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
		Assert.isInstanceOf(ServletRequestAttributes.class, attrs);
		ServletRequestAttributes servletAttrs = (ServletRequestAttributes) attrs;
		HttpServletRequest request = servletAttrs.getRequest();

		String uri = request.getRequestURI();
		logger.debug("parsing uri: " + uri);
		String extension = "xml";
		if (uri.endsWith(".json")) {
			extension = "json";
		}
		return extension;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int getOrder() {
		return order;
	}

}
