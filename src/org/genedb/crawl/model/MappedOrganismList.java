package org.genedb.crawl.model;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class MappedOrganismList {
	
	@XStreamAlias("organisms")
	public List<MappedOrganism> list = new ArrayList<MappedOrganism>(); 
}
