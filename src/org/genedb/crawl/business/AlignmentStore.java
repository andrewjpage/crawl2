package org.genedb.crawl.business;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import net.sf.samtools.SAMFileReader;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.type.TypeReference;
import org.genedb.crawl.elasticsearch.json.JsonIzer;
import org.genedb.crawl.model.Alignment;
import org.springframework.util.StringUtils;


public class AlignmentStore {
	
	private static Logger logger = Logger.getLogger(AlignmentStore.class);
	
	Integer fileID = 0;
	
	List<Alignment> alignments = new ArrayList<Alignment>();
	
	private JsonIzer jsonIzer = JsonIzer.getJsonIzer();
	
	public SAMFileReader getReader(int fileID) {
		if (fileID < alignments.size()) {
			return alignments.get(fileID).getReader();
		}
		return null;
	}
	
	public List<Alignment> getAlignments() {
		return alignments;
	}
	
	
	
	public void setAlignmentFiles(File alignmentFile) throws JsonParseException, JsonMappingException, IOException {
		
		logger.info(String.format("Alignment file : %s" , alignmentFile));
		
		if (alignmentFile == null) {
			return;
		}
		
		if (alignmentFile.isFile() == false) {
			return;
		}
		
		alignments = (List<Alignment>) jsonIzer.fromJson(alignmentFile,  new TypeReference<List<Alignment>>() {} );
		
		generateMetaFields();
		assignFileIDs();
	}
	
	void generateMetaFields() {
		
		Set<String> found = new HashSet<String>();
		Set<String> uniques = new HashSet<String>();
		
		for (Alignment alignment : alignments) {
			
			String[] metas = alignment.file.getAbsolutePath().split("/");
			
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
		
		for (Alignment alignment : alignments) {
			
			String[] metas = alignment.file.getAbsolutePath().split("/");
			
			List<String> path_elements = new ArrayList<String>();
			
			for (String meta : metas) {
				if (uniques.contains(meta)) {
					path_elements.add(meta);
				}
			}
			
			alignment.meta = StringUtils.collectionToDelimitedString(path_elements, " > "); 
			
		}
		
	}

	void assignFileIDs() {
		for (Alignment alignment : alignments) {
			alignment.fileID = fileID++;
		}
	}

	
}
