package org.genedb.crawl.elasticsearch.mappers;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.action.search.SearchRequestBuilder;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.json.JsonXContent;

import org.elasticsearch.index.query.BoolQueryBuilder;


import org.elasticsearch.index.query.FieldQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.genedb.crawl.elasticsearch.Connection;
import org.genedb.crawl.json.JsonIzer;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.LocatedFeature;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class ElasticSearchBaseMapper {
	
	private Logger logger = Logger.getLogger(ElasticSearchBaseMapper.class);
	protected JsonIzer jsonIzer = new JsonIzer();
	
	private static final Field[] featureFields = Feature.class.getDeclaredFields();
	
	//String index = "features";
	//String type = "Feature";
	
//	public static String getIndex() {
//		return "index";
//	}
//	public static String getType() {
//		return "type";
//	}
	
	@Autowired
	Connection connection;
	
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	
	protected LocatedFeature getFeatureFromJson(String json) {
		
		if (json == null) {
			return null;
		}
		
		try {
			
			LocatedFeature feature = (LocatedFeature) jsonIzer.fromJson(json, LocatedFeature.class);
			return feature;
						
		} catch (JsonParseException e) {
			logger.error("Could not parse the JSON!");
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Could not read the JSON!");
			e.printStackTrace();
		} 
		return null;
	}
	
	protected Feature copy(Feature feature, String[] fields)  {
		Feature copy = new Feature();
		
		if (feature == null) {
			return null;
		}
		
		for (String field : fields) {
			for (Field featureField : featureFields) {
				if (featureField.getName().equals(field)) {
					try {
						featureField.set(copy, featureField.get(feature));
					} catch (Exception e) {
						logger.error(String.format("could not copy field", field));
						e.printStackTrace();
					} 
				}
			}
		}
		
		return copy;
	}
	
	protected List<Feature> fetchAndCopy(String index, String type, List<String> features, String[] fields) {
		List<Feature> featureResults = new ArrayList<Feature>();
		for (String uniqueName : features) {
			String json = getFromElastic(index, type, uniqueName, fields);
			Feature feature = getFeatureFromJson(json);
			Feature featureToAdd = copy(feature, fields);
			if (featureToAdd != null) {
				featureResults.add(featureToAdd);
			}
		}
		return featureResults;
	}
	
	protected String getFromElastic(String index, String type, String uniqueName) {
		return connection.getClient().prepareGet(index,type, uniqueName).execute().actionGet().sourceAsString();
	}
	
	protected String getFromElastic(String index, String type, String uniqueName, String[] fields) {
		return connection.getClient().prepareGet(index,type, uniqueName).execute().actionGet().sourceAsString();
		
//		logger.debug("Searching for uniqueName " + uniqueName);
//		
//		SearchRequestBuilder srb = connection.getClient().prepareSearch(index)
//			.setQuery( QueryBuilders.fieldQuery("uniqueName", uniqueName));
//		//	.setQuery( QueryBuilders.termQuery("uniqueName", uniqueName));
////			.setFrom(0)
////			.setSize(1);
//		
////		for (String field : fields) {
////			String fieldName = "_source." + field;
////			logger.debug("Field " + fieldName);
////			srb.addField(fieldName);
////		}
//		
//		// srb.addField("_source.coordinates");
//		
//		SearchResponse response = srb.execute().actionGet();
//		
//		logger.debug(response.getHits().totalHits());
//		
//		if (response.getHits().totalHits() == 1) {
//			logger.debug("returning ");
//			
//			SearchHit hit = response.hits().getAt(0);
//			
//			logger.debug(hit.sourceAsString());
//			
//			logger.debug(hit.fields());
//			
//			return response.hits().getAt(0).sourceAsString();
//		}
//		
//		return null;
		
		
	}
	

	
	protected <T extends Feature> T copy(Feature feature, String[] fields, Class<T> cls) throws InstantiationException, IllegalAccessException  {
		
		Field[] tFields = cls.getFields();
		
		T copy = (T) cls.newInstance();
		
		if (feature == null) {
			return null;
		}
		
		for (String field : fields) {
			for (Field featureField : tFields) {
				//logger.debug(String.format("%s == %s", featureField.getName(), field));
				if (featureField.getName().equals(field)) {
					try {
						featureField.set(copy, featureField.get(feature));
					} catch (Exception e) {
						logger.error(String.format("could not copy field", field));
						e.printStackTrace();
					} 
				}
			}
		}
		
		return copy;
	}
	
	protected <T extends Object> T getFirstMatch(String indexName, String typeName, Map<String, String> fieldNamesAndValues, Class<T> cls) {
	    
		logger.info(String.format("Fetching index %s, type %s, field %s, value %s, casting to %s.", indexName, typeName, fieldNamesAndValues, cls.getName()));
		
		BoolQueryBuilder booleanQuery = QueryBuilders.boolQuery();
		for (String key : fieldNamesAndValues.keySet())  {
		    String value = fieldNamesAndValues.get(key);
		    FieldQueryBuilder fieldQuery = QueryBuilders.fieldQuery(key, value);
		    booleanQuery.must(fieldQuery);
		}
		
		SearchResponse response = connection.getClient()
			.prepareSearch(indexName)
			.setTypes(typeName)
			.setQuery(booleanQuery)
			.execute()
			.actionGet();
		
		return getFirstMatch( response, cls);
		
	}
	
	protected <T extends Object> T getFirstMatch(String indexName, String typeName, String fieldName, String value, Class<T> cls) {
		
		logger.info(String.format("Fetching index %s, type %s, field %s, value %s, casting to %s.", indexName, typeName, fieldName, value, cls.getName()));
		
		FieldQueryBuilder fieldQuery = QueryBuilders.fieldQuery(fieldName, value);
	
		SearchResponse response = connection.getClient()
			.prepareSearch(indexName)
			.setTypes(typeName)
			.setQuery(fieldQuery)
			.execute()
			.actionGet();
		
		return getFirstMatch( response, cls);
		
	}
	
	protected <T extends Object> T getFirstMatch(SearchResponse response, Class<T> cls) {
		
			
		for (SearchHit hit : response.getHits()) {
		
			String source = hit.sourceAsString();
			
			try {
				
				T object = (T) jsonIzer.fromJson(source, cls);
				return object;
				
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
				return null;
			}
			
		}
		
		return null;
	}
	
	protected <T extends Object> List<T> getAllMatches(String indexName, String fieldName, QueryBuilder query, Class<T> cls) {
	    
	    SearchRequestBuilder srb = connection.getClient().prepareSearch(indexName).setQuery(query);
	    
	    logger.info(toString(srb.internalBuilder()));
	    
	    SearchResponse response = srb.execute().actionGet();
	        
	        return getAllMatches(response, cls);
	}
	
	protected <T extends Object> List<T> getAllMatches(String indexName, String fieldName, String value, Class<T> cls) {
		
		FieldQueryBuilder regionQuery = 
		QueryBuilders.fieldQuery(fieldName, value);
		
		
		
		SearchResponse response = connection.getClient().prepareSearch(indexName)
			.setQuery(regionQuery)
			.setSize(Integer.MAX_VALUE)
			.execute()
			.actionGet();
		
		return getAllMatches(response, cls);
		
	}
	
	protected <T extends Object> List<T> getAllMatches(SearchResponse response, Class<T> cls) {
		
		List<T> list = new ArrayList<T>();
		
		for (SearchHit hit : response.getHits()) {
		
			String source = hit.sourceAsString();
			
			try {
				
				T object = (T) jsonIzer.fromJson(source, cls);
				list.add(object);
				
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
			
		}
		
		return list;
	}
	
	
	public void createOrUpdate(String index, String type, String key, Object obj) {
		
		try {
			
			String json = jsonIzer.toJson(obj);
			logger.debug("Source:");
			logger.debug(json);
			
			logger.info(String.format("Storing %s in index %s and type %s", key, index, type));
			
			connection
				.getClient()
				.prepareIndex(index, type, key)
				.setSource(json)
				.execute()
				.actionGet();
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
//	public void waitForYellowOrGreenStatus() {
//		ClusterHealthRequest clusterHealth = new ClusterHealthRequest();
//		ClusterHealthResponse response;
//		
//		boolean ok = false;
//		logger.info("Waiting...");
//		
//		while (! ok) {
//			response = connection.getClient().admin().cluster().health(clusterHealth).actionGet();
//			ClusterHealthStatus status = response.getStatus();
//			
//			if (status.equals(ClusterHealthStatus.GREEN) || status.equals(ClusterHealthStatus.YELLOW)) {
//				logger.info(status);
//				
//				ok = true;
//			}
//			
//			logger.info(status);
//			
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//	}
	
	
	public static String toString(ToXContent tmp) {
        try {
            return
            tmp.toXContent(JsonXContent.unCachedContentBuilder(),
                    ToXContent.EMPTY_PARAMS).
                    prettyPrint().
                    string();
        } catch (Exception ex) {
            return "<ERROR:" + ex.getMessage() + ">";
        }
    }
	
	public void waitForStatus(EnumSet<ClusterHealthStatus> acceptableStatuses) {
		ClusterHealthRequest clusterHealth = new ClusterHealthRequest();
		ClusterHealthResponse response;
		
		boolean ok = false;
		logger.info("Waiting...");
		
		while (! ok) {
			response = connection.getClient().admin().cluster().health(clusterHealth).actionGet();
			ClusterHealthStatus status = response.getStatus();
			
			for (ClusterHealthStatus acceptableStatus : acceptableStatuses) {
				if (acceptableStatus.equals(status)) {
					ok = true;
				}
			}
			
			logger.info(status);
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	protected static final String LUCENE_ESCAPE_CHARS = "[\\\\+\\-\\!\\(\\)\\:\\^\\]\\{\\}\\~\\*\\?]";
    protected static final Pattern LUCENE_PATTERN = Pattern.compile(LUCENE_ESCAPE_CHARS);
    protected static final String REPLACEMENT_STRING = "\\\\$0";
    
    protected String escape(String value) {
        String escaped = LUCENE_PATTERN.matcher(value).replaceAll(REPLACEMENT_STRING);
        logger.info(String.format("%s ... %s", value, escaped));
        return escaped;
    }
	
	
}
