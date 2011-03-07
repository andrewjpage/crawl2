package org.genedb.crawl.elasticsearch.mappers;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.xcontent.FieldQueryBuilder;
import org.elasticsearch.index.query.xcontent.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.genedb.crawl.elasticsearch.Connection;
import org.genedb.crawl.elasticsearch.json.JsonIzer;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.LocatedFeature;
import org.springframework.beans.factory.annotation.Autowired;

public class ElasticSearchBaseMapper {
	
	private Logger logger = Logger.getLogger(ElasticSearchBaseMapper.class);
	protected JsonIzer jsonIzer = JsonIzer.getJsonIzer();
	
	private static final Field[] featureFields = Feature.class.getDeclaredFields();
	
	String index = "features";
	String type = "Feature";
	
	
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
	
	protected List<Feature> fetchAndCopy(List<String> features, String[] fields) {
		List<Feature> featureResults = new ArrayList<Feature>();
		for (String uniqueName : features) {
			String json = getFromElastic(uniqueName, fields);
			Feature feature = getFeatureFromJson(json);
			Feature featureToAdd = copy(feature, fields);
			if (featureToAdd != null) {
				featureResults.add(featureToAdd);
			}
		}
		return featureResults;
	}
	
	protected String getFromElastic(String uniqueName, String[] fields) {
		return connection.getClient().prepareGet(index, type, uniqueName).execute().actionGet().sourceAsString();
		
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
	
	
	protected <T extends Object> T getFirstMatch(String indexName, String fieldName, String value, Class<T> cls) {
		
		FieldQueryBuilder regionQuery = 
		QueryBuilders.fieldQuery(fieldName, value);
	
		SearchResponse response = connection.getClient().prepareSearch(indexName)
			.setQuery(regionQuery)
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
	
	protected <T extends Object> List<T> getAllMatches(String indexName, String fieldName, String value, Class<T> cls) {
		
		FieldQueryBuilder regionQuery = 
		QueryBuilders.fieldQuery(fieldName, value);
		
		SearchResponse response = connection.getClient().prepareSearch(indexName)
			.setQuery(regionQuery)
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
	
}
