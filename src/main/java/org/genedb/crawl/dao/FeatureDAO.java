package org.genedb.crawl.dao;

import java.util.List;

import org.biojava.bio.BioException;
import org.genedb.crawl.CrawlException;
import org.genedb.crawl.model.Dbxref;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.Property;
import org.genedb.util.TranslationException;

public interface FeatureDAO {

    public abstract LocatedFeature getInfo(String uniqueName, String organism, String name, String type);

    public abstract List<Dbxref> dbxrefs(String featureUniqueName, String organism, String name);

    public abstract List<Feature> parents(String featureUniqueName, String organism, String name, String[] relationships) throws CrawlException;

    public abstract List<Feature> children(String featureUniqueName, String organism, String name, String[] relationships) throws CrawlException;

    public abstract Feature hierarchy(String uniqueName, String organism, String name, String[] relationships, Boolean includeSummaries) throws CrawlException;

    public abstract List<LocatedFeature> locations(String feature);

    public abstract List<LocatedFeature> domains(String featureUniqueName, String organism, String name);

    public abstract List<Property> getPolypeptideProperties(String featureUniqueName, String organism, String name) throws BioException, TranslationException;

}