package uk.ac.sanger.artemis.components.variant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.genedb.crawl.model.MappedVCFRecord;
import org.genedb.crawl.model.Sequence;

public class TabixReaderAdapter extends VariantReaderAdapter{
	
	private Logger logger = Logger.getLogger(TabixReaderAdapter.class);
	
	private TabixReader reader;
	private String url;
	
	public TabixReaderAdapter(String url) throws IOException {
		
		this.url = url;
		
		reader = new TabixReader(url);
		abstractReader = reader;
		logger.info(String.format("Intantiatging tabix reader %s with url %s ", reader.getName(), reader.getFileName()));
		
		String line;
	    while( (line = reader.readLine() ) != null ) {
	        if(!line.startsWith("##"))
	          break;
	        
	        if(line.indexOf("VCFv4") > -1) {
	        	reader.setVcf_v4(true);
	        	break;
	        }
	    }
		//reader.getSeqNames();
	}
	
	@Override
	public List<VCFRecord> unFilteredQuery(String region, 
			int start, 
			int end) throws IOException {
		logger.info("BEGIN QUERY " + region + ":" + start + "-" + end);
		TabixReader reader = new TabixReader(url);
		TabixReader.Iterator iter = reader.query(region+":"+start+"-"+end);
		
		String s = null;
		List<VCFRecord> records = new ArrayList<VCFRecord>();
		while (iter != null && (s = iter.next()) != null)
        {
			//VCFRecord record = VCFRecord.parse(s);
			logger.info(s);
			VCFRecord record = VCFRecord.parse(s);
			records.add(record);
        }
		return records;
		
	}
	
	@Override
	public List<MappedVCFRecord> query(
			String region, 
			int start, 
			int end, 
			List<GeneFeature> genes, 
			VariantFilterOptions options, 
			Sequence regionSequence) throws IOException {
		logger.info("BEGIN QUERY " + region + ":" + start + "-" + end);
		List<MappedVCFRecord> records = new ArrayList<MappedVCFRecord>();
		
		logger.info(String.format("Using tabix reader %s with url %s ", reader.getName(), reader.getFileName()));
		
		TabixReader.Iterator iter = reader.query(region+":"+start+"-"+end);
		
		logger.info("Iterating with " + iter);
		logger.info(region+":"+start+"-"+end);
		
		String s = null;
		while (iter != null && (s = iter.next()) != null)
        {
			logger.error(s);
			VCFRecord record = VCFRecord.parse(s);
			logger.error(record);
			VCFRecordAdapter recordAdapter = new VCFRecordAdapter(record, regionSequence);
			logger.error(recordAdapter);
			
			if (showRecord(recordAdapter, genes, options, end, regionSequence)) {
				records.add(processRecord(recordAdapter));
				logger.warn("Added record " + record);
			} else {
				logger.warn("not showing " + record.getPos());
			}
			
        }
		
		return records;
		
	}

	
}