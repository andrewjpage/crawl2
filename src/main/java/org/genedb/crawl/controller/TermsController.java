package org.genedb.crawl.controller;

import javax.jws.WebService;

import org.apache.log4j.Logger;
import org.genedb.crawl.annotations.ResourceDescription;
import org.genedb.crawl.mappers.FeatureCvtermMapper;
import org.genedb.crawl.mappers.OrganismsMapper;
import org.genedb.crawl.mappers.TermsMapper;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.Statistic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/terms")
@ResourceDescription("Term related queries")
@WebService(serviceName="terms")
public class TermsController extends BaseQueryController {
    
    private static Logger logger = Logger.getLogger(TermsController.class);
    
    @Autowired
    TermsMapper termsMapper;
    
    @Autowired
    OrganismsMapper organismsMapper;
    
    @Autowired
    FeatureCvtermMapper featureCvTermsMapper;
    
    @RequestMapping(method=RequestMethod.GET, value="/hello")
    public String[] hello(){
        return new String[]{"hello"};
    }
    
    @ResourceDescription("Counts instances of a term in an organism.")
    @RequestMapping(method=RequestMethod.GET, value="/countInOrganism")
    public Statistic countInOrganism(
            @RequestParam("organism") String organism, 
            @RequestParam(value="term") String term,
            @RequestParam(value="cv") String cv) {
        Organism o = util.getOrganism(organism);
        int i = featureCvTermsMapper.countInOrganism(o, cv, term);
        Statistic statistic = new Statistic();
        statistic.name = "count";
        statistic.value = i;
        return statistic;
    }
    
    
}
