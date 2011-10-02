package org.genedb.crawl.dao;

import java.util.List;

import org.genedb.crawl.CrawlException;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.Property;

public interface OrganismsDAO {

    public abstract List<Organism> listOrganisms() throws CrawlException;

    public abstract List<Organism> getByID(int id) throws CrawlException;

    public abstract List<Organism> getByTaxonID(int taxonID) throws CrawlException;

    public abstract List<Organism> getByCommonName(String commonName) throws CrawlException;

    public abstract List<Organism> getByString(String organism) throws CrawlException;

    public abstract Property property(String organism, String term, String cv);

    public abstract List<Property> properties(String organism, String cv);

}