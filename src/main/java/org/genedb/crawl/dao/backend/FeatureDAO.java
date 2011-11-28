package org.genedb.crawl.dao.backend;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.biojava.bio.BioException;
import org.genedb.crawl.CrawlException;
import org.genedb.crawl.mappers.FeatureMapper;
import org.genedb.crawl.mappers.FeaturesMapper;
import org.genedb.crawl.mappers.OrganismsMapper;
import org.genedb.crawl.mappers.RegionsMapper;
import org.genedb.crawl.mappers.TermsMapper;
import org.genedb.crawl.model.Cvterm;
import org.genedb.crawl.model.Dbxref;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.Property;
import org.genedb.crawl.model.Synonym;
import org.genedb.util.TranslationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FeatureDAO extends BaseDAO implements org.genedb.crawl.dao.FeatureDAO {
    
    private static Logger logger = Logger.getLogger(FeatureDAO.class);
    
    @Autowired
    public FeaturesMapper  featuresMapper;
    
    @Autowired
    public FeatureMapper   featureMapper;
    
    @Autowired
    public TermsMapper     terms;
    
    @Autowired
    public OrganismsMapper organismsMapper;
    
    @Autowired
    public RegionsMapper regionsMapper;
    
    private String[] defaultRelationshipTypes = new String[] {"part_of", "derives_from"};

    /* (non-Javadoc)
     * @see org.genedb.crawl.controller.FeatureControllerInterface#getInfo(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Feature get(
            String uniqueName, 
            String organism, 
            String name,
            String type) {
        
        
        Integer organism_id =  null;
        if (organism != null) {
            Organism o = util.getOrganism(organism);
            if (o != null) 
                organism_id = o.ID;
        }
        
        Feature resultFeature = featureMapper.get(uniqueName, name, organism_id, type);
        
        util.summarise(resultFeature);
        
        return resultFeature;
        
        //return featuresMapper.pubs(features);
    }
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.controller.FeatureControllerInterface#dbxrefs(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public List<Dbxref> dbxrefs(
            String featureUniqueName, 
            String organism, 
            String name) {
        
        Feature feature = util.getFeature(featureUniqueName, name, organism);
        return featureMapper.dbxrefs(feature);
    }
    
//    private void summarise (Feature feature) {
//        
//        feature.coordinates = featureMapper.coordinates(feature);
//        
//        // TODO - this might need to be fixed to work with non-LocatedFeature instances
////        if (feature instanceof LocatedFeature 
////                && feature.coordinates != null 
////                && feature.coordinates.size() > 0) {
////            LocatedFeature locatedFeature = (LocatedFeature) feature;
////            Coordinates c = locatedFeature.coordinates.get(0);
////            locatedFeature.fmin = c.fmin;
////            locatedFeature.fmax = c.fmax;
////            locatedFeature.region = c.region;
////            locatedFeature.phase = c.phase;
////            locatedFeature.strand = c.strand;
////            
////            feature = LocatedFeatureUtil.fromFeature(feature);
////            
////        }
//        
//        feature.properties = featureMapper.properties(feature);
//        feature.terms = featureMapper.terms(feature);
//        feature.synonyms = featureMapper.synonyms(feature);
//        feature.pubs = featureMapper.pubs(feature);
//        feature.dbxrefs = featureMapper.dbxrefs(feature);
//        feature.domains = featureMapper.domains(feature);
//        feature.orthologues = featureMapper.orthologues(feature);
//        
//        logger.info(String.format("summarising feature %s, last modified %s, last accessioned %s", feature.uniqueName , feature.timeaccessioned, feature.timelastmodified));
//    }
    
    
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.controller.FeatureControllerInterface#parents(java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
     */
    @Override
    public List<Feature> parents( 
            String featureUniqueName, 
            String organism, 
            String name,
            String[] relationships) throws CrawlException {
        
        if (relationships == null || relationships.length < 1) 
            relationships = defaultRelationshipTypes;
        
        List<Cvterm> relationshipTerms = getRelationshipTypes(Arrays.asList(relationships), terms);
        Feature feature = util.getFeature(featureUniqueName, name, organism);
        return featureMapper.parents(feature, relationshipTerms);
    }
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.controller.FeatureControllerInterface#children(java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
     */
    @Override
    public List<Feature> children( 
            String featureUniqueName, 
            String organism, 
            String name,
            String[] relationships) throws CrawlException {
        
        if (relationships == null || relationships.length < 1) 
            relationships = defaultRelationshipTypes;
        
        List<Cvterm> relationshipTerms = getRelationshipTypes(Arrays.asList(relationships), terms);
        Feature feature = util.getFeature(featureUniqueName, name, organism);
        return featureMapper.children(feature, relationshipTerms);
    }
    
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.controller.FeatureControllerInterface#hierarchy(java.lang.String, java.lang.String, java.lang.String, java.lang.String[], java.lang.Boolean)
     */
    @Override
    public Feature hierarchy( 
            String uniqueName, 
            String organism, 
            String name,
            String[] relationships,
            Boolean includeSummaries
            ) throws CrawlException {
        
        
        if (relationships == null || relationships.length < 1) {
            relationships = defaultRelationshipTypes;
        }
        
        if (includeSummaries == null)
            includeSummaries = true;
        
        List<Cvterm> ofType = getRelationshipTypes(Arrays.asList(relationships), terms);
        
        Feature feature = util.getFeature(uniqueName, name, organism);
        
        Feature hierarchyRoot = util.getAncestorGene(feature, ofType);
        
        if (hierarchyRoot == null)
            hierarchyRoot = feature;
        
        hierarchyRoot.organism = this.organismsMapper.getByID(hierarchyRoot.organism_id);
        
        
        util.getDescendants(hierarchyRoot, ofType, includeSummaries);
        
        // mybatis returns 'null' strings here
        if (hierarchyRoot.name != null && hierarchyRoot.name.equals("null")) {
            hierarchyRoot.name = null;
        }
        
        return hierarchyRoot;
        
    }
    
    

    /* (non-Javadoc)
     * @see org.genedb.crawl.controller.FeatureControllerInterface#locations(java.lang.String)
     */
    @Override
    public List<LocatedFeature> locations(String  feature) {
        return this.featuresMapper.locations(feature);
    }
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.controller.FeatureControllerInterface#domains(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public List<LocatedFeature> domains(
            String featureUniqueName, 
            String organism, 
            String name) {
        
        Feature feature = util.getFeature(featureUniqueName, name, organism);
        return featureMapper.domains(feature);
        
    }
    
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.controller.FeatureControllerInterface#getPolypeptideProperties(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public List<Property> getPolypeptideProperties(
            String featureUniqueName, 
            String organism, 
            String name) throws BioException, TranslationException {
    
        Feature feature = util.getFeature(featureUniqueName, name, organism);
        
        // assemble a hierarchy for this feature
        List<Cvterm> ofType = getRelationshipTypes(Arrays.asList(defaultRelationshipTypes), terms);
        Feature geneFeature = util.getAncestorGene(feature, ofType);
        util.getDescendants(geneFeature, ofType, false);
        
        return util.getPolypeptideProperties(feature, geneFeature);
        
        
    }

    @Override
    public List<Synonym> synonyms(String featureUniqueName, String organism, String name) {
        Feature feature = util.getFeature(featureUniqueName, name, organism);
        return featureMapper.synonyms(feature);
    }

    @Override
    public Feature getIsoform(String featureUniqueName, String organism, String name) {
        Feature feature = util.getFeature(featureUniqueName, name, organism);
        List<Cvterm> ofType = getRelationshipTypes(Arrays.asList(defaultRelationshipTypes), terms);
        return util.getIsoform(feature, ofType);
    }
    
}
