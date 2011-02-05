package org.genedb.crawl.business;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.genedb.crawl.business.GFFFileFilter.GFFFileExtensionSet;

public class GFFAnnotatationAndFastaExtractor {
	
	private static Logger logger = Logger.getLogger(GFFAnnotatationAndFastaExtractor.class);
	
	private File file;
	private File destinationFolder;
	
	String fastaFileName;
	String annotationFileName;
	
	
	public File getAnnotationFile() {
		logger.info(String.format("Return annoation file %s", annotationFileName));
		return new File(annotationFileName);
	}
	
	public File getFastaFile() {
		return new File(fastaFileName);
	}
	
	public GFFAnnotatationAndFastaExtractor(File file, File destinationFolder) throws IOException {
		this.file = file;
		this.destinationFolder = destinationFolder;
	
		
		if (! file.exists()) {
			throw new FileNotFoundException("Could not find file " + file);
		}
		
		if (! destinationFolder.isDirectory()) {
			throw new IOException(String.format("The destination folder %s is not a directory.", destinationFolder.getAbsolutePath()) );
		}
		
		
		if (! file.isDirectory() && file.getName().endsWith(".gff")) {
			extractAnnotationsAndSequence(file, destinationFolder);
		} else {
			throw new IOException("GFFAnnotatationAndFastaExtractor can't process a directory.");
		}
		
//		} else if (! file.isDirectory() && file.getName().endsWith(".gz")) {
//			
//			
//			
//			
//		} else { 
//			GFFFileFilter filter = new GFFFileFilter();
//			filter.filter_set = GFFFileExtensionSet.UNZIPPED_ONLY;
//			for (File child : file.listFiles(filter)) {
//				extractAnnotationsAndSequence(child, destinationFolder);
//			}
//		}
		
	}
	
	private void extractAnnotationsAndSequence(File file, File destinationFolder) throws IOException  {
		
		BufferedWriter fastaWriter = null;
		BufferedWriter annotationWriter = null;
		BufferedReader buf = null;
		
		fastaFileName = destinationFolder.getAbsolutePath() + "/" + file.getName().substring(0, file.getName().length() - 4) + ".fasta";
		annotationFileName = destinationFolder.getAbsolutePath() + "/" + file.getName().substring(0, file.getName().length() - 4) + ".gff";
		
		try {
			
			// logger.debug(file.getAbsolutePath());
			
			buf = new BufferedReader(new FileReader(file.getAbsolutePath()));
			
			String line = "";
			
			fastaWriter = new BufferedWriter(new FileWriter(fastaFileName));
			annotationWriter = new BufferedWriter(new FileWriter(annotationFileName));
			
			boolean parsingAnnotations = true;
			
			while ((line=buf.readLine())!=null) {
				// logger.debug(line);
				
				if (line.contains("##FASTA")) {
					parsingAnnotations = false;
				}
				
				if (line.startsWith("#")) {
					continue;
				}
				
				if (parsingAnnotations) {
					annotationWriter.write(line + "\n");
				} else {
					fastaWriter.write(line + "\n");
				}
				
			}
			
			
		
		} finally {
			
			if (buf != null) {
				buf.close();
			}
			
			if (fastaWriter != null) {
				fastaWriter.close();
			}
			
			if (annotationWriter != null) {
				annotationWriter.close();
			}
			
			
			
		}
		
		logger.info("Annotation: " + annotationFileName);
		logger.info("Fasta: " + fastaFileName);
		
	}
	
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		
		if (args.length < 2) {
			logger.error("You must supply a path to a GFF file or folder, followed by a destination folder");
			System.exit(1);
		}
		
		File file = new File (args[0]);
		File destinationFolder = new File (args[1]);
		
		
		try {
			GFFAnnotatationAndFastaExtractor extractor = new GFFAnnotatationAndFastaExtractor(file, destinationFolder);
			
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			System.exit(1);
		}
	}
	
	
}
