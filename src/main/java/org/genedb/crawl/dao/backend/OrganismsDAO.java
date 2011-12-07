package org.genedb.crawl.dao.backend;

import java.util.ArrayList;
import java.util.List;

import org.genedb.crawl.CrawlException;
import org.genedb.crawl.mappers.OrganismsMapper;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrganismsDAO extends BaseDAO implements org.genedb.crawl.dao.OrganismsDAO {
    
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
    
    private String[] trims = new String[] {"strain", ",", "_"};
    
    private List<Organism> addProps(List<Organism> list) {
        for (Organism organism : list) {
            Property prop = organismsMapper.getOrganismProp(organism, "genedb_misc", "translationTable");
            if (prop != null) {
                organism.translation_table = Integer.parseInt(prop.value);
            }
            
            organism.species = organism.species.trim();
            
            if (organism.strain != null) {
                String strain = organism.strain;
                
                int strainPosition = organism.species.indexOf(strain);
                
//                System.out.println(String.format("'%s' '%s' '%s' ... (%s)", organism.species, organism.species, organism.strain, strainPosition));
                
                if (strainPosition > -1) {
                    String newSpecies = organism.species.substring(0,strainPosition).trim();
                    
                    
                    for (String trim : trims) {
//                        System.out.println(String.format("??? '%s' '%s' %s ",newSpecies, trim, newSpecies.endsWith(trim)));
                        if (newSpecies.endsWith(trim)) {
                            String oldSpecies = newSpecies;
                            newSpecies = newSpecies.substring(0, newSpecies.lastIndexOf(trim)).trim();
//                            System.out.println(String.format(">>> '%s' '%s' -> '%s'", trim, oldSpecies, newSpecies));
                        }
                    }
                    
                    newSpecies = newSpecies;
                    
                    organism.species = newSpecies;
                }
                
                //organism.name = String.format("%s %s %s", organism.genus, organism.species, organism.strain).trim();
                
            }
            
        }
        return list;
    }
}
