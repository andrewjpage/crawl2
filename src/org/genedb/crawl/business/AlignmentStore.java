package org.genedb.crawl.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import net.sf.samtools.SAMFileReader;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AlignmentStore {
	
	private static Logger logger = Logger.getLogger(AlignmentStore.class);
	
	int fileID = 0;
	
	List<Alignment> alignments = new ArrayList<Alignment>();
	
	public SAMFileReader getReader(int fileID) {
		if (fileID < alignments.size()) {
			return alignments.get(fileID).getReader();
		}
		return null;
	}
	
	public List<Alignment> getAlignments() {
		return alignments;
	}
	
	public void generateMetaFields() {
		
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
	
	public void setAlignmentFiles(File alignmentFile) throws FileNotFoundException {
		
		logger.info(alignmentFile);
		
		final JsonParser parser = new JsonParser();

		final JsonElement jsonElement = parser.parse(new FileReader(alignmentFile));
		
		logger.info(jsonElement);
		
		JsonArray jAlignments = jsonElement.getAsJsonArray();
		
		
		
		for (JsonElement jAlignment : jAlignments) {
			
			if (jAlignment.isJsonObject()) {
				
				JsonObject jsonObject = jAlignment.getAsJsonObject();
				
				Alignment alignment = new Alignment();
				alignment.fileID = fileID++;
				alignments.add(alignment);
				
				for (final Entry<String, JsonElement> entry : jsonObject.entrySet()) {
					final String key = entry.getKey();
					final JsonElement value = entry.getValue();
					
					if (key.equals("name")) {
						alignment.name = value.getAsString();
					} else if (key.equals("file")) {
						alignment.file = new File(value.getAsString());
					} else if (key.equals("organism")) {
						alignment.organism = value.getAsString();
					} else if (key.equals("chromosome")) {
						
						JsonArray jChromosomes = value.getAsJsonArray();
						
						if (jChromosomes.size() == 1) {
							for (JsonElement jChromosome : jChromosomes ) {
								alignment.chromosomes.add(jChromosome.getAsString());
							}
						}
						
					}

				}
			}
			
		}
		
		generateMetaFields();
		
	}
	

	
}
