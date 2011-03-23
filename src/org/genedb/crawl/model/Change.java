package org.genedb.crawl.model;

import javax.xml.bind.annotation.XmlAttribute;

public class Change {
	
	@XmlAttribute(required=false)
	public String user;
	
	@XmlAttribute(required=false)
	public String date;
	
	@XmlAttribute(required=false)
	public String detail;
	
	@XmlAttribute(required=false)
	public String type;
}
