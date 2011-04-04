package org.genedb.crawl.elasticsearch.mappers;

import java.util.List;

import org.gmod.cat.TermsMapper;
import org.springframework.stereotype.Component;

@Component
public class ElasticSearchTermsMapper extends ElasticSearchBaseMapper implements TermsMapper {

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

	
//	public static String getIndex() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	
//	public static String getType() {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
