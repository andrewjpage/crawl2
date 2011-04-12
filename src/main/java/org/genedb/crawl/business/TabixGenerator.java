package org.genedb.crawl.business;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;


import org.apache.log4j.Logger;


public class TabixGenerator {
	
	private Logger logger = Logger.getLogger(TabixGenerator.class);
	
	private File inputFolder;
	private File outputFolder;
	
	private Runtime run = Runtime.getRuntime();
	
	class GFFFileMap {
		public File fasta;
		public File annotations; 
	}
	
	public TabixGenerator(File inputFolder, File outputFolder) throws Exception {
		
		if (! inputFolder.isDirectory() || ! outputFolder.isDirectory()) {
			throw new Exception("Both the input and output should be folders");
		}
		
		this.inputFolder = inputFolder;
		this.outputFolder = outputFolder;
		
		for (File inputFile : this.inputFolder.listFiles(new GFFFileFilter())) {
			//logger.info("Tabixing " + inputFile);
			
			GFFFileMap map = extractAnnotationsAndSequence( unzipIfZipped( copyFileToDestination( inputFile )));
			
			File sortedFile = sortFileColumns(map.annotations);
			
			indexAnnotations(sortedFile);
			indexFasta(map.fasta);
			
		}
		
	}
	
	private File copyFileToDestination(File file) throws IOException, InterruptedException {
		
		BufferedReader buf = null;
		
		try {
		
			String cmd = String.format("cp %s %s", file.getAbsolutePath(), outputFolder);
			logger.info(cmd);
			
			Process pr = run.exec(cmd);
			pr.waitFor();
			
			buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line = "";
			while ((line=buf.readLine())!=null) {
				logger.info(line);
			}
			
		} finally {
			if (buf != null) {
				buf.close();
			}
		}
		
		return new File(outputFolder.getAbsolutePath() + "/" + file.getName());
		
	}
	
	private File unzipIfZipped(File file) throws IOException, InterruptedException {
		
		BufferedReader buf = null;
		
		try {
		
			if (file.getName().endsWith(".gz")) {
				
				//logger.info("Unzipping " + file.getName());
				
				String cmd = String.format("gunzip %s", file.getAbsolutePath());
				logger.info(cmd);
				
				Process pr = run.exec(cmd);
				pr.waitFor();
				
				buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				String line = "";
				while ((line=buf.readLine())!=null) {
					logger.info(line);
				}
				
				String newFileName = file.getName().substring(0, file.getName().length() - 3);
				//logger.info(newFileName);
				
				return new File(file.getParent() + "/" + newFileName);
	
			}
		
		} finally {
			buf.close();
		}
		
		return file;
	}
	
	private GFFFileMap extractAnnotationsAndSequence(File file) throws IOException  {
		
		BufferedWriter fastaWriter = null;
		BufferedWriter annotationWriter = null;
		BufferedReader buf = null;
		
		String fastaFileName = file.getParent() + "/" + file.getName().substring(0, file.getName().length() - 4) + ".fasta";
		String annotationFileName = file.getParent() + "/" + file.getName().substring(0, file.getName().length() - 4) + ".annotations";
		
		try {
			
			//logger.debug(file.getAbsolutePath());
			
			buf = new BufferedReader(new FileReader(file.getAbsolutePath()));
			
			String line = "";
			
			fastaWriter = new BufferedWriter(new FileWriter(fastaFileName));
			annotationWriter = new BufferedWriter(new FileWriter(annotationFileName));
			
			boolean parsingAnnotations = true;
			
			while ((line=buf.readLine())!=null) {
				//logger.debug(line);
				
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
		
		GFFFileMap map = new GFFFileMap();
		map.fasta = new File(fastaFileName);
		map.annotations = new File(annotationFileName);
		return map;
		
	}
	
	
	private File sortFileColumns(File file) throws IOException, InterruptedException {
		
		BufferedReader buf = null;
		
		try {
			
			//logger.info("Unzipping " + file.getName());
			
			// grep -v ^"#" $TMPDIR/no_sequence_$INFILE.gff | sort -k1,1 -k4,4n | bgzip > $OUTDIR/$FILENAME.gff.gz
			// have to run this as a shell command, because of the pipe
			String[] cmd = {
				"/bin/bash",
				"-c",
				String.format("sort -k1,1 -k4,4n %s | /Users/gv1/bin/tabix/bgzip > %s.gz", file.getAbsolutePath(), file.getAbsolutePath())
			};
			
			logger.info(Arrays.deepToString(cmd));
			
			Process pr = run.exec(cmd);
			pr.waitFor();
			
			buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line = "";
			while ((line=buf.readLine())!=null) {
				logger.info(line);
			}
			

			return new File(file.getAbsolutePath() + ".gz");
		
		} finally {
			buf.close();
		}
	}
	
	private File indexFasta(File file) throws IOException, InterruptedException {
		BufferedReader buf = null;
		
		try {
			
			logger.info("Indexing the fasta " + file.getName());
			
			String[] cmd = {
					"/Users/gv1/bin/samtools/samtools",
					"faidx",
					file.getAbsolutePath()
				};
			
			
			logger.info(Arrays.deepToString(cmd));
			
			Process pr = run.exec(cmd);
			pr.waitFor();
			
			
			buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line = "";
			while ((line=buf.readLine())!=null) {
				logger.info(line);
			}
			
			return new File(file.getAbsolutePath() + ".fai");
		
		} finally {
			if (buf != null) {
				buf.close();
			}
		}
		
	}
	
	private File indexAnnotations(File file) throws IOException, InterruptedException {
		BufferedReader buf = null;
		
		try {
			
			logger.info("Indexing the annotations " + file.getAbsolutePath());
			
			String[] cmd = {
				"/Users/gv1/bin/tabix/tabix",
				"-p",
				"gff",
				"-f",
				file.getAbsolutePath()
			};
			
			logger.info(Arrays.deepToString(cmd));
			
			ProcessBuilder processBuilder = new ProcessBuilder(cmd);
			//logger.info(processBuilder.command());
			
			
			Process process = processBuilder.start();
			buf = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			String line = "";
			while ((line=buf.readLine())!=null) {
				logger.info(line);
			}
			
			return new File(file.getAbsolutePath() + ".tbi");
		
		} finally {
			if (buf != null) {
				buf.close();
			}
		}
		
	}
	
	
	
	static class GFFFileFilter implements FileFilter {
		
		public static final String[] extensions = {".gff.gz", ".gff"};
		
		@Override
		public boolean accept(File pathname) {
			
			for (String extension : extensions) {
				if (pathname.getName().endsWith(extension)) {
					return true;
				}
			}
			
			return false; 
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		if (args.length != 2) {
			throw new Exception("Please supply an input folder and an output folder");
		}
		TabixGenerator g =  new TabixGenerator(new File(args[0]), new File(args[1]));
		
	}
	
}
