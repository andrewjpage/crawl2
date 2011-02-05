package org.genedb.crawl.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class ResultsRegions extends Results {

	
	@XmlElementWrapper(name="locations")
	@XmlElement(name="location", type=LocationRegion.class)
	public List<LocationRegion> locations;
	
	@XmlElementWrapper(name="sequences")
	@XmlElement(name="sequence", type=Sequence.class)
	public List<Sequence> sequences;
	
	@XmlElementWrapper(name="regions")
	@XmlElement(name="region", type=String.class)
	public List<String> regions;
	
	
}
