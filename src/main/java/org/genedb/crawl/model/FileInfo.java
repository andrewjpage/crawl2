package org.genedb.crawl.model;

import javax.xml.bind.annotation.XmlAttribute;

public class FileInfo  {
	
	@XmlAttribute(required=true)
	public Integer fileID;
	@XmlAttribute
	public String file;
	@XmlAttribute
	public String meta;
	@XmlAttribute
	public String organism;
	
	public FileInfo() {}
	
	public FileInfo(int fileID, String file, String meta, String organism) {
		this.fileID = fileID;
		this.file = file;
		this.meta = meta;
		this.organism = organism;
	}
	
	
}