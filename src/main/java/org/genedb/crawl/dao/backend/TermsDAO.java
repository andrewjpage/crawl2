package org.genedb.crawl.dao.backend;

import org.genedb.crawl.mappers.FeatureCvtermMapper;
import org.genedb.crawl.mappers.OrganismsMapper;
import org.genedb.crawl.mappers.TermsMapper;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.Statistic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TermsDAO extends BaseDAO implements org.genedb.crawl.dao.TermsDAO {
    
    @Autowired
    TermsMapper termsMapper;
    
    @Autowired
    OrganismsMapper organismsMapper;
    
    @Autowired
    FeatureCvtermMapper featureCvTermsMapper;
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.dao.backend.TermsDAO#hello()
     */
    @Override
    public String[] hello(){
        return new String[]{"hello"};
    }
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.dao.backend.TermsDAO#countInOrganism(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Statistic countInOrganism(
            String organism, 
            String term,
            String cv) {
        Organism o = util.getOrganism(organism);
        int i = featureCvTermsMapper.countInOrganism(o, cv, term);
        Statistic statistic = new Statistic();
        statistic.name = "count";
        statistic.value = i;
        return statistic;
    }
    
}
