package org.genedb.crawl.dao;

import java.util.Date;
import java.util.List;

import org.genedb.crawl.CrawlException;
import org.genedb.crawl.model.BlastPair;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.Gene;
import org.genedb.crawl.model.HierarchicalFeature;
import org.genedb.crawl.model.Statistic;

public interface FeaturesDAO {

    public abstract List<Feature> genes(List<String> features);

    public abstract List<HierarchicalFeature> hierarchy(List<String> features, Boolean root_on_genes, String[] relationships) throws CrawlException;

    public abstract List<Feature> coordinates(List<String> features, String region);

    public abstract List<Feature> synonyms(List<String> features, List<String> types);

    public abstract List<Feature> withnamelike(String term, boolean regex, String region);

    public abstract List<Feature> properties(List<String> features, List<String> types);

    public abstract List<Feature> withproperty(String value, boolean regex, String region, String type);

    public abstract List<Feature> pubs(List<String> features);

    public abstract List<Feature> dbxrefs(List<String> features);

    public abstract List<Feature> terms(List<String> features, List<String> cvs);

    public abstract List<Feature> withterm(String term, String cv, boolean regex, String region);

    public abstract List<Feature> orthologues(List<String> features);

    public abstract List<Feature> clusters(List<String> features);

    public abstract List<Feature> annotationModified(Date date, String organism, String region) throws CrawlException;

    public abstract List<Statistic> annotationModifiedStatistics(Date date, String organism, String region) throws CrawlException;

    public abstract List<BlastPair> blastpair(String f1, int start1, int end1, String f2, int start2, int end2, Integer length, Double score);

    public abstract List<Gene> transcripts(String gene, boolean exons);

}