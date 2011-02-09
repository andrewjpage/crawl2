package org.genedb.crawl.elasticsearch.mappers;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.samtools.SAMRecord;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.action.get.GetRequestBuilder;
import org.elasticsearch.client.action.search.SearchRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.xcontent.QueryBuilders;
import org.elasticsearch.index.query.xcontent.XContentQueryBuilder;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;
import org.genedb.crawl.CrawlErrorType;
import org.genedb.crawl.CrawlException;
import org.genedb.crawl.elasticsearch.Connection;
import org.genedb.crawl.elasticsearch.index.JsonIzer;
import org.genedb.crawl.model.BlastPair;
import org.genedb.crawl.model.CrawlError;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.HierarchyGeneFetchResult;
import org.genedb.crawl.model.HierarchyRelation;
import org.genedb.crawl.model.LocatedFeature;
import org.gmod.cat.FeaturesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ElasticSearchFeaturesMapper extends ElasticSearchBaseMapper implements FeaturesMapper {
	
	private Logger logger = Logger.getLogger(ElasticSearchFeaturesMapper.class);
	
	String index = "features";
	String type = "Feature";
	
	JsonIzer jsonIzer = new JsonIzer();

	
	@Override
	public List<HierarchyGeneFetchResult> getGeneForFeature(
			List<String> features) {
		
		
		//XContentQueryBuilder getFeatureQuery = QueryBuilders.termQuery("uniqueName", feature);
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<HierarchyRelation> getRelationshipsParents(String feature,
			List<Integer> relationships) {
		
		//XContentQueryBuilder getFeatureQuery = QueryBuilders.termQuery("uniqueName", feature);
		//XContentQueryBuilder builder = QueryBuilders.hasChildQuery(type, getFeatureQuery);
		
		connection.getClient().prepareSearch("features");
		
		
		return null;
	}

	@Override
	public List<HierarchyRelation> getRelationshipsChildren(String feature,
			List<Integer> relationships) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Feature> coordinates(List<String> features, String region) {
		return fetchAndCopy(features,  new String[]{"uniqueName", "coordinates"});
	}

	@Override
	public List<Feature> synonyms(List<String> features, List<String> types) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

	@Override
	public List<Feature> properties(List<String> features) {
		return fetchAndCopy(features,  new String[]{"uniqueName", "properties"});
	}

	@Override
	public List<Feature> pubs(List<String> features) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Feature> dbxrefs(List<String> features) {
		return fetchAndCopy(features,  new String[]{"uniqueName", "dbxrefs"});
	}

	@Override
	public List<Feature> terms(List<String> features, List<String> cvs) {
		return fetchAndCopy(features,  new String[]{"uniqueName", "terms"});
	}

	@Override
	public List<Feature> orthologues(List<String> features) {
		return fetchAndCopy(features,  new String[]{"uniqueName", "orthologues"});
	}

	@Override
	public List<Feature> clusters(List<String> features) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Feature> synonymsLike(String term, Boolean regex, String region) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Feature> featuresLike(String term, Boolean regex, String region) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Feature> withproperty(String value, Boolean regex,
			String region, String type) {
		
		//SearchRequestBuilder srb = connection.getClient().prepareSearch(index)
		//.setQuery( QueryBuilders.fieldQuery("uniqueName", uniqueName));
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Feature> withterm(String cvterm, String cv, Boolean regex,
			String region) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Feature> timelastmodified(Date date, Integer organism_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BlastPair> blastPairs(String f1, int start1, int end1,
			String f2, int start2, int end2, Integer length, Integer normscore) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	private static final Field[] featureFields = Feature.class.getDeclaredFields();
	
	private LocatedFeature getFeatureFromJson(String json) {
		
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
	
	private Feature copy(Feature feature, String[] fields)  {
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
	
	private List<Feature> fetchAndCopy(List<String> features, String[] fields) {
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
	
	private String getFromElastic(String uniqueName, String[] fields) {
		//return client.prepareGet(index, type, uniqueName).execute().actionGet().sourceAsString();
		
		logger.debug("Searching for uniqueName " + uniqueName);
		
		SearchRequestBuilder srb = connection.getClient().prepareSearch(index)
			.setQuery( QueryBuilders.fieldQuery("uniqueName", uniqueName));
		//	.setQuery( QueryBuilders.termQuery("uniqueName", uniqueName));
//			.setFrom(0)
//			.setSize(1);
		
//		for (String field : fields) {
//			String fieldName = "_source." + field;
//			logger.debug("Field " + fieldName);
//			srb.addField(fieldName);
//		}
		
		// srb.addField("_source.coordinates");
		
		SearchResponse response = srb.execute().actionGet();
		
		logger.debug(response.getHits().totalHits());
		
		if (response.getHits().totalHits() == 1) {
			logger.debug("returning ");
			
			SearchHit hit = response.hits().getAt(0);
			
			logger.debug(hit.sourceAsString());
			
			logger.debug(hit.fields());
			
			return response.hits().getAt(0).sourceAsString();
		}
		
		return null;
		
		
	}
	
	
	
}
