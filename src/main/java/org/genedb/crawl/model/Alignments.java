package org.genedb.crawl.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * A descriptor for various kinds of next-gen format files and reference sequences.
 * @author gv1
 *
 */
public class Alignments {
	
	@XmlElement(required=true, name="alignments")
	public List<Alignment> alignments = new ArrayList<Alignment>();
	
	@XmlElement(required=false, name="sequences")
	public List<AlignmentSequenceAlias> sequences;
	
	@XmlElement(required=true, name="variants")
	public List<Variant> variants;
	
}
