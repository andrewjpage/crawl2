package org.genedb.crawl.business.postgres;

import org.genedb.crawl.model.interfaces.Organisms;
import org.genedb.crawl.model.interfaces.QuerySource;
import org.genedb.crawl.model.interfaces.Regions;

public class PostgresQuerySource extends Base implements QuerySource {
	
	@Override
	public Regions getRegions() {
		RegionsQueries regions = new RegionsQueries();
		initialise(regions);
		return regions;
	}

//	@Override
//	public Organisms getOrganisms() {
//		OrganismsQueries organisms = new OrganismsQueries();
//		initialise(organisms);
//		return organisms;
//	}
	
	private Base initialise(Base query) {
		query.jdbcTemplate = jdbcTemplate;
		query.queryMap = queryMap;
		return query;
	}
	
}
