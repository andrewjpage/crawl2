package org.genedb.crawl;

import java.io.File;
import java.io.IOException;

import org.genedb.crawl.business.GFFIndexer;

import junit.framework.TestCase;

public class GFFIndexerTest extends TestCase {
	
	public void testGFFIndex() throws IOException {
		
		File luceneDirectory =  new File ("/Users/gv1/Desktop/tabix test/lucene");
		
		GFFIndexer indexer = new GFFIndexer();
		indexer.setIndexDirectory(luceneDirectory);
		
		//File gffExample = new File ("/Users/gv1/Desktop/tabix test/out/Pf3D7_01.gff.gz");
		File gffExample = new File ("/Users/gv1/Desktop/Pfalciparum/artemis/GFF/Pfalciparum/1/Pf3D7_01.gff");
		
		indexer.indexFile(gffExample);
		
		indexer.closeIndex();
		
		System.out.println(indexer);
		
	}

}
