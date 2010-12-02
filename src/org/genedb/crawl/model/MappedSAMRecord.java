package org.genedb.crawl.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import net.sf.samtools.AlignmentBlock;
import net.sf.samtools.CigarElement;
import net.sf.samtools.SAMRecord;

@XStreamAlias("record")
public class MappedSAMRecord {
	
	@XStreamOmitField
	private Logger logger = Logger.getLogger(MappedSAMRecord.class);
	
	public int alignmentStart;
	public int alignmentEnd;
	public int mappingQuality;
	
	public String baseQualities;
	public String readBases;
	
	public String readName;
	public int readLength;
	
	public MappedFlags flags;
	
	public List<MappedCigarElement> cigars = new ArrayList<MappedCigarElement>();
	public List<MappedAlignmentBlock> alignmentBlocks = new ArrayList<MappedAlignmentBlock>();
	
	public MappedSAMRecord(SAMRecord record) {
		
		alignmentStart = record.getAlignmentStart();
		alignmentEnd = record.getAlignmentEnd();
		mappingQuality = record.getMappingQuality();
		
		readName = record.getReadName();
		readLength = record.getReadLength();
		
		baseQualities = record.getBaseQualityString();
		readBases = record.getReadString();
		
		flags = new MappedFlags(record);
		
		for (CigarElement ce : record.getCigar().getCigarElements()) {
			MappedCigarElement mce = new MappedCigarElement();
			mce.length = ce.getLength();
			mce.operator = ce.getOperator().toString();
			cigars.add(mce);
		}
		
		for (AlignmentBlock ab : record.getAlignmentBlocks()) {
			MappedAlignmentBlock mab = new MappedAlignmentBlock();
			mab.readStart = ab.getReadStart();
			mab.referenceStart = ab.getReferenceStart();
			mab.length = ab.getLength();
			alignmentBlocks.add(mab);
		}
		
		logger.debug("Added " + readName);
		
	}
	
	public String toString() {
		return readName;
	}
	
}

@XStreamAlias("flags")
class MappedFlags {
	
	public boolean readPairedFlag;
	public boolean properPairFlag;
	
	public boolean readUnmappedFlag;
	public boolean mateUnmappedFlag;
	
	public boolean readNegativeStrandFlag;
	public boolean mateNegativeStrandFlag;
	
	public boolean firstOfPairFlag;
	
	public boolean secondOfPairFlag;
	
	public boolean notPrimaryAlignmentFlag;
	public boolean readFailsVendorQualityCheckFlag;
	public boolean duplicateReadFlag;
	
	
	public MappedFlags(SAMRecord record) {
		
		readPairedFlag = record.getReadPairedFlag();
		properPairFlag = record.getProperPairFlag();
		readUnmappedFlag = record.getReadUnmappedFlag();
		mateUnmappedFlag = record.getMateUnmappedFlag();
		readNegativeStrandFlag = record.getReadNegativeStrandFlag();
		mateNegativeStrandFlag = record.getMateNegativeStrandFlag();
		firstOfPairFlag = record.getFirstOfPairFlag();
		secondOfPairFlag = record.getSecondOfPairFlag();
		notPrimaryAlignmentFlag = record.getNotPrimaryAlignmentFlag();
		readFailsVendorQualityCheckFlag = record.getReadFailsVendorQualityCheckFlag();
		duplicateReadFlag = record.getDuplicateReadFlag();
		
	}
	
}


@XStreamAlias("cigar")
class MappedCigarElement {
	public int length;
	public String operator;
}

@XStreamAlias("alignmentBlock")
class MappedAlignmentBlock {
	public int readStart;
	public int referenceStart;
	public int length;
}
