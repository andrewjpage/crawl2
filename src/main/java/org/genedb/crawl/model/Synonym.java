package org.genedb.crawl.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;

public class Synonym implements Serializable {
	
	@XmlAttribute
	public String synonym;
	
	@XmlAttribute
	public String synonymtype;
	
	@XmlAttribute
	public Boolean is_current;
}
