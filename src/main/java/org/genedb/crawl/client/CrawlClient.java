package org.genedb.crawl.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

// unused, but we reference the model package so that any clients built against this class have access to them
import org.genedb.crawl.CrawlErrorType;
import org.genedb.crawl.CrawlException;
import org.genedb.crawl.model.*;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;
import org.genedb.crawl.json.JsonIzer;

@SuppressWarnings("unused")
public class CrawlClient {

	public String baseURL;

	public CrawlClient(String baseURL) {
		this.baseURL = baseURL;
	}

	private static final String sep = "/";
	private static final String extension = ".json";
	private static final String encoding = "UTF-8";
	private static final JsonIzer jsonIzer = new JsonIzer();

	private static Logger logger = Logger.getLogger(CrawlClient.class);
	
	
	/**
	 * 
	 * For returning an object of a known type, T.
	 * 
	 * @param cls
	 * @param resource
	 * @param method
	 * @param parameters
	 * @return
	 * @throws IOException
	 * @throws CrawlException
	 */
	public <T extends Object> T request(
	        Class<T> cls,
	        String resource, 
            String method,
            Map<String, String[]> parameters) throws IOException, CrawlException {
	    
	    String result = this.result(resource, method, parameters);
	    //logger.info(result);
	    
	    @SuppressWarnings("unchecked")
        T object = (T) jsonIzer.fromJson(result, cls);
	    
	    return object;
	    
	}
	
	
	/**
	 * For returning a collection (type T) of objects (type U).
	 * 
	 * @param listClass
	 * @param listItemClass
	 * @param resource
	 * @param method
	 * @param parameters
	 * @return
	 * @throws IOException
	 * @throws CrawlException
	 */
    public <T extends Collection> T request(
            Class<T> listClass,
            Class<?> listItemClass,
            String resource, 
            String method,
            Map<String, String[]> parameters) throws IOException, CrawlException {
        
        String result = this.result(resource, method, parameters);
        //logger.info(result);
        
        JavaType type = TypeFactory.collectionType(listClass, listItemClass);
        
        @SuppressWarnings("unchecked")
        T object = (T) jsonIzer.fromJson(result, type);
        
        return object;
        
    }
    
    /**
     * 
     * For returning a collection (type T), using a JavaType.
     * 
     * @param type
     * @param resource
     * @param method
     * @param parameters
     * @return
     * @throws IOException
     * @throws CrawlException
     */
	public <T extends Object> T request(
            JavaType type,
            String resource, 
            String method,
            Map<String, String[]> parameters) throws IOException, CrawlException {
        
        String result = this.result(resource, method, parameters);
        logger.info(result);
        
        @SuppressWarnings("unchecked")
        T object = (T) jsonIzer.fromJson(result, type);
        
        return object;
        
    }
	
	/**
	 * For returning the json response as a string.
	 * 
	 * @param resource
	 * @param method
	 * @param parameters
	 * @return
	 * @throws MalformedURLException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws CrawlException
	 */
	public String result (
	        String resource, 
            String method,
            Map<String, String[]> parameters) throws MalformedURLException, UnsupportedEncodingException, IOException, CrawlException {
	    
	    HttpURLConnection conn = null;
        BufferedReader rd = null;
        StringBuffer result = new StringBuffer();

        String urlString = baseURL + sep + resource + sep + method + extension;

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
            
            conn =  (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            
            rd = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            
            return result.toString();
            
        } catch (IOException e) {
            
            try {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            } catch (NullPointerException npe) {
                throw (e);
            }
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            
            CrawlError error = (CrawlError) jsonIzer.fromJson(result.toString(), CrawlError.class);
            logger.error(error.message);
            throw new CrawlException(error);
         
            
        } finally {
            
            rd = null;
            result = null;
            
            conn.disconnect();
            conn = null;
        }
        
	}

}
