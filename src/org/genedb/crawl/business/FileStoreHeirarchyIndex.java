package org.genedb.crawl.business;

import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import net.sf.samtools.SAMFileReader;

import org.apache.log4j.Logger;
import org.genedb.crawl.model.FileInfo;

public class FileStoreHeirarchyIndex implements HeirarchyIndex {
	
	private Logger logger = Logger.getLogger(FileStoreHeirarchyIndex.class);
	private String fileStorePath;
	
	private int indexPosition = 1;
	private Hashtable<Integer, FileInfo> fileStore = new Hashtable<Integer, FileInfo>();
	
	public void setFileStorePath(String fileStorePath) {
		
		try {
			this.fileStorePath = fileStorePath;
			logger.debug(this.fileStorePath);
			
			File dir = new File(this.fileStorePath);
			
			recurseHeirarchy(dir);
			
			makeUniqueMetaPaths();
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}
	
	private void makeUniqueMetaPaths() {
		
		Set<String> found = new HashSet<String>();
		
		Set<String> uniques = new HashSet<String>();
		
		for (FileInfo fi : this) {
			
			String[] metas = fi.getFile().getAbsolutePath().split("/");
			
			logger.debug(metas);
			
			for (String meta : metas) {
				if (found.contains(meta)) {
					
					if (uniques.contains(meta)) {
						uniques.remove(meta);
					}
					
					continue;
				}
				
				found.add(meta);
				uniques.add(meta);
				
			}
		}
		
		logger.debug("Found");
		StringBuffer sb = new StringBuffer();
		for (String f : found) {
			sb.append(f + ",");
		}
		logger.debug(sb.toString());
		
		logger.debug("Unique");
		StringBuffer sb2 = new StringBuffer();
		for (String u : uniques) {
			sb2.append(u + ",");
		}
		logger.debug(sb2.toString());
		
		for (FileInfo fi : this) {
			
			StringBuffer newMeta = new StringBuffer();
			
			String[] metas = fi.getFile().getAbsolutePath().split("/");
			String appendage = "";
			for (String meta : metas) {
				if (uniques.contains(meta)) {
					newMeta.append(meta + appendage);
					appendage = " > ";
				}
			}
			
			fi.setMeta(newMeta.toString());
			
		}
		
		
	}
	
	
	private void recurseHeirarchy(File dir) {
		for (String fileName : dir.list()) {
			
			File file = new java.io.File(dir.getAbsolutePath() + "/" + fileName);
			
			String meta = dir.getAbsolutePath().replace(fileStorePath, "");
			
			
			
			if ( (file.isDirectory()) ) {
				recurseHeirarchy(file);
			} else {
				
				if (fileName.endsWith("bam")) {
					logger.debug("Adding bam! " + file.getAbsolutePath());
					this.fileStore.put(indexPosition, new FileInfo(indexPosition, file, meta));
					indexPosition++;
				}				
			}
		}
	}
	
	public SAMFileReader getSamOrBam(int fileID) {
		
		if (fileStore.containsKey(fileID)) {
			return fileStore.get(fileID).getReader(); 
		}
		
		return null;
	}

	@Override
	public Iterator<FileInfo> iterator() {
		return fileStore.values().iterator();
	}
	
	
}




