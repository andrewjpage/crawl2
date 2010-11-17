package org.genedb.crawl.business.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


import org.genedb.crawl.CrawlException;
import org.genedb.crawl.model.MappedOrganism;
import org.genedb.crawl.model.interfaces.Organisms;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class OrganismsQueries extends Base  {

	
	public List<MappedOrganism> list() {
		
		String sql = queryMap.getQuery("get_all_organisms_and_taxon_ids");
		
		Collection<MappedOrganism> results = jdbcTemplate.query( sql, new HashMap<String, Object>(), new RowMapper<MappedOrganism>() {
			public MappedOrganism mapRow(ResultSet rs, int rowNum) throws SQLException {
				MappedOrganism o = new MappedOrganism ();
				o.genus = rs.getString("genus");
				o.species = rs.getString("species");
				o.common_name = rs.getString("common_name");
				o.ID = rs.getString("ID");
				o.taxonID = rs.getString("taxonID");
				return o;
			}
		});
		
		return new ArrayList<MappedOrganism>(results);
		
	}

	

}
