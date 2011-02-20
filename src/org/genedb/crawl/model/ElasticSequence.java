package org.genedb.crawl.model;

import javax.xml.bind.annotation.XmlElement;



public class ElasticSequence {
	
	@XmlElement
	public String name;
	
	@XmlElement
	public SequenceType sequenceType;
	
	@XmlElement
	public Integer organism_id;
	
	public String sequence;
	
	public ElasticSequence(String name, SequenceType sequenceType, int organism_id) {
		this.name = name;
		this.sequenceType = sequenceType;
		this.organism_id = organism_id;
	}
	
	public ElasticSequence() {
		// need an empty constructor!
	}
	
	

	
	
}
