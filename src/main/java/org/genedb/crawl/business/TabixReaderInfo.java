package org.genedb.crawl.business;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;

public class TabixReaderInfo {
	
	private File tabixFile;
	private TabixReader reader;
	
	public TabixReaderInfo (File tabixFile) throws IOException {
		this.setTabixFile(tabixFile);
	}
	
	public void setTabixFile(File tabixFile) throws IOException {
		this.tabixFile = tabixFile;
		reader = new TabixReader(this.tabixFile.getAbsolutePath());
	}
	
	public String[] getChrs() {
		return reader.getChrs();
	}
	
	public TabixReader getReader() {
		return reader;
	}
	
	public String getName() {
		return StringUtils.join(getChrs(), " ");
	}
	
}
