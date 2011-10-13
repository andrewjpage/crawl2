package org.genedb.crawl.dao.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.genedb.crawl.CrawlException;
import org.genedb.crawl.mappers.FeatureMapper;
import org.genedb.crawl.mappers.FeaturesMapper;
import org.genedb.crawl.mappers.MapperUtil;
import org.genedb.crawl.mappers.OrganismsMapper;
import org.genedb.crawl.mappers.TermsMapper;
import org.genedb.crawl.mappers.MapperUtil.HierarchicalSearchType;
import org.genedb.crawl.model.BlastPair;
import org.genedb.crawl.model.Cvterm;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.Gene;
import org.genedb.crawl.model.HierarchicalFeature;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.Statistic;
import org.genedb.crawl.model.Transcript;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class FeaturesDAO extends BaseDAO implements org.genedb.crawl.dao.FeaturesDAO {
    
    private static Logger logger = Logger.getLogger(FeaturesDAO.class);
    
    @Autowired
    FeaturesMapper featuresMapper;
    
    @Autowired
    FeatureMapper featureMapper;
    
    @Autowired
    TermsMapper terms;
    
    @Autowired
    OrganismsMapper organismsMapper;
    
    private String[] defaultRelationshipTypes = new String[] {"part_of", "derives_from"};
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.controller.backend.FeaturesControllerInterface#genes(java.util.List)
     */
    @Override
    public List<Feature> genes(List<String> features) {
        return MapperUtil.getGeneFeatures(featuresMapper, features);
    }
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.controller.backend.FeaturesControllerInterface#hierarchy(java.util.List, java.lang.Boolean, java.lang.String[])
     */
    @Override
    public List<HierarchicalFeature> hierarchy( 
            List<String> features, 
            Boolean root_on_genes,
            String[] relationships) throws CrawlException {
        
        // JAX-WS does not know about defaultValue
        if (root_on_genes == null)
            root_on_genes = false;
        
        if (relationships == null || relationships.length < 1) {
            relationships = defaultRelationshipTypes;
        }
        
        List<Cvterm> relationshipTypes = getRelationshipTypes(Arrays.asList(relationships), terms);
        List<String> featuresToRecurse = features;
        List<HierarchicalFeature> hfs = new ArrayList<HierarchicalFeature>();
        
        if (root_on_genes) {
            featuresToRecurse = new ArrayList<String>();
            
            Collection<Feature> featureGenes = MapperUtil.getGeneFeatures(featuresMapper,features);
            
            for (Feature fg : featureGenes) {
                featuresToRecurse.addAll(fg.genes);
            }
            
        }
        
        for (String feature : featuresToRecurse) {
            
            HierarchicalFeature hf = new HierarchicalFeature();
            hf.uniqueName = feature;
            
            Feature f = featureMapper.get(feature, null, null, null);
            hf.type = f.type.name;
            
            MapperUtil.searchForRelations(featuresMapper, hf, relationshipTypes, HierarchicalSearchType.CHILDREN);
            MapperUtil.searchForRelations(featuresMapper, hf, relationshipTypes, HierarchicalSearchType.PARENTS);
            
            hfs.add(hf);
            
            
            
            
        }
        
        
        return hfs;
        
        
        
    }
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.controller.backend.FeaturesControllerInterface#coordinates(java.util.List, java.lang.String)
     */
    @Override
    public List<Feature> coordinates(
            List<String> features, 
            String region ) {
        return featuresMapper.coordinates(features, region);
    }
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.controller.backend.FeaturesControllerInterface#synonyms(java.util.List, java.util.List)
     */
    @Override
    public List<Feature> synonyms(
            List<String> features,
            List<String> types) {
        return featuresMapper.synonyms(features, types);
    }
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.controller.backend.FeaturesControllerInterface#withnamelike(java.lang.String, boolean, java.lang.String)
     */
    @Override
    public List<Feature> withnamelike( 
            String term,
            boolean regex, 
            String region) {
        List<Feature> synonyms = featuresMapper.synonymsLike(term, regex, region);
        List<Feature> matchingFeatures = featuresMapper.featuresLike(term, regex, region);
        matchingFeatures.addAll(synonyms);
        return matchingFeatures;
    }
    
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.controller.backend.FeaturesControllerInterface#properties(java.util.List, java.util.List)
     */
    @Override
    public List<Feature> properties(
            List<String> features, 
            List<String> types) {
        return featuresMapper.properties(features,types);
    }
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.controller.backend.FeaturesControllerInterface#withproperty(java.lang.String, boolean, java.lang.String, java.lang.String)
     */
    @Override
    public List<Feature> withproperty( 
            String value,
            boolean regex, 
            String region,
            String type) {
        return featuresMapper.withproperty(value, regex, region, type);
    }
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.controller.backend.FeaturesControllerInterface#pubs(java.util.List)
     */
    @Override
    public List<Feature> pubs(List<String> features) {
        return featuresMapper.pubs(features);
    }
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.controller.backend.FeaturesControllerInterface#dbxrefs(java.util.List)
     */
    @Override
    public List<Feature> dbxrefs(List<String> features) {
        return featuresMapper.dbxrefs(features);
    }
    
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.controller.backend.FeaturesControllerInterface#terms(java.util.List, java.util.List)
     */
    @Override
    public List<Feature> terms(List<String> features, List<String> cvs) {
        return featuresMapper.terms(features, cvs);
    }
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.controller.backend.FeaturesControllerInterface#withterm(java.lang.String, java.lang.String, boolean, java.lang.String)
     */
    @Override
    public List<Feature> withterm(
            String term, 
            String cv,
            boolean regex, 
            String region) {
        
        logger.info(String.format("%s - %s - %s - %s", term, cv, regex, region));
        
        return featuresMapper.withterm(term, cv, regex, region);
        
    }
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.controller.backend.FeaturesControllerInterface#orthologues(java.util.List)
     */
    @Override
    public List<Feature> orthologues(List<String> features) {
        return featuresMapper.orthologues(features);
    }
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.controller.backend.FeaturesControllerInterface#clusters(java.util.List)
     */
    @Override
    public List<Feature> clusters(List<String> features) {
        return featuresMapper.clusters(features);
    }
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.controller.backend.FeaturesControllerInterface#annotationModified(java.util.Date, java.lang.String, java.lang.String)
     */
    @Override
    public List<Feature> annotationModified( 
            Date date, 
            String organism, 
            String region) throws CrawlException {
        Organism o = util.getOrganism(organism);
        return featuresMapper.annotationModified(date, o.ID, region);
    }
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.controller.backend.FeaturesControllerInterface#annotationModifiedStatistics(java.util.Date, java.lang.String, java.lang.String)
     */
    @Override
    public List<Statistic> annotationModifiedStatistics( 
            Date date, 
            String organism, 
            String region) throws CrawlException {
        Organism o = util.getOrganism(organism);
        return featuresMapper.annotationModifiedStatistics(date, o.ID, region);
    }
    
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.controller.backend.FeaturesControllerInterface#blastpair(java.lang.String, int, int, java.lang.String, int, int, java.lang.Integer, java.lang.Double)
     */
    @Override
    public List<BlastPair> blastpair( 
            String f1, 
            int start1, 
            int end1,
            String f2, 
            int start2, 
            int end2,
            Integer length,
            Double score) {
        logger.info("Filtering on score :");
        logger.info(score);
        return featuresMapper.blastPairs(f1, start1, end1, f2, start2, end2, length, score); 
    }
    
    
        
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.controller.backend.FeaturesControllerInterface#transcripts(java.lang.String, boolean)
     */
    @Override
    public List<Gene> transcripts(String gene, boolean exons) {
        List<Gene> l = new ArrayList<Gene>(); 
        Gene geneFeature = (Gene) featureMapper.get(gene, null, null, "gene");
        if (geneFeature != null) {
            logger.info(geneFeature.getClass());
            logger.info(geneFeature.uniqueName);
            
            geneFeature.transcripts = featureMapper.transcripts(geneFeature, exons);
            logger.info(geneFeature.transcripts);
            
            for (Transcript t : geneFeature.transcripts) {
                logger.info(t.uniqueName);
            }
            
             
            l.add(geneFeature);
            
        }
        
        
        return l;
        
        //return featuresMapper.pubs(features);
    }
    
    
    
}
