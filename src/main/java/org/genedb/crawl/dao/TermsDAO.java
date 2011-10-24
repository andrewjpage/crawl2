package org.genedb.crawl.dao;

import org.genedb.crawl.model.Statistic;

public interface TermsDAO {

    public abstract String[] hello();

    public abstract Statistic countInOrganism(String organism, String term, String cv);

}