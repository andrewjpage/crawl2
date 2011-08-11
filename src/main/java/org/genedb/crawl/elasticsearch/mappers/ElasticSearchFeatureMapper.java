package org.genedb.crawl.elasticsearch.mappers;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.action.index.IndexRequestBuilder;
import org.elasticsearch.client.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.xcontent.BoolQueryBuilder;
import org.elasticsearch.index.query.xcontent.FieldQueryBuilder;
import org.elasticsearch.index.query.xcontent.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.genedb.crawl.mappers.FeatureMapper;
import org.genedb.crawl.model.Coordinates;
import org.genedb.crawl.model.Cvterm;
import org.genedb.crawl.model.Exon;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.Property;
import org.genedb.crawl.model.Gene;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.Transcript;
import org.springframework.stereotype.Component;

@Component
public class ElasticSearchFeatureMapper extends ElasticSearchBaseMapper implements FeatureMapper {
	
	private Logger logger = Logger.getLogger(ElasticSearchFeatureMapper.class);
	
	@Override
	public Feature get(String uniqueName, String organism_id, String name) {
		
	    BoolQueryBuilder booleanQuery = QueryBuilders.boolQuery();
	    
	    booleanQuery.must(QueryBuilders.fieldQuery("uniqueName", uniqueName));
	    
	    if (organism_id != null) {
	        booleanQuery.must(QueryBuilders.fieldQuery("organism_id", organism_id));
	    }
	    
	    if (name != null) {
            booleanQuery.must(QueryBuilders.fieldQuery("name", name));
        }
	    
	    List<LocatedFeature> features = (List<LocatedFeature>) getAllMatches(connection.getIndex(), connection.getFeatureType(), booleanQuery, LocatedFeature.class);
	    
	    return features.get(0);
	    
	}
	
