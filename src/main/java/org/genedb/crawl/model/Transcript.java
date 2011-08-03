package org.genedb.crawl.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.genedb.crawl.model.LocatedFeature;


public class Transcript extends LocatedFeature {
	
	@XmlElement(required=false)
	public List<Exon> exons;
}
