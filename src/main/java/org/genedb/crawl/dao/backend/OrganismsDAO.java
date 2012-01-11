package org.genedb.crawl.dao.backend;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.genedb.crawl.CrawlException;
import org.genedb.crawl.mappers.OrganismsMapper;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrganismsDAO extends BaseDAO implements org.genedb.crawl.dao.OrganismsDAO {
    
    private static Logger logger = Logger.getLogger(OrganismsDAO.class);
    
    @Autowired
    private OrganismsMapper organismsMapper;
    
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.dao.backend.OrganismsDAO#listOrganisms()
     */
    @Override
    public List<Organism> listOrganisms() throws CrawlException {
        List<Organism> list = organismsMapper.list();
        return addProps(list);
    }
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.dao.backend.OrganismsDAO#getByID(int)
     */
    @Override
    public List<Organism> getByID(int id) throws CrawlException {
        ArrayList<Organism> list = new ArrayList<Organism>();
        list.add(organismsMapper.getByID(id));
        return addProps(list);
    }
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.dao.backend.OrganismsDAO#getByTaxonID(int)
     */
    @Override
    public List<Organism> getByTaxonID(int taxonID) throws CrawlException {
        ArrayList<Organism> list = new ArrayList<Organism>();
        list.add(organismsMapper.getByTaxonID(String.valueOf(taxonID)));
        return addProps(list);
    }
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.dao.backend.OrganismsDAO#getByCommonName(java.lang.String)
     */
    @Override
    public List<Organism> getByCommonName(String commonName) throws CrawlException {
        ArrayList<Organism> list = new ArrayList<Organism>();
        list.add(organismsMapper.getByCommonName(commonName));
        return addProps(list);
    }
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.dao.backend.OrganismsDAO#getByString(java.lang.String)
     */
    @Override
    public List<Organism> getByString(String organism) throws CrawlException {
        ArrayList<Organism> list = new ArrayList<Organism>();
        list.add(util.getOrganism(organism));
        return addProps(list);
    }
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.dao.backend.OrganismsDAO#property(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Property property(String organism, String term, String cv) {
        Organism o = util.getOrganism(organism);
        return organismsMapper.getOrganismProp(o, cv, term);
    }
    
    /* (non-Javadoc)
     * @see org.genedb.crawl.dao.backend.OrganismsDAO#properties(java.lang.String, java.lang.String)
     */
    @Override
    public List<Property> properties(String organism, String cv) {
        Organism o = util.getOrganism(organism);
        return organismsMapper.getOrganismProps(o, cv);
    }
    
    private List<Organism> addProps(List<Organism> list) {
        for (Organism organism : list) {
            Property prop = organismsMapper.getOrganismProp(organism, "genedb_misc", "translationTable");
            if (prop != null) {
                organism.translation_table = Integer.parseInt(prop.value);
            }
            
            organism.species = organism.species.trim();
            
            /*
             * This is a hack to make remove the strain from species name!
             */
            if (organism.strain != null) {
                String strain = organism.strain;
                
                int strainPosition = organism.species.indexOf(strain);
                
                if (strainPosition > -1) {
                    String newSpecies = organism.species.substring(0,strainPosition).trim();
                    
                    logger.warn(String.format("common_name:: '%s', genus::'%s', species:: '%s', strain:: '%s', displaying species as -------> '%s'", 
                            organism.common_name, organism.genus, organism.species, organism.strain, newSpecies));
                    
                    organism.species = newSpecies;
                    
                }
                
                //organism.name = String.format("%s %s %s", organism.genus, organism.species, organism.strain).trim();
                
            }
            
        }
        return list;
    }
}
