package uk.ac.sanger.artemis.components.variant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.genedb.crawl.model.MappedVCFRecord;

class TabixReaderFacade extends VariantReaderFacade{
	
	private TabixReader reader;
	
	public TabixReaderFacade(String url) throws IOException {
		reader = new TabixReader(url);
		abstractReader = reader;
		
		isVcf_v4 = reader.isVcf_v4();
		reader.getSeqNames();
	}
	
	@Override
	public List<MappedVCFRecord> query(String region, int start, int end) throws IOException {
		
		List<MappedVCFRecord> records = new ArrayList<MappedVCFRecord>();
		
		TabixReader.Iterator iter = reader.query(region+":"+start+"-"+end); 
		String s = null;
		while (iter != null && (s = iter.next()) != null)
        {
			VCFRecord vcfRecord = VCFRecord.parse(s);
			records.add(processRecord(vcfRecord));
        }
		
		return records;
		
	}

	
}