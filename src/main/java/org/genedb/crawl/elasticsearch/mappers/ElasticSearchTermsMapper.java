package org.genedb.crawl.elasticsearch.mappers;

import java.util.List;

import org.apache.log4j.Logger;
import org.genedb.crawl.mappers.TermsMapper;
import org.genedb.crawl.model.Cvterm;
import org.genedb.crawl.model.Feature;
import org.springframework.stereotype.Component;

@Component
public class ElasticSearchTermsMapper extends ElasticSearchBaseMapper implements TermsMapper {
    
    private Logger logger = Logger.getLogger(ElasticSearchTermsMapper.class);
    
	@Override
	public List<Integer> getCvtermIDs(String cv, String[] cvterms) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getCvtermID(String cv, String cvterm) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void createOrUpdate(Cvterm term) {
        createOrUpdate(connection.getOntologyIndex(), term.cv.name, term.accession, term);
    }

}
