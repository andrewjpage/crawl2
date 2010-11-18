package org.genedb.crawl.model.interfaces;

import org.gmod.cat.Organisms;

public interface QuerySource {
	Regions getRegions();
	Organisms getOrganisms();
}
