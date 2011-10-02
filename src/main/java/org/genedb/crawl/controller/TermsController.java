package org.genedb.crawl.controller;

import javax.jws.WebService;

import org.genedb.crawl.annotations.ResourceDescription;
import org.genedb.crawl.dao.TermsDAO;
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
public class TermsController extends BaseController implements TermsDAO {
    
    @Autowired
    TermsDAO dao;
    
    @Override
    @RequestMapping(method=RequestMethod.GET, value="/hello")
    public String[] hello(){
        return dao.hello();
    }
    
    @Override
    @ResourceDescription("Counts instances of a term in an organism.")
    @RequestMapping(method=RequestMethod.GET, value="/countInOrganism")
    public Statistic countInOrganism(
            @RequestParam("organism") String organism, 
            @RequestParam(value="term") String term,
            @RequestParam(value="cv") String cv) {
        return dao.countInOrganism(organism, term, cv);
    }
    
    
}
