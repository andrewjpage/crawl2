package org.genedb.crawl.elasticsearch.index.sql;

import org.genedb.crawl.model.Cv;
import org.genedb.crawl.model.Cvterm;
import org.gmod.cat.TermsMapper;

public class CvtermUtil {
	
	public static Cvterm makeTerm(TermsMapper termsMapper, String cvName, String cvtermName) {
		Cvterm cvterm = new Cvterm();
		cvterm.name = cvtermName;
		
		Cv cv = new Cv();
		cv.name = cvName;
		
		cvterm.cv = cv;
		
		cvterm.cvterm_id = termsMapper.getCvtermID(cvterm.cv.name, cvterm.name);
		
		return cvterm;
	}
}
