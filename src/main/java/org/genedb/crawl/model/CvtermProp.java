package org.genedb.crawl.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@SuppressWarnings("serial")
public class CvtermProp implements Serializable {
	
	@XmlAttribute
	public String value;
	
	@XmlElement
	public Cvterm type;

	@XmlElement
	public Pub pub;
}
