package org.genedb.crawl.dao;

import java.io.IOException;
import java.util.List;

import org.genedb.crawl.CrawlException;
import org.genedb.crawl.annotations.ListType;
import org.genedb.crawl.model.MappedSAMSequence;
import org.genedb.crawl.model.MappedVCFRecord;
import org.genedb.crawl.model.Variant;

import uk.ac.sanger.artemis.util.OutOfRangeException;

public interface VariantDAO {

    @ListType("org.genedb.crawl.model.Variant")
    public abstract List<Variant> list() throws IOException;

    @ListType("org.genedb.crawl.model.MappedVCFSequence")
    public abstract List<MappedSAMSequence> sequences(int fileID) throws IOException;

    @ListType("org.genedb.crawl.model.Variant")
    public abstract List<Variant> listfororganism(String organism) throws IOException;

    @ListType("org.genedb.crawl.model.Variant")
    public abstract List<Variant> listforsequence(String sequence) throws Exception;

    @ListType("org.genedb.crawl.model.MappedVCFRecord")
    public abstract List<MappedVCFRecord> query(int fileID, String sequence, int start, int end, Integer filter, List<String> filters) throws IOException, CrawlException, OutOfRangeException;

}