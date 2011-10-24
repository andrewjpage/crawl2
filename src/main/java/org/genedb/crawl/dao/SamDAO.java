package org.genedb.crawl.dao;

import java.util.List;

import org.genedb.crawl.CrawlException;
import org.genedb.crawl.annotations.ListType;
import org.genedb.crawl.model.Alignment;
import org.genedb.crawl.model.MappedCoverage;
import org.genedb.crawl.model.MappedQuery;
import org.genedb.crawl.model.MappedSAMHeader;
import org.genedb.crawl.model.MappedSAMSequence;

public interface SamDAO {

    public abstract MappedSAMHeader header(int fileID) throws Exception;

    @ListType("org.genedb.crawl.model.MappedSAMSequence")
    public abstract List<MappedSAMSequence> sequences(int fileID) throws Exception;

    public abstract MappedQuery query(int fileID, String sequence, int start, int end, Boolean contained, int filter, String[] properties) throws Exception;

    public abstract MappedCoverage coverage(int fileID, String sequence, int start, int end, int window, Integer filter) throws Exception;

    @ListType("org.genedb.crawl.model.Alignment")
    public abstract List<Alignment> list();

    @ListType("org.genedb.crawl.model.Alignment")
    public abstract List<Alignment> listfororganism(String organism) throws CrawlException;

    @ListType("org.genedb.crawl.model.Alignment")
    public abstract List<Alignment> listforsequence(String sequence) throws Exception;

}