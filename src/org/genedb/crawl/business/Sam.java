package org.genedb.crawl.business;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.sf.samtools.AlignmentBlock;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;

import org.apache.log4j.Logger;
import org.genedb.crawl.model.MappedCoverage;
import org.genedb.crawl.model.MappedQuery;


public class Sam {
	
	private Logger logger = Logger.getLogger(Sam.class);
	public HeirarchyIndex heirarchyIndex;
	
	private final String[] properties = {"alignmentStart", "alignmentEnd", "flags", "readName"};
	
	public synchronized MappedQuery query(int fileID, String sequence, int start,  int end, boolean contained, String[] properties) throws Exception {

		final SAMFileReader inputSam = heirarchyIndex.getSamOrBam(fileID);
		if (inputSam == null) {
			throw new Exception ("Could not find the file " + fileID);
		}
		
		
		MappedQuery model = new MappedQuery();
		
		Set<String> propertySet = new HashSet<String>(Arrays.asList(properties));
		Map<Method,String> methods2properties = new HashMap<Method,String>();
		
		Method[] methods = SAMRecord.class.getDeclaredMethods();
		for (Method method : methods) {
			String methodName = method.getName();
			if (methodName.startsWith("get")) {
				String propertyName = methodName.substring(3);
				propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
				logger.info(methodName + " " + propertyName);
				
				if (propertySet.contains(propertyName)) {
					logger.info("added!");
					model.records.put(propertyName, new ArrayList<Object>());
					methods2properties.put(method, propertyName);
				}
				
			}
		}
		
		SAMRecordIterator i = null;
		try {
			
			/**
			 * 
			 * According to the BAMFileReader2 docs:
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
			
			i = inputSam.query(sequence, start, end, true);
			
			while ( i.hasNext() )  {
				SAMRecord record = i.next();
				
				for (Entry<Method, String> entry : methods2properties.entrySet()) {
					Method method = entry.getKey();
					String propertyName = entry.getValue();
					Object result = method.invoke(record, new Object[]{});
					List list = model.records.get(propertyName);
					list.add(result);
				}
				
				//MappedSAMRecord m = new MappedSAMRecord(record);
				//model.addResult(m);
				//logger.info("Adding " + m);
			}
		
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			
		} finally {
			if (i != null) {
				i.close();
			}
		}
		
		model.contained = contained;
		model.start = start;
		model.end = end;
		model.sequence = sequence;
		
		return model;
	}
	
	public synchronized MappedQuery query(int fileID, String sequence, int start,  int end, boolean contained) throws Exception {
		return query(fileID, sequence, start, end, contained, properties);		
	}
	
	public synchronized MappedCoverage coverage(int fileID, String sequence, int start, int end, int window) throws Exception {
		
		final SAMFileReader inputSam = heirarchyIndex.getSamOrBam(fileID);
		if (inputSam == null) {
			throw new Exception ("Could not find the file " + fileID);
		}
		
		int max = 0;
		final int nBins = Math.round((end-start+1.f)/window);
		
	    int coverage[] = new int[nBins];
	    
	    for(int i=0; i<coverage.length; i++) {
	    	coverage[i] = 0;  
	    }
		
	    logger.debug("starting iterations");
	    logger.debug(start + "," + end + "," + window + "," + nBins);
		
		SAMRecordIterator iter = null;
		
		long startTime = System.currentTimeMillis();
		logger.debug(startTime);
		
		try {
			iter = inputSam.query(sequence, start, end, false);
			while (iter.hasNext()) {
				
				SAMRecord record = iter.next();
				List<AlignmentBlock> blocks = record.getAlignmentBlocks();
				
				for (AlignmentBlock block : blocks) {
					for (int k = 0; k < block.getLength(); k++) {
						
						final int pos = block.getReferenceStart() + k - start;
						final float fbin =  pos / (float) window;
						
						if ( record.getReadName().equals("IL6_4415:2:54:17521:5107#11")) {
							logger.debug(pos + " / " + window + " = " + fbin);
							//debug(record.getReadName() + " " + record.getAlignmentStart() + " " + pos + " " + bin + " = " + k + " " + window + " " + (bin < 0) + " " + (bin > nBins-1));
						}
						
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
		logger.debug(endTime);
		
		logger.debug("ending iterations");
		
		MappedCoverage mc = new MappedCoverage();
		mc.coverage = coverage;
		mc.start = start;
		mc.end = end;
		mc.window = window;
		mc.max = max;
		mc.time = endTime - startTime;
		mc.bins = nBins;
		
		return mc;
		
	}
	
}
