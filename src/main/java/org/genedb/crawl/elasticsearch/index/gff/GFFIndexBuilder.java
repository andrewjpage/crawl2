package org.genedb.crawl.elasticsearch.index.gff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;
import org.genedb.crawl.elasticsearch.index.NonDatabaseDataSourceIndexBuilder;
import org.genedb.crawl.model.Organism;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;


public class GFFIndexBuilder extends NonDatabaseDataSourceIndexBuilder {
	
	static Logger logger = Logger.getLogger(GFFIndexBuilder.class);
	
	@Option(name = "-g", aliases = {"--gffs"}, usage = "The path to the GFF folder", required = false)
	public String gffs;
		
	public void run() throws IOException, ParseException, SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		
		init();
		
		Organism o = getAndPossiblyStoreOrganism();
		
		if (gffs != null) {
			convertPath(gffs, o);
		}
		
		logger.debug("Complete");
		
	}
	
	private void convertPath(String path, Organism organism) throws ParseException, IOException {
		
		File gffFile = new File(path);
		
		GFFFileFilter filter = new GFFFileFilter();
		filter.filter_set = GFFFileFilter.GFFFileExtensionSet.ALL;
		
		if (gffFile.isDirectory()) {
			for (File f : gffFile.listFiles(filter)) {
				convertFile(f, organism);
				f = null;
			}
		} else {
			convertFile(gffFile, organism);
			gffFile = null;
		}
		
	}
	
	private void convertFile(File gffFile, Organism organism) throws ParseException, IOException {
		BufferedReader reader = getReader(gffFile);
		new GFFAnnotatationAndFastaExtractor(reader, organism, featureMapper, regionsMapper);
	}
	

	private BufferedReader getReader(File file) throws IOException {
		
		BufferedReader reader = null;
		
		FileInputStream fileStream = new FileInputStream(file);
		
		if (file.getName().endsWith("gz")) {
			logger.info("unzipping");
		    reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(fileStream)));
		} else {
			reader = new BufferedReader(new InputStreamReader(fileStream));
		}
		
		return reader;
	}
	
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws IllegalAccessException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 */
	public static void main(String[] args) throws IOException, ParseException, SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		GFFIndexBuilder gffIndexBuilder = new GFFIndexBuilder();
		CmdLineParser parser = new CmdLineParser(gffIndexBuilder);
		
		try {
			
			parser.parseArgument(args);
		
			if (gffIndexBuilder.help) {
				parser.setUsageWidth(80);
	            parser.printUsage(System.out);
	            System.exit(1);
			}
			
			gffIndexBuilder.run();
		
		} catch (CmdLineException e) {
			logger.error(e.getMessage());
            parser.setUsageWidth(80);
            parser.printUsage(System.out);
            System.exit(1);
		} finally {
			
			gffIndexBuilder.closeIndex();
		}
		 
	}

}
