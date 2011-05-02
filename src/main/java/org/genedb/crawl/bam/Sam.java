package org.genedb.crawl.bam;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.samtools.AlignmentBlock;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMFileReader.ValidationStringency;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;

import org.apache.log4j.Logger;
import org.genedb.crawl.model.Alignment;
import org.genedb.crawl.model.MappedCoverage;
import org.genedb.crawl.model.MappedQuery;
import org.genedb.crawl.model.MappedSAMHeader;
import org.genedb.crawl.model.MappedSAMSequence;
import org.genedb.crawl.model.MappedSAMRecords;
import org.genedb.crawl.model.adapter.AlignmentBlockAdapter;



public class Sam {
	
	public BioDataFileStore<Alignment> alignmentStore;
	
	public void setAlignmentStore (BioDataFileStore<Alignment> alignmentStore) {
		this.alignmentStore = alignmentStore;
	}
	
	private static Logger logger = Logger.getLogger(Sam.class);
	
	/*
	 * If no properties are supplied for the query method, this is the default set.
	 */
	private final static String[] defaultProperties = {"alignmentStart", "alignmentEnd", "flags", "readName"};
	
	
	/*
	 * A map of field names in the MappedSAMRecords class
	 */
	private static Map<String, Field> recordsBeanFields = new HashMap<String, Field>();;
	
	/*
	 * a map of getters in the SAMRecord class whose names equate to (via bean camel case convention) to fields in the Records class. 
	 */
	private static Map<String,Method> samRecordMethodMap = new HashMap<String,Method>();
	
	// no point in doing this more than once
	static {
		
		// generate a map of fields in the MappedSAMRecords bean
		for (Field f : MappedSAMRecords.class.getDeclaredFields()) {
			recordsBeanFields.put(f.getName(), f);
			logger.debug(String.format("field %s %s", f.getName(), f));
		}
		
		// find equivalent methods in the SAMRecord class 
		for (Method method : SAMRecord.class.getDeclaredMethods()) {
			String methodName = method.getName();
			if (methodName.startsWith("get")) {
				String propertyName = methodName.substring(3);
				propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
				
				if (! recordsBeanFields.containsKey(propertyName)) {
					continue;
				}
				
				samRecordMethodMap.put(propertyName, method);
				logger.debug(String.format("method %s %s", propertyName, method));
			}
		}
	}
	
	private SAMFileReader getSamOrBam(int fileID) throws Exception {
		final SAMFileReader inputSam = alignmentStore.getFile(fileID).getReader(); 
		if (inputSam == null) {
			throw new Exception ("Could not find the file " + fileID);
		}
		inputSam.setValidationStringency(ValidationStringency.SILENT);
		return inputSam;
	}
	
	public MappedSAMHeader header(int fileID) throws Exception {
		return this.header(getSamOrBam(fileID));
	}
	
	public MappedSAMHeader header(SAMFileReader file) throws Exception {
		MappedSAMHeader model = new MappedSAMHeader();
		
		for (Map.Entry<String, String> entry : file.getFileHeader().getAttributes()) {
			model.attributes.put(entry.getKey(), entry.getValue().toString());
		}
		
		return model;
	}
	
	public List<MappedSAMSequence> sequence(int fileID) throws Exception {
		return alignmentStore.getSequences(fileID);
		
	}
	
	
	
	
	public List<Alignment> listforsequence(String sequence) throws Exception {
		return alignmentStore.listforsequence(sequence);
	}
	
	public List<Alignment> list() {
		return alignmentStore.getFiles();
	}
	
	public List<Alignment> listfororganism(String organism) {
		return alignmentStore.listfororganism(organism);
	}
	
	
	
	public synchronized MappedQuery query(int fileID, String sequence, int start,  int end, boolean contained, String[] properties, int filter ) throws Exception {
		logger.debug("FileID : " + fileID);
		sequence = alignmentStore.getActualSequenceName(fileID, sequence);
		return this.query(getSamOrBam(fileID), sequence, start, end, contained, properties, filter);
	}
	
