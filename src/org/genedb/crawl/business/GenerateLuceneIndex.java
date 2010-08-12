package org.genedb.crawl.business;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.log4j.Logger;

import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.Cli;
import uk.co.flamingpenguin.jewel.cli.CliFactory;
import uk.co.flamingpenguin.jewel.cli.Option;

public class GenerateLuceneIndex {
	
	private Logger logger = Logger.getLogger(GenerateLuceneIndex.class);
	
	private String tabixFolder;
	private String luceneFolder;
	
	public void setTabixFolder(String tabixFolder) {
		this.tabixFolder = tabixFolder;
	}
	
	public String getTabixFolder() {
		return tabixFolder;
	}
	
	public void setLuceneFolder(String luceneFolder) {
		this.luceneFolder = luceneFolder;
	}
	
	public String getLuceneFolder() {
		return luceneFolder;
	}
	
	public static class GFFFilter implements FileFilter {
		public static final String extension = ".gff.gz";
		@Override
		public boolean accept(File pathname) {
			return pathname.getName().endsWith(extension);
		}
	}
	
	public void run() throws IOException {
		
		File tabixes = new File(tabixFolder);
		File lucenes = new File(luceneFolder);
		
		GFFIndexer indexer = new GFFIndexer();
		logger.info("Setting index directory " + lucenes);
		indexer.setIndexDirectory(lucenes);
		
		logger.info(tabixes);
		
		
		for (File inputFile : tabixes.listFiles(new GFFFilter())) {
			logger.info("Indexing " + inputFile);
			indexer.indexFile(inputFile);
		}
		
		logger.info("Closing");
		indexer.closeIndex();
		
	}
	
	/**
	 * @param args
	 * @throws ArgumentValidationException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws ArgumentValidationException, IOException {
		
		Cli<IGenerateLuceneIndex> cli = CliFactory.createCli(IGenerateLuceneIndex.class);
		IGenerateLuceneIndex cliArgs = cli.parseArguments(args);
		
		GenerateLuceneIndex generator = new GenerateLuceneIndex();
		generator.setLuceneFolder(cliArgs.getLuceneFolder());
		generator.setTabixFolder(cliArgs.getTabixFolder());
		generator.run();
		
	}
	
	interface IGenerateLuceneIndex {
		
	    @Option(shortName="t", description="The folder containing the tabix files.")
        String getTabixFolder();
        void setTabixFolder(String tabixFolder);
        
        @Option(shortName="l", description="The folder containing lucene index.")
        String getLuceneFolder();
        void setLuceneFolder(String luceneFolder);
        
	}
	
}
