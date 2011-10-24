package org.genedb.crawl.dao.backend;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.genedb.crawl.CrawlException;
import org.genedb.crawl.bam.BioDataFileStore;
import org.genedb.crawl.bam.BioDataFileStoreInitializer;
import org.genedb.crawl.controller.VariantController;
import org.genedb.crawl.mappers.FeatureMapper;
import org.genedb.crawl.mappers.RegionsMapper;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.LocationBoundaries;
import org.genedb.crawl.model.MappedSAMSequence;
import org.genedb.crawl.model.MappedVCFRecord;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.Sequence;
import org.genedb.crawl.model.Variant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.ac.sanger.artemis.components.variant.VariantFilterOption;
import uk.ac.sanger.artemis.components.variant.VariantFilterOptions;
import uk.ac.sanger.artemis.util.OutOfRangeException;

@Component
public class VariantDAO extends BaseDAO implements org.genedb.crawl.dao.VariantDAO {
    private static Logger logger = Logger.getLogger(VariantController.class);
    
    private BioDataFileStore<Variant> variantStore;
    
    @Autowired
    private RegionsMapper regionsMapper;
    
    @Autowired
    private FeatureMapper featureMapper;
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.dao.backend.VariantDAO#setInitializer(org.genedb.crawl.bam.BioDataFileStoreInitializer)
     */
    
    @Autowired
    public void setInitializer(BioDataFileStoreInitializer initializer) {
        variantStore=initializer.getVariants();
    }
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.dao.backend.VariantDAO#list()
     */
    @Override
    public List<Variant> list() throws IOException {
        return variantStore.getFiles();
    }
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.dao.backend.VariantDAO#sequences(int)
     */
    @Override
    public List<MappedSAMSequence> sequences(int fileID) throws IOException {
        return variantStore.getSequences(fileID);
    }
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.dao.backend.VariantDAO#listfororganism(java.lang.String)
     */
    @Override
    public List<Variant> listfororganism(String organism) throws IOException {
        Organism mappedOrganism = util.getOrganism(organism);
        return variantStore.listfororganism(mappedOrganism.common_name);
    }
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.dao.backend.VariantDAO#listforsequence(java.lang.String)
     */
    @Override
    public List<Variant> listforsequence(String sequence) throws Exception {
        return variantStore.listforsequence(sequence);
    }
    
    
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.dao.backend.VariantDAO#query(int, java.lang.String, int, int, java.lang.Integer, java.util.List)
     */
    @Override
    public List<MappedVCFRecord> query(
            int fileID, 
            String sequence, 
            int start, 
            int end,
            Integer filter,
            List<String> filters) throws IOException, CrawlException, OutOfRangeException {
        
        if (filter == null && filters != null) {
            if (filters.size() > 0) {
                filter = 0;
                for (String f : filters) {
                    VariantFilterOption option = VariantFilterOption.valueOf(f);
                    filter += option.index();           
                }
            }
        }
        
        return doQuery(fileID,sequence,start,end,filter);
    }
    
    
    private List<MappedVCFRecord> doQuery(int fileID, String sequence, int start, int end, Integer filter) throws IOException, OutOfRangeException {
        VariantFilterOptions options = new VariantFilterOptions(filter);
        
        logger.info(String.format("Filter %d, values: %s ", filter, options.toString()));
        
        String alignmentName = variantStore.getAlignmentFromName(sequence);
        String referenceName = variantStore.getReferenceFromName(sequence);
        
        logger.info(String.format("sequence name supplied: %s, alignment sequence name used: %s, reference sequence name used: %s", sequence, alignmentName, referenceName));
        
        Sequence regionSequence = regionsMapper.sequence(referenceName);
        List<LocatedFeature> geneFeatures = getExons(referenceName, start, end, regionsMapper, featureMapper);
        
        return variantStore.getFile(fileID).getReader().query(
                alignmentName, 
                start, 
                end, 
                variantStore.getFile(fileID).getReader().makeCDSFeatures(geneFeatures, regionSequence), 
                options);
    }
    
    
    // we need to get the gene and pseudo gene features in this request to make sure that the proper boundaries are calculated
    // they will then be ignored by makeCDSFeatures()
    private static final List<String> geneTypes = Arrays.asList(new String[]{"gene", "pseudogene", "exon"});
    
    public static List<LocatedFeature> getExons(String sequence, int start, int end, RegionsMapper regionsMapper, FeatureMapper featureMapper) {
        LocationBoundaries boundaries = regionsMapper.locationsMinAndMaxBoundaries(sequence, start, end, false, geneTypes);
        List<LocatedFeature> features = regionsMapper.locations(sequence, boundaries.start, boundaries.end, false, geneTypes);
        return features;
    }
    
}
