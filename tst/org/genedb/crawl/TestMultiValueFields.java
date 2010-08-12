package org.genedb.crawl;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import junit.framework.TestCase;

public class TestMultiValueFields extends TestCase {
	
	private Logger logger = Logger.getLogger(TestMultiValueFields.class);
	
	public void test1() throws IOException {
		
		FSDirectory dir = FSDirectory.open(new File("/Users/gv1/Desktop/tabix test/lucene"));
		IndexReader reader =  IndexReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		
		logger.info(dir.toString());
		
		Term t = new Term("ID", "LmjF21.0130:pep");
		TermQuery tq = new TermQuery(t);
		
		TopDocs td = searcher.search(tq, 10);
		
		logger.info(td.totalHits);
		
		for (ScoreDoc sd : td.scoreDocs) {
			
			Document d = reader.document(sd.doc);
			
			logger.info(d.getField("ID"));
			
			logger.info(d.getField("gO"));
			
			Field[] f = d.getFields("gO");
			
			logger.info(f.length);
			
			
		}
		
	}
	
}
