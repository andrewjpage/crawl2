package org.genedb.crawl;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/testing")
public class Tester {
	
	private Logger logger = Logger.getLogger(Tester.class);
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
	    this.jdbcTemplate = new JdbcTemplate(dataSource);
	    logger.info(this.jdbcTemplate);
	}
	
	@RequestMapping(method=RequestMethod.GET, value={"/jquery", "/jquery.*"})
	public ModelAndView test() {
		ModelAndView mav = new ModelAndView("jsp:test");
		mav.addObject("model", "testing");
		
		System.out.println("TEST!");
		logger.info("TEST!");
		return mav;
	}

}
