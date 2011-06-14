package uk.ac.sanger.artemis.components.variant;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.genedb.crawl.model.MappedVCFRecord;
import org.genedb.crawl.model.Sequence;


public abstract class VariantReaderAdapter {

	protected AbstractVCFReader abstractReader;
	
	public static final VariantReaderAdapter getReader(String url) throws IOException {
		if (IOUtils.isBCF(url)) {
			return new BCFReaderAdapter(url);
		} 
		return new TabixReaderAdapter(url);
	}
	
	public abstract List<VCFRecord> unFilteredQuery(
			String region, 
			int start, 
			int end) throws IOException;
	
	public abstract List<MappedVCFRecord> query(
			String region, 
			int start, 
			int end, 
			List<GeneFeature> genes, 
			VariantFilterOptions options,
			Sequence regionSequence) throws IOException;
	
	public List<String> getSeqNames() {
		return Arrays.asList(abstractReader.getSeqNames());
	}
	
	
	protected boolean showRecord(
			VCFRecordAdapter record, 
			List<GeneFeature> genes, 
			VariantFilterOptions options, 
			int basePosition,
			Sequence regionSequence) {
		
		if (!options.isEnabled(VariantFilterOption.SHOW_DELETIONS) //.showDeletions
				&& record.getAlt().isDeletion(isVcf_v4()))
			return false;

		if (!options.isEnabled(VariantFilterOption.SHOW_INSERTIONS) //.showInsertions
				&& record.getAlt().isInsertion(isVcf_v4()))
			return false;

		if (!options.isEnabled(VariantFilterOption.SHOW_NON_OVERLAPPINGS) //.showNonOverlappings
				&& ! record.isOverlappingFeature(genes, basePosition))
			return false;

		if (!options.isEnabled(VariantFilterOption.SHOW_NON_VARIANTS) /*.showNonVariants*/ && record.getAlt().isNonVariant())
			return false;

		short isSyn = record.isSynonymous(genes, basePosition);
		
		record.markAsNewStop = false;
		if (options.isEnabled(VariantFilterOption.MARK_NEW_STOPS) //.markNewStops
				&& !record.getAlt().isDeletion(isVcf_v4())
				&& !record.getAlt().isInsertion(isVcf_v4())
				&& record.getAlt().length() == 1
				&& record.getRef().length() == 1) {

			if (isSyn == 2)
				record.markAsNewStop = true;
		}

		if ((!options.isEnabled(VariantFilterOption.SHOW_SYNONYMOUS) /*.showSynonymous*/ || !options.isEnabled(VariantFilterOption.SHOW_NON_SYNONYMOUS) /*.showNonSynonymous*/)
				&& !record.getAlt().isDeletion(isVcf_v4())
				&& !record.getAlt().isInsertion(isVcf_v4())
				&& record.getAlt().length() == 1
				&& record.getRef().length() == 1) {

			if ((!options.isEnabled(VariantFilterOption.SHOW_SYNONYMOUS) /*.showSynonymous*/ && isSyn == 1)
					|| (!options.isEnabled(VariantFilterOption.SHOW_NON_SYNONYMOUS) /*.showNonSynonymous*/ && (isSyn == 0 || isSyn == 2)))
				return false;
		}

		if (!options.isEnabled(VariantFilterOption.SHOW_MULTI_ALLELES) //.showMultiAlleles
				&& record.getAlt().isMultiAllele())
			return false;

		return true;
		
	}
	
	protected MappedVCFRecord processRecord (VCFRecordAdapter record) {
		
		MappedVCFRecord mappedRecord = new MappedVCFRecord();
		
		mappedRecord.markAsNewStop = record.markAsNewStop;
		
		mappedRecord.chrom = record.getChrom();
		mappedRecord.pos = record.getPos();
		mappedRecord.quality = record.getQuality();
		
		mappedRecord.ref = record.getRef();
		mappedRecord.ref_length = mappedRecord.ref.length();
		
		if (record.getAlt().isMultiAllele()) {
			mappedRecord.alt.isMultiAllele = true;
		} else if (record.getAlt().isDeletion(isVcf_v4())) {
			mappedRecord.alt.isDeletion = true;
		} else if (record.getAlt().isInsertion(isVcf_v4())) {
			mappedRecord.alt.isInsertion = true;
		}
		
		mappedRecord.alt.length = record.getAlt().length();
		mappedRecord.alt.alternateBase = record.getAlt().toString();
		
		return mappedRecord;
	}
	
	private boolean isVcf_v4()
	{
		return abstractReader.isVcf_v4();
	}
	
}