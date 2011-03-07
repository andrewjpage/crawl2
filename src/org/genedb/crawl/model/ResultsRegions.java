package org.genedb.crawl.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class ResultsRegions extends Results {
	
	@XmlElement(required=true)
	public Integer actual_start;
	
	@XmlElement(required=true)
	public Integer actual_end;
	
	@XmlElementWrapper(name="locations")
	@XmlElement(name="location", type=LocatedFeature.class)
	public List<LocatedFeature> locations;
	
	@XmlElementWrapper(name="sequences")
	@XmlElement(name="sequence", type=Sequence.class)
	public List<Sequence> sequences;
	
	@XmlElementWrapper(name="regions")
	@XmlElement(name="region", type=String.class)
	public List<Feature> regions;
	
	
}
