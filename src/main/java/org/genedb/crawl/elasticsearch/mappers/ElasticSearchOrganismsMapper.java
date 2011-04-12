package org.genedb.crawl.elasticsearch.mappers;


import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.xcontent.QueryBuilders;
import org.genedb.crawl.mappers.OrganismsMapper;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.OrganismProp;
import org.springframework.stereotype.Component;

@Component
public class ElasticSearchOrganismsMapper extends ElasticSearchBaseMapper implements OrganismsMapper {
	
	private Logger logger = Logger.getLogger(ElasticSearchOrganismsMapper.class);
	
	private int getTotalOrganisms() {
		
		CountResponse cr = connection.getClient()
		 	.prepareCount(connection.getIndex())
		 	.setTypes(connection.getOrganismType())
		 	.setQuery( QueryBuilders.matchAllQuery())
		 	.execute()
	        .actionGet();
		
		long count = cr.count();
		
		return (int) count;
	}
	
	@Override
	public List<Organism> list() {
		
		SearchResponse response = connection.getClient()
			.prepareSearch(connection.getIndex())
			.setTypes(connection.getOrganismType())
			.setQuery(QueryBuilders.matchAllQuery())
			.setSize(getTotalOrganisms())
			.execute()
			.actionGet();
		
		return this.getAllMatches(response, Organism.class);
		
	}

	@Override
	public Organism getByID(int ID) {
		Organism o = (Organism) this.getFirstMatch(connection.getIndex(), connection.getOrganismType(), "ID", String.valueOf(ID), Organism.class);
		logger.info(o);
		return o;
	}

	@Override
	public Organism getByTaxonID(String taxonID) {
		return (Organism) this.getFirstMatch(connection.getIndex(), connection.getOrganismType(), "taxonID", taxonID, Organism.class);
	}

	@Override
	public Organism getByCommonName(String commonName) {
		return (Organism) this.getFirstMatch(connection.getIndex(), connection.getOrganismType(), "common_name", commonName, Organism.class);
	}

	@Override
	public OrganismProp getOrganismProp(int ID, String cv, String cvterm) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void createOrUpdate(Organism organism) {
		
		try {
			String source = jsonIzer.toJson(organism);
			logger.info(source);
			connection.getClient().prepareIndex(connection.getIndex(), connection.getOrganismType(), organism.common_name).setSource(source).execute().actionGet();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		
	}


}