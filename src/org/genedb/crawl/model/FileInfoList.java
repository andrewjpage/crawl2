package org.genedb.crawl.model;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("files")
public class FileInfoList {
	
	@XStreamImplicit
	public List<FileInfo> files = new ArrayList<FileInfo>();
	
}
