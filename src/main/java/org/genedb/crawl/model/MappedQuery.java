package org.genedb.crawl.model;

import javax.xml.bind.annotation.XmlAttribute;


public class MappedQuery {
	
	@XmlAttribute
	public boolean contained;
	
	@XmlAttribute
	public int start;
	
	@XmlAttribute
	public int end;
	
	@XmlAttribute
	public String sequence;
	
	@XmlAttribute
	public int count;
	
	@XmlAttribute
	public String time;
	
	@XmlAttribute
	public int filter;
	
	//@XmlElement(name="records")
	//public List<MappedQueryRecordElementList> records;
	//public Hashtable<String, ArrayList<Object>> records = new Hashtable<String, ArrayList<Object>>();
	
	public MappedSAMRecords records;
	
	
	
	
}
