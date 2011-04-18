/**
 * 
 */
package org.genedb.crawl.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.apache.log4j.Logger;

import net.sf.samtools.SAMFileReader;

public class Alignment {
	
	private static Logger logger = Logger.getLogger(Alignment.class);
	
	@XmlAttribute(required=true)
	public Integer fileID = 0;
	
	@XmlAttribute(required=false)
	public String file;
	
	@XmlAttribute(required=false)
	public String index;
	
	public String organism;
	public List<String> chromosomes = new ArrayList<String>();
	public String meta;
	
	@XmlElement(required=false)
	public List<AlignmentSequenceAlias> sequences;
	
	private SAMFileReader reader;
	private File bamFile;
	private File indexFile;
	private URL bamFileURL;
	
	
	
	public SAMFileReader getReader() throws IOException {
		if (reader == null) {
			
			if (file == null) {
				throw new RuntimeException("Could not generate a SAMFileReader because neither a file or url has been specified.");
			}
			
			if (index != null) {
				logger.info("getting index: " + index);
				indexFile = getFile(index);
			}
			
			logger.info("getting bam: " + file);
			
			if (file.startsWith("http")) {
				
				bamFileURL = new URL(file); 
				reader = new SAMFileReader(bamFileURL, indexFile, false);
				
			} else {
				
				bamFile = new File(file);
				reader = new SAMFileReader(bamFile, indexFile);
			}
			
			
		} 
		
		return reader;
	}
	
	private File getFile(String path) throws IOException {
		
		File f = null;
		
		if (path.startsWith("http://")) {
			URL url = new URL(path);
			f = download(url);
		} else {
			f = new File(path);
		}
		
		return f;
	}
	
	private File download(URL url) throws IOException {
		
		String fileName = "/tmp/" + fileID;
		
		logger.info(fileName);
		
		File f = new File(fileName);
		
		OutputStream out = new FileOutputStream(f);
		
		InputStream in = url.openStream();
	    byte[] buf = new byte[4 * 1024]; // 4K buffer
	    int bytesRead;
	    while ((bytesRead = in.read(buf)) != -1) {
	      out.write(buf, 0, bytesRead);
	      logger.info("reading");
	    }
	    
	    out.close();
	    
	    logger.info(String.format("Downloaded %s to %s", url, f));
	    
	    return f;
	    
	}
	
}