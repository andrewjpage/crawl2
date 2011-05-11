package org.genedb.crawl;

import java.io.File;
import java.io.IOException;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;
import net.sf.samtools.SAMSequenceRecord;

import org.apache.log4j.Logger;

import uk.ac.sanger.artemis.components.variant.FTPSeekableStream;

import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.Map;

import junit.framework.TestCase;

public class FTPTest extends TestCase {
	
	private static final Logger logger = Logger.getLogger(FTPTest.class);
	
	String urlString = "ftp://ftp.sanger.ac.uk/pub/mouse_genomes/current_bams/129S1.bam";
	URL url;

	
	@Override
	public void setUp() {
		try {
			url =  new URL(urlString);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public void test1() throws SocketException, IOException {
		
		FTPSeekableStream fss = new FTPSeekableStream(url);
		File index = fss.getIndexFile();
		
		SAMFileReader reader = new SAMFileReader(fss, index, false);
		reader.getFileHeader();
		
		logger.info("attributes");
		for (Map.Entry<String, String> entry : reader.getFileHeader().getAttributes()) {
			logger.info(String.format("%s : %s", entry.getKey(), entry.getValue()));
		}
		
		logger.info("sequences");
		for (SAMSequenceRecord ssr : reader.getFileHeader().getSequenceDictionary().getSequences()) {
			logger.info(String.format("%s : %s", ssr.getSequenceName(), ssr.getSequenceLength()));
		}
		
		SAMRecordIterator i = reader.query("NT_166325", 0, 3994, false);
		
		while ( i.hasNext() )  {
			SAMRecord record = i.next();
			logger.info(String.format("Read: %s, %s, %s, %s", record.getReadName(), record.getAlignmentStart(), record.getAlignmentEnd(), record.getFlags()));
		}
		
		logger.info("Done");
		
	}
	
	
	
	


	
	
	
	

}
