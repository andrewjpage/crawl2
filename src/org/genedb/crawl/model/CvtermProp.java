package org.genedb.crawl.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class CvtermProp {
	
	@XmlAttribute
	public String value;
	
	@XmlElement
	public Cvterm type;
}
