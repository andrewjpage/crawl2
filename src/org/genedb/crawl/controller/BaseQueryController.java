package org.genedb.crawl.controller;

import org.genedb.crawl.CrawlException;
import org.genedb.crawl.model.MappedOrganism;
import org.genedb.crawl.model.interfaces.Organisms;


public abstract class BaseQueryController {

	protected MappedOrganism getOrganism(Organisms organisms, String organism) throws CrawlException {
		MappedOrganism mappedOrganism = null;
		
		if (organism.contains(":")) {
			String[] split = organism.split(":");
			
			if (split.length == 2) {
					
				String prefix = split[0];
				String orgDescriptor = split[1];
				
				if (prefix.equals("com")) {
					mappedOrganism = organisms.getByCommonName(orgDescriptor);
				} else if (prefix.equals("tax")) {
					mappedOrganism = organisms.getByTaxonID(orgDescriptor);
				} else if (prefix.equals("org")) {
					mappedOrganism = organisms.getByID(Integer.parseInt(orgDescriptor));
				}
				
			}
			
		} else {
			
			mappedOrganism = organisms.getByCommonName(organism);
			
		}
		
		return mappedOrganism;
	}
	
	
}