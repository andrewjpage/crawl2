package org.genedb.crawl;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


import com.thoughtworks.xstream.annotations.XStreamAlias;

@Controller
@RequestMapping("/organisms")
public class Organisms {
	
	private Logger logger = Logger.getLogger(Organisms.class);
	private JdbcTemplate jdbcTemplate;
	
	private QueryMap queryMap;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
	    this.jdbcTemplate = new JdbcTemplate(dataSource);
	    logger.info(this.jdbcTemplate);
	}
	
	@Autowired
	public void setQueryMap(QueryMap queryMap) {
		this.queryMap = queryMap;
		logger.info(this.queryMap);
	}
	
	
	@RequestMapping(method=RequestMethod.GET, value={"/list", "/list.*"})
	public ModelAndView list() {
		ModelAndView mav = new ModelAndView("service:");
		
		String sql = queryMap.getQuery("get_all_organisms_and_taxon_ids");
		
		Collection<MappedOrganism> results = jdbcTemplate.query( sql, new OrganismMapper() );
		
		mav.addObject("model", results);

		return mav;
	}
	
	private static class OrganismMapper implements RowMapper {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			MappedOrganism o = new MappedOrganism ();
			o.genus = rs.getString("genus");
			o.species = rs.getString("species");
			o.common_name = rs.getString("common_name");
			o.ID = rs.getInt("ID");
			o.taxonID = rs.getString("taxonID");
			return o;
		}
	}
	
	
	
}


@XStreamAlias("organism")
class MappedOrganism {
	String genus;
	String species;
	String common_name;
	String taxonID;
	int ID;
}

