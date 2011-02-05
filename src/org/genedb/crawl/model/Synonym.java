package org.genedb.crawl.model;

import javax.xml.bind.annotation.XmlAttribute;

public class Synonym {
	
	@XmlAttribute
	public String synonym;
	
	@XmlAttribute
	public String synonymtype;
	
	@XmlAttribute
	public boolean is_current;
}
