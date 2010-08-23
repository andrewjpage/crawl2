package org.genedb.crawl.business.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


import org.genedb.crawl.model.BaseResult;
import org.genedb.crawl.model.LocationRegion;
import org.genedb.crawl.model.Locations;
import org.genedb.crawl.model.interfaces.Regions;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

@Component
public class RegionsQueries extends Base implements Regions {

	@Override
	public Locations locations(String region, int start, int end) {
		
		int regionid = this.getFeaureID(region);
		
		Locations locations = new Locations();
		locations.request_start = start;
		locations.request_end = end;
		locations.region = region;
		
		String sql = queryMap.getQuery("get_locations");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("regionid", regionid);
		params.put("start", start);
		params.put("end", end);
		
		Collection<LocationRegion> results = jdbcTemplate.query( sql, params, new RowMapper<LocationRegion>() {
			public LocationRegion mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				LocationRegion lr = new LocationRegion();
				
				lr.start = rs.getString("start");
				lr.end = rs.getString("end");
				lr.feature = rs.getString("feature");
				
				lr.is_obsolete = BaseResult.bool(rs.getBoolean(("is_obsolete")));
				lr.phase = rs.getString("phase");
				lr.type = rs.getString("type");
				
				return lr;
				
			}
		});
		
		locations.features = new ArrayList<LocationRegion>(results);
		
		return locations;
	}
	
	private int getFeaureID(String uniquename) {
		String sql = "select feature_id from feature where uniquename = :uniquename";
		SqlParameterSource param = new MapSqlParameterSource("uniquename", uniquename);
		return jdbcTemplate.queryForInt(sql, param);
	}
	
	
	
	
}
