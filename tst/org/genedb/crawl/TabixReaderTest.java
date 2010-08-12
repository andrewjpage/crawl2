package org.genedb.crawl;

import java.io.IOException;

import org.genedb.crawl.business.TabixReader;

import junit.framework.TestCase;

public class TabixReaderTest extends TestCase {
	
	public void testTabixReader() throws IOException {
		
		String fName = "/Users/gv1/Desktop/tabixed/Pf3D7_01.annotations.gz";
		
		TabixReader reader = new TabixReader(fName);
		//TabixReader.Iterator iter = reader.query("Pf3D7_01:5000-100000");
		TabixReader.Iterator iter = reader.query("Pf3D7_01");
		
		
		String line;
		while((line=iter.next()) != null) {
			System.out.println(line);
		}
	}
	
}
