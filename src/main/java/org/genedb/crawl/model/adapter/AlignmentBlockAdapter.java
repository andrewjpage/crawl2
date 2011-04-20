package org.genedb.crawl.model.adapter;

import javax.xml.bind.annotation.XmlAttribute;

import net.sf.samtools.AlignmentBlock;

public class AlignmentBlockAdapter {
	
	@XmlAttribute
	public int length;
	
	@XmlAttribute
	public int readStart;
	
	@XmlAttribute
	public int referenceStart;
	
	public AlignmentBlockAdapter(AlignmentBlock block) {
		length = block.getLength();
		readStart = block.getReadStart();
		referenceStart = block.getReferenceStart();
	}
	
	public AlignmentBlockAdapter() { }
	
}
