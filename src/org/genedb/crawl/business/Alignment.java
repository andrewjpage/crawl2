/**
 * 
 */
package org.genedb.crawl.business;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sf.samtools.SAMFileReader;

class Alignment {
	
	public int fileID;
	public File file;
	public String organism;
	public String name;
	public List<String> chromosomes = new ArrayList<String>();
	public String meta;
	
	private SAMFileReader reader;
	
	public SAMFileReader getReader() {
		if (reader == null) {
			reader = new SAMFileReader(file);
		}
		return reader;
	}
	
}