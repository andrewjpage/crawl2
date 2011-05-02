package uk.ac.sanger.artemis.components.variant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.genedb.crawl.model.MappedVCFRecord;

import uk.ac.sanger.artemis.components.variant.BCFReader.BCFReaderIterator;

class BCFReaderFacade extends VariantReaderFacade {
	
	private Logger logger = Logger.getLogger(BCFReaderFacade.class);
	
	private BCFReader reader;
	
	public BCFReaderFacade(String url) throws IOException {
		reader = new BCFReader(url);
		abstractReader = reader;
		
		isVcf_v4 = reader.isVcf_v4();
		
	}
	
	@Override
	public List<MappedVCFRecord> query(String region, int start, int end) throws IOException {
		
		List<MappedVCFRecord> records = new ArrayList<MappedVCFRecord>();
		
		BCFReaderIterator it = reader.query(region, start, end);
		VCFRecord bcfRecord;
		while((bcfRecord = it.next()) != null) {
			logger.info(bcfRecord);
			records.add(processRecord(bcfRecord));
		}
		
		return records;
	          
	}

	
	
}