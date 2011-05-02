package org.genedb.crawl.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class MappedVCFRecord {
	
	public MappedVCFRecord() {}
	
	@XmlAttribute
	public String chrom;
	
	@XmlAttribute
	public int pos;
	
	@XmlAttribute
	public float quality;
	
	@XmlAttribute
	public String ref;
	
	@XmlAttribute
	public int ref_length;
	
	@XmlElement
	public MappedVariantBase alt = new MappedVariantBase();
	
	
}
