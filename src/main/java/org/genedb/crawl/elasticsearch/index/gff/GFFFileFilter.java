package org.genedb.crawl.elasticsearch.index.gff;

import java.io.File;
import java.io.FileFilter;

public class GFFFileFilter implements FileFilter {
	
	public GFFFileExtensionSet filter_set = GFFFileExtensionSet.ALL;
	
	public enum GFFFileExtensionSet {
		ZIPPED_ONLY 			(new String[] {".gff.gz"}),
		UNZIPPED_ONLY			(new String[] { ".gff"}),
		ALL 					(new String[] {".gff.gz", ".gff"});
		
		private String[] extensions;
		
		GFFFileExtensionSet(String[] extensions) {
			this.extensions = extensions;
		}
		
		public String[] getExtensions() {
			return extensions;
		}
		
	};
	
	@Override
	public boolean accept(File pathname) {
		
		String[] extensions = filter_set.getExtensions();
		
		for (String extension : extensions) {
			if (pathname.getName().endsWith(extension)) {
				return true;
			}
		}
		return false; 
	}

}
