package org.genedb.crawl.dao;

import java.io.IOException;
import java.util.List;

import org.genedb.crawl.CrawlException;
import org.genedb.crawl.model.MappedSAMSequence;
import org.genedb.crawl.model.MappedVCFRecord;
import org.genedb.crawl.model.Variant;

import uk.ac.sanger.artemis.util.OutOfRangeException;

public interface VariantDAO {

    public abstract List<Variant> list() throws IOException;

    public abstract List<MappedSAMSequence> sequences(int fileID) throws IOException;

    public abstract List<Variant> listfororganism(String organism) throws IOException;

    public abstract List<Variant> listforsequence(String sequence) throws Exception;

    public abstract List<MappedVCFRecord> query(int fileID, String sequence, int start, int end, Integer filter, List<String> filters) throws IOException, CrawlException, OutOfRangeException;

}