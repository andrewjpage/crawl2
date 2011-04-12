package org.genedb.crawl.bam;

import java.io.File;
import java.util.Map;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMFileReader.ValidationStringency;
import net.sf.samtools.SAMFileWriter;
import net.sf.samtools.SAMFileWriterFactory;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMSequenceDictionary;
import net.sf.samtools.SAMSequenceRecord;

public class BAMConverter {
	
	@Option(name = "-h", aliases = {"--help"}, usage = "Print help")
	public boolean help;
	
	@Option(name = "-i", aliases = {"--input"}, usage = "The input BAM", required = true)
	public File inputSamOrBamFile;
	
	@Option(name = "-o", aliases = {"--output"}, usage = "The output BAM", required = true)
	public File outputSamOrBamFile;
	
	@Option(name = "-m", aliases = {"--m"}, usage = "The map of old to new sequences", required = true)
	public Map<String,String> sequenceMap;
	
	public static void main(String[] args) {
		
		BAMConverter converter = new BAMConverter();
		CmdLineParser parser = new CmdLineParser(converter);
		
		try {
			
			parser.parseArgument(args);
		
			if (converter.help) {
				parser.setUsageWidth(80);
	            parser.printUsage(System.out);
	            System.exit(1);
			}
			
			converter.convert();
		
		} catch (CmdLineException e) {
			System.out.println(e.getMessage());
            parser.setUsageWidth(80);
            parser.printUsage(System.out);
            System.exit(1);
		} 

	}
	
	public void convert() {
		final SAMFileReader inputSam = new SAMFileReader(inputSamOrBamFile);
		
		inputSam.setValidationStringency(ValidationStringency.SILENT);
		
		System.out.println(String.format("Copying %s to %s", inputSamOrBamFile, sequenceMap.get(outputSamOrBamFile)));
		
		System.out.println(sequenceMap);
		
		SAMFileHeader header = inputSam.getFileHeader();
		
		SAMSequenceDictionary dict = header.getSequenceDictionary();
		SAMSequenceDictionary newDict = new SAMSequenceDictionary();
		
		for (SAMSequenceRecord sequence : dict.getSequences()) {
			String currentName = sequence.getSequenceName();
			if (sequenceMap.containsKey(currentName)) {
				System.out.println(String.format("Converting %s to %s", currentName, sequenceMap.get(currentName)));
				currentName = sequenceMap.get(currentName);
			}
			SAMSequenceRecord newSequence = new SAMSequenceRecord(currentName, sequence.getSequenceLength());
			newDict.addSequence(newSequence);
		}
		
		header.setSequenceDictionary(newDict);
		
		final SAMFileWriter outputSam = new SAMFileWriterFactory().makeSAMOrBAMWriter(header, 
				true, outputSamOrBamFile); 
		
		for (final SAMRecord samRecord : inputSam) { 
			outputSam.addAlignment(samRecord); 
		}
		
		outputSam.close(); 
		inputSam.close(); 
	}
	
	

}
