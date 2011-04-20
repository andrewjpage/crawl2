package org.genedb.crawl.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.genedb.crawl.model.adapter.AlignmentBlockAdapter;
import org.genedb.crawl.model.adapter.AlignmentBlockAdapterList;

public class Records {
	
//	public static class AlignmentBlockAdapterListAdapter extends XmlAdapter<AlignmentBlockAdapterList, ArrayList<Map<String,Integer>>> {
//
//		@Override
//		public AlignmentBlockAdapterList marshal(
//				ArrayList<Map<String, Integer>> list) throws Exception {
//			
//			for (AlignmentBlockAdapter block : list) {
//				
//			}
//			
//			return null;
//		}
//
//		@Override
//		public ArrayList<Map<String, Integer>> unmarshal(
//				AlignmentBlockAdapterList v) throws Exception {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//	
//		
//
//		
//	}
	
	//@XmlJavaTypeAdapter(AlignmentBlockAdapterListAdapter.class)
	
	//@XmlElement(type=AlignmentBlockAdapterList.class)
	
	@XmlElement
	public AlignmentBlockAdapter[][] alignmentBlocks;
	
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
	
	@XmlElement
	public List validationStringency;
	
}