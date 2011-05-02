package org.genedb.crawl.model;

import javax.xml.bind.annotation.XmlAttribute;

public class BioDataFile {

	@XmlAttribute(required = true)
	public Integer fileID;
	
	@XmlAttribute(required = true)
	public String file;
	
	@XmlAttribute(required = true)
	public String organism;
	
	@XmlAttribute(required = false)
	public String meta;
	
	@Override
	public String toString() {
		return (String.format("%d : %s : %s", fileID, file, meta));
	}
	
}