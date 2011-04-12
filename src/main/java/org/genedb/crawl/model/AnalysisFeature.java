package org.genedb.crawl.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class AnalysisFeature implements Serializable {
	
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
