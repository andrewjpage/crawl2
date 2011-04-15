package org.genedb.crawl.model;

import java.util.ArrayList;
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
	
	//@XmlElement(name="records")
	//public List<MappedQueryRecordElementList> records;
	public Hashtable<String, ArrayList<Object>> records = new Hashtable<String, ArrayList<Object>>();
	
	
//	public class RecordStore {
//		@XmlElement
//		public List<Integer> alignmentEnd;
//		public List<Integer> alignmentStart;
//		public List<Integer> flags;
//		public List<String> readName;
//	}
	
//	public Records records;
//	
//	public static class Records {
//		public List<Integer> alignmentEnd;
//		public List<Integer> alignmentStart;
//		public List<Integer> flags;
//		public List<String> readName;
//		
//		private Hashtable<String, List> atts = new Hashtable<String, List>();
//		
//		{
//			atts.put("alignmentEnd", alignmentEnd);
//		}
//		
//		List get(String name) {
//			return atts.get(name);
//		}
//		
//	}
	
}
