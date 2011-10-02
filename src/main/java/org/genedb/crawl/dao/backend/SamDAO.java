package org.genedb.crawl.dao.backend;
import java.util.ArrayList;
import java.util.List;

import org.genedb.crawl.CrawlException;
import org.genedb.crawl.bam.BioDataFileStoreInitializer;
import org.genedb.crawl.bam.Sam;
import org.genedb.crawl.model.Alignment;
import org.genedb.crawl.model.MappedCoverage;
import org.genedb.crawl.model.MappedQuery;
import org.genedb.crawl.model.MappedSAMHeader;
import org.genedb.crawl.model.MappedSAMSequence;
import org.genedb.crawl.model.Organism;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SamDAO extends BaseDAO implements org.genedb.crawl.dao.SamDAO  {
    
    private Sam sam = new Sam();

    

    @Autowired
    public void setBioDataFileStoreInitializer(
            BioDataFileStoreInitializer initializer) {
        sam.setAlignmentStore(initializer.getAlignments());
    }

    
    public MappedSAMHeader header(int fileID)
            throws Exception {
        return sam.header(fileID);
    }

    public List<MappedSAMSequence> sequences(int fileID)
            throws Exception {
        return sam.sequence(fileID);
    }

    
    public MappedQuery query(
            int fileID,
            String sequence,
            int start,
            int end,
            Boolean contained,
            int filter,
            String[] properties)
            throws Exception {
        return sam.query(fileID, sequence, start, end, contained, properties,
                filter);
    }

    
    public synchronized MappedCoverage coverage(
            int fileID,
            String sequence,
            int start,
            int end,
            int window,
            Integer filter)
            throws Exception {
        return sam.coverage(fileID, sequence, start, end, window, filter);
    }

    
    public List<Alignment> list() {
        return sam.list();
    }

    
    public List<Alignment> listfororganism(String organism) throws CrawlException {

        List<Alignment> matchedAlignments = new ArrayList<Alignment>();
        Organism mappedOrganism = util.getOrganism(organism);
        if (mappedOrganism != null) {
            matchedAlignments = sam.listfororganism(mappedOrganism.common_name);
        }
        return matchedAlignments;
    }

    public List<Alignment> listforsequence(String sequence) throws Exception {
        List<Alignment> matchedAlignments = sam.listforsequence(sequence);
        return matchedAlignments;
    }
}
