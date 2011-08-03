package org.genedb.crawl.model;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;

import org.genedb.crawl.model.LocatedFeature;

public class Gene extends LocatedFeature {
	
	@XmlElement(required=false)
	public List<Transcript> transcripts;
}
