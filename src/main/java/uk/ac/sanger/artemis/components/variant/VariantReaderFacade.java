package uk.ac.sanger.artemis.components.variant;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.genedb.crawl.model.MappedVCFRecord;


public abstract class VariantReaderFacade {
	
	protected boolean isVcf_v4;
	protected AbstractVCFReader abstractReader;
	
	public static final VariantReaderFacade getReader(String url) throws IOException {
		if (IOUtils.isBCF(url)) {
			return new BCFReaderFacade(url);
		} 
		return new TabixReaderFacade(url);
	}
	
	public abstract List<MappedVCFRecord> query(String region, int start, int end) throws IOException;
	
	public List<String> getSeqNames() {
		
		return Arrays.asList(abstractReader.getSeqNames());
	}
	
	protected MappedVCFRecord processRecord (VCFRecord record) {
		
		MappedVCFRecord mappedRecord = new MappedVCFRecord();
		
		mappedRecord.chrom = record.getChrom();
		mappedRecord.pos = record.getPos();
		mappedRecord.quality = record.getQuality();
		
		mappedRecord.ref = record.getRef();
		mappedRecord.ref_length = mappedRecord.ref.length();
		
		if (record.getAlt().isMultiAllele()) {
			mappedRecord.alt.isMultiAllele = true;
		} else if (record.getAlt().isDeletion(isVcf_v4)) {
			mappedRecord.alt.isDeletion = true;
		} else if (record.getAlt().isInsertion(isVcf_v4)) {
			mappedRecord.alt.isInsertion = true;
		}
		
//		mappedRecord.alt.isMultiAllele = record.getAlt().isMultiAllele();
//		mappedRecord.alt.isDeletion = record.getAlt().isDeletion(isVcf_v4);
//		mappedRecord.alt.isInsertion = record.getAlt().isInsertion(isVcf_v4);
		
		mappedRecord.alt.length = record.getAlt().length();
		
		return mappedRecord;
	}
	
}