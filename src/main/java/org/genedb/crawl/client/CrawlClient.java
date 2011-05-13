package org.genedb.crawl.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

// unused, but we reference the model package so that any clients built against this class have access to them
import org.genedb.crawl.model.*;

import org.apache.log4j.Logger;
import org.codehaus.jackson.type.TypeReference;
import org.genedb.crawl.json.JsonIzer;

@SuppressWarnings("unused")
public class CrawlClient {

	public String host;

	public CrawlClient(String host) {
		this.host = host;
	}

	private static final String sep = "/";
	private static final String extension = ".json";
	private static final String encoding = "UTF-8";
	private static final JsonIzer jsonIzer = JsonIzer.getJsonIzer();

	private static Logger logger = Logger.getLogger(CrawlClient.class);

	public <T extends Object> T request(String resource, String method,
			Map<String, String[]> parameters, TypeReference<T> type)
			throws IOException {

		URLConnection conn = null;
		BufferedReader rd = null;
		StringBuffer result = null;

		String urlString = host + sep + resource + sep + method + extension;

		StringBuffer encodedParameters = new StringBuffer();

		if (parameters != null) {
			String amp = "";
			for (Entry<String, String[]> parameter : parameters.entrySet()) {
				String key = parameter.getKey();
				for (String value : parameter.getValue()) {
					encodedParameters.append(amp + key + "="
							+ URLEncoder.encode(value, encoding));
					amp = "&";
				}
			}
			urlString += "?" + encodedParameters;
		}

		URL url = new URL(urlString);

		logger.info(url);

		try {

			conn = url.openConnection();
			conn.setUseCaches(false);
			result = new StringBuffer();

			rd = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

		} finally {
			if (rd != null) {
				rd.close();
			}

		}

		@SuppressWarnings("unchecked")
		T t = (T) jsonIzer.fromJson(result.toString(), type);

		return t;

	}

}
