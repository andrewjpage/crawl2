package org.genedb.crawl.business;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import org.genedb.crawl.business.GFFFileFilter.GFFFileExtensionSet;

public class AnnotationRepository {
	
	private File luceneDirectory;
	private IndexReader reader;
	
	private File tabixDirectory;
	
	private Map<String, TabixReaderInfo> tabixReaderInfos = new HashMap<String, TabixReaderInfo>();
	
	public void setLuceneDirectory(String dir) throws IOException {
		File luceneDirectory = new File(dir);
		
		if (! luceneDirectory.isDirectory()) {
			throw new IOException("The " + dir + " is not a folder.");
		}
		this.luceneDirectory = luceneDirectory;
		
		FSDirectory fsdir = FSDirectory.open(this.luceneDirectory);
		reader =  IndexReader.open(fsdir);
	}
	
	public void setTabixDirectory(String dir) throws IOException {
		File tabixDirectory = new File(dir);
		
		if (! tabixDirectory.isDirectory()) {
			throw new IOException("The " + dir + " is not a folder.");
		}
		this.tabixDirectory = tabixDirectory;
		
		GFFFileFilter filter = new GFFFileFilter();
		filter.filter_set = GFFFileExtensionSet.ZIPPED_ONLY;
		
		for (File file : this.tabixDirectory.listFiles(filter)) {
			TabixReaderInfo readerInfo = new TabixReaderInfo(file);
			tabixReaderInfos.put(readerInfo.getName(), readerInfo);
			//System.out.println("Loading info " + readerInfo.getName());
		}
		
	}
	
	public TabixReaderInfo getTabixReaderInfo (String name) {
		
		//System.out.println("Searching for '" + name + "'");
		
		return tabixReaderInfos.get(name);
	}
	
	public IndexReader luceneIndexReader() {
		return reader;
	}
	
	
	
}
