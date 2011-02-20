package org.genedb.crawl.business;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

public class FileUtil {
	
	public static String unzip(String inFilePath) throws FileNotFoundException, IOException
	{
	    GZIPInputStream gzipInputStream = new GZIPInputStream(new FileInputStream(inFilePath));
	 
	    String outFilePath = inFilePath.replace(".gz", "");
	    OutputStream out = new FileOutputStream(outFilePath);
	 
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = gzipInputStream.read(buf)) > 0)
	        out.write(buf, 0, len);
	 
	    gzipInputStream.close();
	    out.close();
	 
	    //new File(inFilePath).delete();
	 
	    return outFilePath;
	}
	
	public static void copy(String from, String to) throws IOException {
		File inputFile = new File(from);
		File outputFile = new File(to);
		
		FileReader in = new FileReader(inputFile);
		FileWriter out = new FileWriter(outputFile);
		int c;
		
		while ((c = in.read()) != -1)
		  out.write(c);
		
		in.close();
		out.close();
	}
}
