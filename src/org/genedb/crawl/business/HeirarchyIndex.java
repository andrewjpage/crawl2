package org.genedb.crawl.business;

import org.genedb.crawl.model.FileInfo;

import net.sf.samtools.SAMFileReader;

public interface HeirarchyIndex extends Iterable<FileInfo>  {
	public SAMFileReader getSamOrBam(int fileID);
}