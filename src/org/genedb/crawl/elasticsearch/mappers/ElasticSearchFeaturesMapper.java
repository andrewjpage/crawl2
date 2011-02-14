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
	
	
	
	
	
	
	
	
	
	
}
