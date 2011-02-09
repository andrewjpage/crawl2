package org.genedb.crawl.elasticsearch.index.gff;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.genedb.crawl.business.FileUtil;
import org.genedb.crawl.elasticsearch.index.IndexBuilder;
import org.genedb.crawl.model.Feature;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;


public class GFFIndexBuilder extends IndexBuilder {
	
	private Logger logger = Logger.getLogger(GFFIndexBuilder.class);
	
	@Option(name = "-g", aliases = {"--gffs"}, usage = "The path to the GFF folder", required = true)
	public String gffs;
	
	@Option(name = "-t", aliases = {"--tmp"}, usage = "The path to a tmp folder folder")
	public String tmp = "/tmp/crawl";
	
	
	public GFFIndexBuilder() {
		super();
		
	}
	
	public void run() throws IOException {
		
		setupIndex();
		
		List<Feature> features = convert();
		sendFeaturesToIndex(features);
		logger.debug("Complete");
		
	}
	
	List<Feature> convert() throws IOException {
		File gffFile = new File(gffs);
		
		File tmpFolder = new File(tmp);
		tmpFolder.mkdirs();
		
		List<Feature> features = new ArrayList<Feature>();
		
		if (gffFile.isDirectory()) {
			for (File f : gffFile.listFiles()) {
				features.addAll(convert(f, tmpFolder));
			}
		} else {
			features.addAll(convert(gffFile, tmpFolder));
		}
		return features;
	}
	
	List<Feature> convert(File gffFile, File tmpFolder) throws IOException {
		System.out.println(String.format("Converting %s using tmp folder %s", gffFile.getName(), tmpFolder.getPath()));
		
		String newFilePath = tmpFolder + File.pathSeparator + gffFile.getName();
		FileUtil.copy(gffFile.getAbsolutePath(), newFilePath);
		gffFile = new File(newFilePath);
		
		if (gffFile.getName().endsWith("gz")) {
			String gunzippedFileName = FileUtil.unzip(gffFile.getAbsolutePath());
			gffFile = new File(gunzippedFileName);
		}
		
		GFFFileToFeatureListConverter converter = new GFFFileToFeatureListConverter(gffFile, tmpFolder);
		return converter.features;
	}
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
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
			System.out.println(e.getMessage());
            parser.setUsageWidth(80);
            parser.printUsage(System.out);
            System.exit(1);
		} finally {
			
			gffIndexBuilder.closeIndex();
		}
		 
	}

}
