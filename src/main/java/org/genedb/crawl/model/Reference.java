package org.genedb.crawl.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Reference {
	
	@XmlAttribute(required = true)
	public String file;
	
	@XmlElement(required = true)
	public Organism organism;
	
}
