package org.genedb.crawl.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class Results {
	
	@XmlAttribute()
	public String name;
	
	
	@XmlElementWrapper(name="organisms")
	@XmlElement(name="organism", type=Organism.class)
	public List<Organism> organisms;
	
	
	@XmlElementWrapper(name="features")
	@XmlElement(name="feature", type=Feature.class)
	public List<Feature> features;
	
	@XmlElementWrapper(name="blastPairs")
	@XmlElement(name="blastPair", type=BlastPair.class)
	public List<BlastPair> blastPairs;
	
	
	@XmlElementWrapper(name="hierarchy")
	@XmlElement(name="feature", type=HierarchicalFeature.class)
	public List<HierarchicalFeature> hierarchy;
	
	public Service service;
	
	public void addOrganism(Organism organism) {
		if (organisms == null) {
			organisms = new ArrayList<Organism>();
		}
		organisms.add(organism);
	}
	
}