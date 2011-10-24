package org.genedb.crawl.dao;

import java.util.List;

import org.genedb.crawl.CrawlException;
import org.genedb.crawl.annotations.ListType;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.Sequence;

public interface RegionsDAO {

    
    @ListType("org.genedb.crawl.model.LocatedFeature")
    public abstract List<LocatedFeature> locations(String region, Integer start, Integer end, Boolean exclude, List<String> types) throws CrawlException;

    @ListType("org.genedb.crawl.model.Sequence")
    public abstract List<Sequence> sequenceLength(String region);

    @ListType("org.genedb.crawl.model.Sequence")
    public abstract List<Sequence> sequence(String region, Integer start, Integer end);

    public abstract Feature getInfo(String uniqueName, String name, String organism) throws CrawlException;

    @ListType("org.genedb.crawl.model.Feature")
    public abstract List<Feature> inorganism(String organism, Integer limit, Integer offset, String type) throws CrawlException;

    @ListType("org.genedb.crawl.model.Feature")
    public abstract List<Feature> typesInOrganism(String organism) throws CrawlException;

}