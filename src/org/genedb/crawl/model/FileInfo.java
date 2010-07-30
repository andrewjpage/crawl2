package org.genedb.crawl.model;

import java.io.File;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import net.sf.samtools.SAMFileReader;

@XStreamAlias("file")
public class FileInfo extends BaseResult {
	
	private int id;
	
	@XStreamAlias("path")
	private File file;
	
	@XStreamOmitField
	private SAMFileReader reader;
	
	private String meta;
	
	public FileInfo(int id, File file, String meta) {
		this.id = id;
		this.file = file;
		this.meta = meta;
	}
	
	public File getFile() {
		return file;
	}
	
	public int getId() {
		return id;
	}
	
	public String getMeta() {
		return meta;
	}
	
	public void setMeta(String meta) {
		this.meta = meta;
	}
	
	public SAMFileReader getReader() {
		if (reader == null) {
			reader = new SAMFileReader(file);
		}
		return reader;
	}
	
}