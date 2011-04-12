package org.genedb.crawl.model;

import javax.xml.bind.annotation.XmlAttribute;


public class Statistic {
	
	@XmlAttribute
	String name;
	
	@XmlAttribute
	Integer value;
}
