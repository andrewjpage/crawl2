package org.genedb.crawl.search.mappers;

import java.util.List;

import org.genedb.crawl.CrawlException;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.OrganismProp;
import org.gmod.cat.OrganismsMapper;

public class ElasticSearchOrganismsMapper implements OrganismsMapper {

	@Override
	public List<Organism> list() throws CrawlException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Organism getByID(int ID) throws CrawlException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Organism getByTaxonID(String taxonID) throws CrawlException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Organism getByCommonName(String commonName) throws CrawlException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrganismProp getOrganismProp(int ID, String cv, String cvterm) {
		// TODO Auto-generated method stub
		return null;
	}

}