	public LocatedFeature get(String uniqueName) {
		try {
			return (LocatedFeature) jsonIzer.fromJson (getFromElastic(connection.getIndex(), connection.getFeatureType(), uniqueName), LocatedFeature.class);
		} catch (Exception e) {
		    logger.trace("Could not find " + uniqueName );
			//e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Property> properties(Feature feature) {
		return feature.properties;
	}

	@Override
	public List<Cvterm> terms(Feature feature) {
		return feature.terms;
	}
	
	@Override
	public List<Coordinates> coordinates(Feature feature) {
		return feature.coordinates;
	}
	
//	public void createOrUpdate(ElasticSequence sequence) {
//		
//		try {
//			String json = jsonIzer.toJson(sequence);
//			
//			logger.debug("Storing sequence: " + sequence.name);
//			
//			connection.getClient().prepareIndex("sequences", "Sequence", sequence.name)
//				.setSource(json)
//				.execute()
//				.actionGet();
//		
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//		
//	}
	
	public void createOrUpdate(Feature feature) {
		
		if (feature.coordinates != null && feature.coordinates.size() > 0) {
			if (! (feature instanceof LocatedFeature)) {
				
				LocatedFeature lFeature = new LocatedFeature();
				
				for (Field field : Feature.class.getFields()) {
					try {
						field.set(lFeature, field.get(feature));
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
				
				Coordinates c = feature.coordinates.get(0);
				
				lFeature.fmax = c.fmax;
				lFeature.fmin = c.fmin;
				lFeature.region = c.region;
				lFeature.phase = c.phase;
				lFeature.strand = c.strand;
				
				feature = lFeature;
				
			}
		}
		
		try {
			
			logger.debug("Storing: " + feature.uniqueName);
			
			IndexRequestBuilder builder = connection.getClient().prepareIndex( connection.getIndex(), connection.getFeatureType(), feature.uniqueName);
			String json = jsonIzer.toJson(feature);
			
//			
			logger.debug("Source:");
			logger.debug(json);
			
			builder.setSource(json);
			
//			if (feature instanceof LocatedFeature) {
//				LocatedFeature lFeature = (LocatedFeature) feature;
//				if (lFeature.parent != null) {
//					logger.debug(String.format("Setting %s as parent of %s!", lFeature.parent, feature.uniqueName));
//					builder.setParent(lFeature.parent);
//				}
//			}
			
			//logger.debug(connection.getClient().prepareGet(index, type, feature.uniqueName).execute().actionGet().sourceAsString());
			
			
			builder.execute().actionGet();
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void delete(Feature feature) {
		logger.debug("Deleting " + feature.uniqueName);
		DeleteResponse response = connection
			.getClient()
			.prepareDelete()
			.setIndex(connection.getIndex())
			.setType(connection.getFeatureType())
			.setId(feature.uniqueName)
			.execute()
			.actionGet();
		
		if (response.isNotFound()) {
			logger.warn(feature.uniqueName + " not found");
		}
	}

	@Override
	public List<Transcript> transcripts(Gene gene, boolean exons) {
		
	    List<Transcript> transcripts = new ArrayList<Transcript>();
	    
	    FieldQueryBuilder parentQuery = 
	        QueryBuilders.fieldQuery("parent", gene.uniqueName);
	    
	    FieldQueryBuilder relationshipQuery = 
                QueryBuilders.fieldQuery("parentRelationshipType", "part_of");
	    
	    FieldQueryBuilder typeQuery = 
	            QueryBuilders.fieldQuery("type.name", "mRNA");
	    
	    BoolQueryBuilder transcriptQuery =
	            QueryBuilders.boolQuery()
	            .must(parentQuery)
	            .must(relationshipQuery)
	            .must(typeQuery);
	    
	    SearchRequestBuilder builder = 
            connection
            .getClient()
            .prepareSearch(connection.getIndex())
            .setTypes(connection.getFeatureType());
	    
	    SearchResponse response = builder
	            .setQuery(transcriptQuery)
	            .setExplain(true)
	            .execute()
	            .actionGet();
	    
	    for (SearchHit hit : response.getHits()) {
            
            String source = hit.sourceAsString();
            
            //logger.debug(source);
            
            
            try {
                Transcript t = (Transcript) jsonIzer.fromJson(source, Transcript.class);
                // logger.info("adding transctipt " + t.uniqueName + " " + gene.uniqueName);
                transcripts.add(t);
                
                if (exons) {
                    t.exons = exons(t);
                }
                
            } catch (Exception e) {
                logger.warn(e.getMessage());
               continue;
            } 
            
	    }
	    
		return transcripts;
	}
	
	public List<Exon> exons(Transcript transcript) {
	    
	    List<Exon> exons = new ArrayList<Exon>();
	    
	    FieldQueryBuilder parentQuery = 
	            QueryBuilders.fieldQuery("parent", transcript.uniqueName);
	        
        FieldQueryBuilder relationshipQuery = 
                QueryBuilders.fieldQuery("parentRelationshipType", "part_of");
        
        FieldQueryBuilder typeQuery = 
                QueryBuilders.fieldQuery("type.name", "exon");
        
        BoolQueryBuilder exonQuery =
                QueryBuilders.boolQuery()
                .must(parentQuery)
                .must(relationshipQuery)
                .must(typeQuery);
        
        SearchRequestBuilder builder = 
            connection
            .getClient()
            .prepareSearch(connection.getIndex())
            .setTypes(connection.getFeatureType());
        
        SearchResponse response = builder
                .setQuery(exonQuery)
                .setExplain(true)
                .execute()
                .actionGet();
        
        for (SearchHit hit : response.getHits()) {
            
            String source = hit.sourceAsString();
            
            try {
                Exon e = (Exon) jsonIzer.fromJson(source, Exon.class);
                //logger.info("adding exon " + e.uniqueName + " to " + transcript.uniqueName);
                exons.add(e);
                
            } catch (Exception e) {
                logger.warn(e.getMessage());
               continue;
            } 
            
        }
        
        return exons;
	}
	

	@Override
	public LocatedFeature getOfType(String uniqueName, String organism_id,
			String name, String type) {
		
	    BoolQueryBuilder booleanQuery = QueryBuilders.boolQuery();
        
        booleanQuery.must(QueryBuilders.fieldQuery("uniqueName", uniqueName));
        booleanQuery.must(QueryBuilders.fieldQuery("type.name", type));
	    
	    if (organism_id != null) {
            booleanQuery.must(QueryBuilders.fieldQuery("organism_id", organism_id));
        }
        
        if (name != null) {
            booleanQuery.must(QueryBuilders.fieldQuery("name", name));
        }
        
        List features = null; 
        
        if (type.equals("gene")) {
            features = (List<Gene>) getAllMatches(connection.getIndex(), connection.getFeatureType(), booleanQuery, Gene.class);
        } else if (type.equals("mRNA")) {
            features = (List<Transcript>) getAllMatches(connection.getIndex(), connection.getFeatureType(), booleanQuery, Transcript.class);
        } else if (type.equals("exon")) {
            features = (List<Exon>) getAllMatches(connection.getIndex(), connection.getFeatureType(), booleanQuery, Exon.class);
        } else {
            features = (List<LocatedFeature>) getAllMatches(connection.getIndex(), connection.getFeatureType(), booleanQuery, LocatedFeature.class);
        }
        
        return (LocatedFeature) features.get(0);
	}

	
//	public static String getIndex() {
//		return "features";
//	}
//
//	
//	public static String getType() {
//		return "Feature";
//	}
	
}
