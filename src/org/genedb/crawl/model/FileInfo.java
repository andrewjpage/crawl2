package org.genedb.crawl.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("file")
public class FileInfo  {
	
	public int fileID;
	public String file;
	public String meta;
	public String organism;
	
	public FileInfo(int fileID, String file, String meta, String organism) {
		this.fileID = fileID;
		this.file = file;
		this.meta = meta;
		this.organism = organism;
	}
	
	
}