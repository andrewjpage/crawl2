package org.genedb.crawl.model.gff;

import javax.xml.bind.annotation.XmlElement;



public class Sequence {
	
	@XmlElement
	public String name;
	
	@XmlElement
	public SequenceType sequenceType;
	
	@XmlElement
	public String sequence = "";
	
	public Sequence(String name, SequenceType sequenceType) {
		this.name = name;
		this.sequenceType = sequenceType;
	}
	
	
	
}
