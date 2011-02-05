package org.genedb.crawl.model.interfaces;

import org.gmod.cat.OrganismsMapper;

public interface QuerySource {
	Regions getRegions();
	OrganismsMapper getOrganisms();
}
