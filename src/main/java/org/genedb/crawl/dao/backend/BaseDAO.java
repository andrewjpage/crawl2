package org.genedb.crawl.dao.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.genedb.crawl.mappers.TermsMapper;
import org.genedb.crawl.model.Cv;
import org.genedb.crawl.model.Cvterm;
import org.genedb.crawl.modelling.FeatureMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;


public abstract class BaseDAO {
    
    
    @Autowired
    public FeatureMapperUtil util;
    
    
    private Map<String, String> relationshipTypes;
    
    @javax.annotation.Resource() 
    public void setRelationshipTypes(Map<String, String> relationshipTypes) {
        this.relationshipTypes = relationshipTypes;
    }
    
    protected List<Cvterm> getRelationshipTypes(List<String> types, TermsMapper termsMapper) {
        List<Cvterm> terms = new ArrayList<Cvterm>();
        for (String type : types) {
            if (relationshipTypes.containsKey(type)) {
                
                Cvterm cvterm = new Cvterm();
                cvterm.name = type;
                cvterm.cv = new Cv();
                cvterm.cv.name = relationshipTypes.get(type);
                
                cvterm.cvterm_id = termsMapper.getCvtermID(cvterm.cv.name, cvterm.name);
                
                terms.add(cvterm);
            }
        }
        return terms;
    }
    
    

    
    
}