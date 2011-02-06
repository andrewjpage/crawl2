package org.genedb.crawl.search.mappers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.action.get.GetRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.genedb.crawl.model.BlastPair;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.HierarchyGeneFetchResult;
import org.genedb.crawl.model.HierarchyRelation;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.search.index.JsonIzer;
import org.gmod.cat.FeaturesMapper;

public class ElasticSearchFeaturesMapper implements FeaturesMapper {
	
	private Logger logger = Logger.getLogger(ElasticSearchFeaturesMapper.class);
	
	Client client = new TransportClient().addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
	String index = "features";
	String type = "Feature";
	
	JsonIzer jsonIzer = new JsonIzer();
	
	private LocatedFeature getFeatureFromJson(GetResponse response) {
		try {
			LocatedFeature feature = (LocatedFeature) jsonIzer.fromJson(response.sourceAsString(), LocatedFeature.class);
			return feature;
						
		} catch (JsonParseException e) {
			logger.error("Could not parse the JSON!");
			e.printStackTrace();
		} catch (JsonMappingException e) {
			logger.error("Could not map the JSON!");
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Could not read the JSON!");
			e.printStackTrace();
		} 
		return null;
	}
	
	

	@Override
	public List<HierarchyGeneFetchResult> getGeneForFeature(
			List<String> features) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<HierarchyRelation> getRelationshipsParents(String feature,
			List<Integer> relationships) {
		// TODO Auto-generated method stub
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
		
		List<Feature> featureResults = new ArrayList<Feature>();
		
		for (String uniqueName : features) {
			
			GetRequestBuilder grb = client.prepareGet(index, type, uniqueName);
			//grb.setFields(new String[] {"uniqueName", "properties"});
			
			GetResponse response = grb.execute().actionGet();
			
			
			logger.debug(response);
			logger.debug(response.sourceAsString());
			
			
			Feature feature = getFeatureFromJson(response);
			
			
			
			featureResults.add(feature);
			
		}
		
		return featureResults;
	}

	@Override
	public List<Feature> synonyms(List<String> features, List<String> types) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Feature> properties(List<String> features) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Feature> pubs(List<String> features) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Feature> dbxrefs(List<String> features) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Feature> terms(List<String> features, List<String> cvs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Feature> orthologues(List<String> features) {
		// TODO Auto-generated method stub
		return null;
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

}
