package org.genedb.crawl.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class Alignments {
	@XmlElement(required=true, name="alignments")
	public List<Alignment> alignments = new ArrayList<Alignment>();
	
	@XmlElement(required=false, name="sequences")
	public List<AlignmentSequenceAlias> sequences;
}
