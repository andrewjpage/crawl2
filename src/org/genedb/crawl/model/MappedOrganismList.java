package org.genedb.crawl.model;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.google.gson.annotations.Expose;

@XStreamAlias("organisms")
public class MappedOrganismList extends BaseResult {
	
	@XStreamImplicit
	@Expose
	public List<MappedOrganism> organisms = new ArrayList<MappedOrganism>(); 
}
