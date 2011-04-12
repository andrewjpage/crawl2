/**
 * 
 */
package org.genedb.crawl.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sf.samtools.SAMFileReader;

public class Alignment {
	
	public Integer fileID;
	public File file;
	public String organism;
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