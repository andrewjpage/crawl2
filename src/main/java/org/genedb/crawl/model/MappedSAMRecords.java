package org.genedb.crawl.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.genedb.crawl.model.adapter.AlignmentBlockAdapter;

public class MappedSAMRecords {
	
	/**
	 * We are using a mutlidimensional array here because JAXB is unable to deal with lists of lists (i.e., List<List<AlignmentBlockAdapter>>). 
	 */
	@XmlElement
	public List<AlignmentBlockAdapter[]> alignmentBlocks;
	
	@XmlElement
	public List<Integer> alignmentEnd;
	
	@XmlElement
	public List<Integer> alignmentStart;
	
	// needs an adapter like alignment blocks
	// @XmlElement
	// public List<SAMRecord.SAMTagAndValue> attributes;
	
	@XmlElement
	public List<String> baseQualityString;
	
	@XmlElement
	public List<String> cigarString;
	
	@XmlElement
	public List<Boolean> duplicateReadFlag;
	
	@XmlElement
	public List<Integer> flags;
	
	@XmlElement
	public List<Boolean> firstOfPairFlag;
	
	@XmlElement
	public List<Integer> inferredInsertSize;
	
	@XmlElement
	public List<Integer> mappingQuality;
	
	@XmlElement
	public List<Integer> mateAlignmentStart;
	
	@XmlElement
	public List<Boolean> mateNegativeStrandFlag;
	
	@XmlElement
	public List<Integer> mateReferenceIndex;
	
	@XmlElement
	public List<String> mateReferenceName;
	
	@XmlElement
	public List<Boolean> mateUnmappedFlag;
	
	@XmlElement
	public List<Boolean> notPrimaryAlignmentFlag;
	
	@XmlElement
	public List<Boolean> properPairFlag;
	
	@XmlElement
	public List<Integer> readLength;
	
	@XmlElement
	public List<String> readName;
	
	@XmlElement
	public List<Boolean> readNegativeStrandFlag;
	
	@XmlElement
	public List<Boolean> readPairedFlag;
	
	@XmlElement
	public List<String> readString;
	
	@XmlElement
	public List<Boolean> readUnmappedFlag;
	
	@XmlElement
	public List<Integer> referenceIndex;
	
	@XmlElement
	public List<String> referenceName;
	
	@XmlElement
	public List<Boolean> secondOfPairFlag;
	
	@XmlElement
	public List<Integer> unclippedEnd;
	
	@XmlElement
	public List<Integer> unclippedStart;
	
}