package org.genedb.crawl.dao;

import java.util.List;

import org.biojava.bio.BioException;
import org.genedb.crawl.CrawlException;
import org.genedb.crawl.annotations.ListType;
import org.genedb.crawl.model.Dbxref;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.Property;
import org.genedb.crawl.model.Synonym;
import org.genedb.util.TranslationException;

public interface FeatureDAO {

    public abstract Feature get(String uniqueName, String organism, String name, String type);
    
    @ListType("org.genedb.crawl.model.Dbxref")
    public abstract List<Dbxref> dbxrefs(String featureUniqueName, String organism, String name);
    
    @ListType("org.genedb.crawl.model.Synonym")
    public abstract List<Synonym> synonyms(String featureUniqueName, String organism, String name);

    @ListType("org.genedb.crawl.model.Feature")
    public abstract List<Feature> parents(String featureUniqueName, String organism, String name, String[] relationships) throws CrawlException;
    
    @ListType("org.genedb.crawl.model.Feature")
    public abstract List<Feature> children(String featureUniqueName, String organism, String name, String[] relationships) throws CrawlException;

    public abstract Feature hierarchy(String uniqueName, String organism, String name, String[] relationships, Boolean includeSummaries) throws CrawlException;

    @ListType("org.genedb.crawl.model.LocatedFeature")
    public abstract List<LocatedFeature> locations(String feature);
    
    @ListType("org.genedb.crawl.model.LocatedFeature")
    public abstract List<LocatedFeature> domains(String featureUniqueName, String organism, String name);

    @ListType("org.genedb.crawl.model.Property")
    public abstract List<Property> getPolypeptideProperties(String featureUniqueName, String organism, String name) throws BioException, TranslationException;
    
    public abstract Feature getIsoform(String featureUniqueName, String organism, String name);

}