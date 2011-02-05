package org.genedb.crawl.model;

import java.util.Hashtable;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

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
	
	@XmlElement(name="records", type=MappedQueryRecordElementList.class)
	public List<MappedQueryRecordElementList> records;
	//public Hashtable<String, List<Object>> records = new Hashtable<String, List<Object>>();
	
}
