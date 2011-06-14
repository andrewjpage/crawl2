package org.genedb.crawl.model;

import javax.xml.bind.annotation.XmlAttribute;

public class MappedVariantBase {
	
	public MappedVariantBase() {}
	
	@XmlAttribute
	public Boolean isMultiAllele;
	
	@XmlAttribute
	public Boolean isInsertion;
	
	@XmlAttribute
	public Boolean isDeletion;
	
	@XmlAttribute
	public String alternateBase;
	
	@XmlAttribute
	public int length;

}