package org.genedb.crawl.dao;

import java.util.List;

import org.genedb.crawl.CrawlException;
import org.genedb.crawl.annotations.ListType;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.Property;

public interface OrganismsDAO {

    @ListType("org.genedb.crawl.model.Organism")
    public abstract List<Organism> listOrganisms() throws CrawlException;
    
    @ListType("org.genedb.crawl.model.Organism")
    public abstract List<Organism> getByID(int id) throws CrawlException;

    @ListType("org.genedb.crawl.model.Organism")
    public abstract List<Organism> getByTaxonID(int taxonID) throws CrawlException;

    @ListType("org.genedb.crawl.model.Organism")
    public abstract List<Organism> getByCommonName(String commonName) throws CrawlException;

    @ListType("org.genedb.crawl.model.Organism")
    public abstract List<Organism> getByString(String organism) throws CrawlException;

    public abstract Property property(String organism, String term, String cv);
    
    @ListType("org.genedb.crawl.model.Property")
    public abstract List<Property> properties(String organism, String cv);

}