	private synchronized MappedQuery query(SAMFileReader file, String sequence, int start,  int end, boolean contained, String[] properties, int filter ) throws Exception {
		
		if (sequence == null) {
			throw new Exception ("Supplied sequence does not exist.");
		}
		
		if (properties == null) {
			properties = defaultProperties;
		}
		
		logger.debug(String.format("file: %s\tlocation: '%s:%d-%d'\tcontained?%s\tfilter: %d(%s)", file, sequence, start, end, contained, filter, padLeft(Integer.toBinaryString(filter), 8)));
		
		long startTime = System.currentTimeMillis();
		
		MappedQuery model = new MappedQuery();
		model.records = new MappedSAMRecords();		
		
		// we certainly don't want duplicates here, as this was cause unnecessary method calls and data to be written out multiple times
		// so we use a set
		Set<String> props = new HashSet<String>();
		
		// filter the properties, and initialise the relevant property fields in the bean 
		for (String propertyName : properties) {
			
			// only add fields that we have bean properties for
			if (! recordsBeanFields.containsKey(propertyName)) {
				continue;
			}
			
			Field f = recordsBeanFields.get(propertyName);
			
			// currently the only records field that is not a list
			if (! f.getName().equals("alignmentBlocks")) {
				f.set(model.records, new ArrayList());
			}
			
			props.add(propertyName);
		}
		
		logger.info(props);
		
		model.count = 0;
		
		if (props.contains("alignmentBlocks")) {
			model.records.alignmentBlocks = new ArrayList<AlignmentBlockAdapter[]>();
		}
		
		
		SAMRecordIterator i = null;
		try {
			
			/**
			 * 
			 * According to the BAMFileReader2 doc:
			 * 
		     * "Prepare to iterate through the SAMRecords in file order.
		     * Only a single iterator on a BAM file can be extant at a time.  If getIterator() or a query method has been called once,
		     * that iterator must be closed before getIterator() can be called again.
		     * A somewhat peculiar aspect of this method is that if the file is not seekable, a second call to
		     * getIterator() begins its iteration where the last one left off.  That is the best that can be
		     * done in that situation."
		     * 
		     * For this reason, we must make sure that we close the iterator at the end of the loop AND make sure that the methods
		     * that use this iterator are iterates is synchronized. 
		     * 
		     */
			
			i = file.query(sequence, start, end, contained);
			
			while ( i.hasNext() )  {
				SAMRecord record = i.next();
				
				
				
				/*
				 * int toFilter = record.getFlags() & filter;
				logger.debug(String.format("Read: %s, Filter: %s, Flags: %s, Result: %s", record.getReadName(), filter, record.getFlags(), toFilter ));
				logger.debug(padLeft(Integer.toBinaryString(filter), 8));
				logger.debug(padLeft(Integer.toBinaryString(record.getFlags()), 8));
				logger.debug(padLeft(Integer.toBinaryString(toFilter), 8));
				*/
				
				if ((record.getFlags() & filter) > 0) {
					//logger.debug("some matches ... skipping");
					continue;
				}
				
				for (String propertyName : props) {
					
					
					if (propertyName.endsWith("alignmentBlocks")) {
						
						List<AlignmentBlock> result = record.getAlignmentBlocks();
						
						@SuppressWarnings("unchecked")
						List<AlignmentBlock> blocks = (List<AlignmentBlock>) result; 
						List<AlignmentBlockAdapter> blockAdapters = new ArrayList<AlignmentBlockAdapter>();
						for (AlignmentBlock block : blocks) {
							blockAdapters.add(new AlignmentBlockAdapter(block));
						}
						
						
						AlignmentBlockAdapter[] blockArray = new AlignmentBlockAdapter[blockAdapters.size()];
						int b = 0;
						for (AlignmentBlockAdapter block : blockAdapters) {
							blockArray[b] = block;
							b++;
						}
						
						model.records.alignmentBlocks.add(blockArray);
						
					} else {
						Method method = samRecordMethodMap.get(propertyName);
						Object result = method.invoke(record);
						Field f = recordsBeanFields.get(propertyName);
						ArrayList list = (ArrayList) f.get(model.records);
						list.add(result);
					}
				}
				
				
				model.count++;
				
			}
		
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			
		} finally {
			if (i != null) {
				i.close();
			}
		}
		
		
		long endTime = System.currentTimeMillis() ;
		float time = (endTime - startTime) / (float) 1000 ;
		
		model.contained = contained;
		model.start = start;
		model.end = end;
		model.sequence = sequence;
		model.time = Float.toString(time);
		model.filter = filter;
		
		return model;
	}
	
	
	public synchronized MappedCoverage coverage(int fileID, String sequence, int start, int end, int window, int filter) throws Exception {
		sequence = alignmentStore.getActualSequenceName(fileID, sequence);
		return this.coverage(getSamOrBam(fileID), sequence, start, end, window, filter);
	}
	
	public synchronized MappedCoverage coverage(SAMFileReader file, String sequence, int start, int end, int window, int filter) throws Exception {
		
		if (sequence == null) {
			throw new Exception ("Supplied sequence does not exist.");
		}
		
		long startTime = System.currentTimeMillis();
		
		int max = 0;
		final int nBins = Math.round((end-start+1.f)/window);
		
	    int coverage[] = new int[nBins];
	    
	    for(int i=0; i<coverage.length; i++) {
	    	coverage[i] = 0;  
	    }
		
	    logger.debug("starting iterations, filter: " + filter);
	    logger.debug(start + "," + end + "," + window + "," + nBins);
		
		SAMRecordIterator iter = null;
		
		
		logger.debug(startTime);
		
		try {
			iter = file.query(sequence, start, end, false);
			while (iter.hasNext()) {
				
				SAMRecord record = iter.next();
				
				if ((record.getFlags() & filter) > 0) {
					continue;
				}
				
				
				List<AlignmentBlock> blocks = record.getAlignmentBlocks();
				
				for (AlignmentBlock block : blocks) {
					for (int k = 0; k < block.getLength(); k++) {
						
						final int pos = block.getReferenceStart() + k - start;
						final float fbin =  pos / (float) window;
						
						if ((fbin < 0) || (fbin > nBins-1)) {
							continue;
						}
						
						final int ibin = (int) fbin;
                        
						coverage[ibin] += 1;
						
						if(coverage[ibin] > max) {
							max = coverage[ibin];
						}
					}
				}
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			
		} finally {
			if (iter != null) {
				iter.close();
			}
		}
		
		long endTime = System.currentTimeMillis() ;
		float time = (endTime - startTime) / (float) 1000 ;
		
		
		
		logger.debug("ending iterations");
		
		MappedCoverage mc = new MappedCoverage();
		mc.data = coverage;
		mc.start = start;
		mc.end = end;
		mc.window = window;
		mc.max = max;
		mc.time = Float.toString(time);
		mc.bins = nBins;
		
		return mc;
		
	}
	
	private String padLeft(String s, int n) {
	    return String.format("%1$#" + n + "s", s);  
	}
	
}
