package uk.ac.sanger.artemis.components.variant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.genedb.crawl.model.MappedVCFRecord;
import org.genedb.crawl.model.Sequence;

import uk.ac.sanger.artemis.components.variant.BCFReader.BCFReaderIterator;

public class BCFReaderAdapter extends VariantReaderAdapter {
	
	private Logger logger = Logger.getLogger(BCFReaderAdapter.class);
	
	private BCFReader reader;
	
	public BCFReaderAdapter(String url) throws IOException {
		reader = new BCFReader(url);
		String hdr = reader.headerToString();
	    if(hdr.indexOf("VCFv4") > -1)
	    	reader.setVcf_v4(true);
		abstractReader = reader;
	}
	
	
	@Override
	public List<VCFRecord> unFilteredQuery(String region, int start, int end)
			throws IOException {
		BCFReaderIterator it = reader.query(region, start, end);
		logger.info("BEGIN QUERY " + region + ":" + start + "-" + end);
		VCFRecord record;
		List<VCFRecord> records = new ArrayList<VCFRecord>();
		while((record = it.next()) != null) {
			logger.info(record);
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
		
		List<MappedVCFRecord> records = new ArrayList<MappedVCFRecord>();
		
		logger.info("BEGIN QUERY " + region + ":" + start + "-" + end);
		
		BCFReaderIterator it = reader.query(region, start, end);
		logger.info("Iterating with " + it);
		
		VCFRecord record;
		while((record = it.next()) != null) {
			logger.info(record);
			
			VCFRecordAdapter recordAdapter = new VCFRecordAdapter(record, regionSequence);
			if (showRecord(recordAdapter, genes, options, end, regionSequence)) {
				records.add(processRecord(recordAdapter));
			} else {
				logger.warn("not showing " + record.getPos());
			}
			
		}
		
		return records;
	          
	}



	
	
}