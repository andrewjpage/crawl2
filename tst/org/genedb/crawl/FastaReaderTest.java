package org.genedb.crawl;

import java.io.File;

import junit.framework.TestCase;

import net.sf.picard.reference.FastaSequenceFile;
import net.sf.picard.reference.FastaSequenceIndex;
import net.sf.picard.reference.IndexedFastaSequenceFile;
import net.sf.picard.reference.ReferenceSequence;
import net.sf.samtools.util.StringUtil;

public class FastaReaderTest extends TestCase {
	
	public void testFasta() {
		
		File fastaFile = new File("/Users/gv1/Desktop/tabix test/out/Pf3D7_01.fasta");
		
		FastaSequenceFile fastaReader = new FastaSequenceFile(fastaFile, true);
		
		final ReferenceSequence referenceSequence = fastaReader.nextSequence();
		
		
		System.out.println(fastaReader.toString()+ " " + fastaReader.isIndexed() + " " + referenceSequence.getName() + " " + StringUtil.bytesToString(referenceSequence.getBases()));
		

//		System.out.println(fastaFile.getAbsolutePath() + ".fai");
//		
		
//		
//		System.out.println(fastaFile.exists());
//		System.out.println(indexFile.exists());
//		
//		
		
		//FastaSequenceIndex index = new FastaSequenceIndex(indexFile);
		
		File indexFile =  new File("/Users/gv1/Desktop/tabix test/out/Pf3D7_01.fasta");
		IndexedFastaSequenceFile indexedFastaReader = new IndexedFastaSequenceFile(indexFile);
		
		
		
		
		//IndexedFastaSequenceFile indexedFastaReader = new IndexedFastaSequenceFile(fastaFile, index);
		
		final ReferenceSequence indexedReferenceSequence = indexedFastaReader.nextSequence();
		
		System.out.println(indexedReferenceSequence.getName());
		
		System.out.println(StringUtil.bytesToString(indexedFastaReader.getSubsequenceAt(indexedReferenceSequence.getName(), 1, 1000).getBases()));
		
		System.out.println(StringUtil.bytesToString(indexedFastaReader.getSubsequenceAt(indexedReferenceSequence.getName(), 1, 1000).getBases()).length());
		
		//System.out.println(indexedReferenceSequence.getName());
		
//		
//		
//		
//		//index.
//		
//		 
//		
//		System.out.println(iFile.getSequenceDictionary());
		
		//System.out.println(IndexedFastaSequenceFile.canCreateIndexedFastaReader(fastaFile));		
		
		//IndexedFastaSequenceFile iFile = new IndexedFastaSequenceFile(fastaFile, indexFile);
		
		//(iFile.getSubsequenceAt("Pf3D7_01", 1, 100));
		
	}
	
}
