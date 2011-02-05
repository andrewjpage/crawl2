package org.genedb.crawl.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class AnalysisFeature {
	
	@XmlElement
	public Analysis analysis;
	
	@XmlAttribute
	public double rawscore;
	
	@XmlAttribute
	public double normscore;
	
	@XmlAttribute
	public double significance;
	
	@XmlAttribute
	public double identity;
	
}
