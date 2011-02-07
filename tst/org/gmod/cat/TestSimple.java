package org.gmod.cat;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.genedb.crawl.CrawlException;
import org.genedb.crawl.model.Organism;

import junit.framework.TestCase;

public class TestSimple extends TestCase {
	
	private static SqlSessionFactory sqlMapper = null;
	
	public void test1() throws CrawlException {
		
		String resource = "sql/test.xml";
		Reader reader = null;

		try {
			reader = Resources.getResourceAsReader(resource);
			sqlMapper = new SqlSessionFactoryBuilder().build(reader, System.getProperties());
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		SqlSession session = sqlMapper.openSession();
		
		try {
			OrganismsMapper mapper = session.getMapper(OrganismsMapper.class);
			List<Organism> organisms = mapper.list();
			
			for (Organism organism : organisms) {
				System.out.println(organism.common_name);
			}
			
		} finally {
			session.close();
		}
		
	}

}