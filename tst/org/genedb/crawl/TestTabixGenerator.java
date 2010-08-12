package org.genedb.crawl;

import java.io.File;

import org.genedb.crawl.business.TabixGenerator;

import junit.framework.TestCase;

public class TestTabixGenerator extends TestCase {
	public void test1() throws Exception {
		TabixGenerator g =  new TabixGenerator(new File("/Users/gv1/Desktop/Pfalciparum/artemis/GFF/Pfalciparum/1"), new File("/Users/gv1/Desktop/tabixed"));
	}
}
