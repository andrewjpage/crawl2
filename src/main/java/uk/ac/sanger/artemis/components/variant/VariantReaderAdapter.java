package uk.ac.sanger.artemis.components.variant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.genedb.crawl.model.Exon;
import org.genedb.crawl.model.Gene;

import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.MappedVCFRecord;
import org.genedb.crawl.model.Sequence;
import org.genedb.crawl.model.Transcript;

import uk.ac.sanger.artemis.io.Range;
import uk.ac.sanger.artemis.io.RangeVector;
import uk.ac.sanger.artemis.sequence.Bases;
import uk.ac.sanger.artemis.util.OutOfRangeException;


public abstract class VariantReaderAdapter {
	
	private static Logger logger = Logger.getLogger(VariantReaderAdapter.class);
	
	protected AbstractVCFReader abstractReader;
	
	public static final VariantReaderAdapter getReader(String url) throws IOException {
		if (IOUtils.isBCF(url)) {
			logger.info("BCF " + url);
			return new BCFReaderAdapter(url);
		} 
		logger.info("Tabix " + url);
		return new TabixReaderAdapter(url);
	}
	
	public abstract void close() throws IOException;
	
	public List<VCFRecord> unFilteredQuery(String region, 
			int start, 
			int end) throws IOException {
		logger.info("BEGIN QUERY " + region + ":" + start + "-" + end);
		List<VCFRecord> records = new ArrayList<VCFRecord>();
		VCFRecord record;
		while((record = abstractReader.getNextRecord(region, start, end)) != null) {
			records.add(record);
		}
		return records;
	}
	
	@SuppressWarnings("unchecked")
	public List<CDSFeature> makeCDSFeatures(List<LocatedFeature> features, Sequence regionSequence) throws OutOfRangeException {
	    List<CDSFeature> cdsFeatures = new ArrayList<CDSFeature>();
	    
	    Map<String, List<LocatedFeature>> parented = new HashMap<String,List<LocatedFeature>>();
	    
	    for (LocatedFeature feature : features) {
	        
	        if (! feature.type.name.equals("exon"))
	            continue;
	        
	        if (feature.parent != null && feature.parentRelationshipType.equals("part_of")) {
	            if (parented.get(feature.parent) ==  null)
	                parented.put(feature.parent, new ArrayList<LocatedFeature>());
	            parented.get(feature.parent).add(feature);
	        }
	        
	        cdsFeatures.add(makeCDSFeature(feature,regionSequence));
	        
	    }
	    
	    for (Entry<String,List<LocatedFeature>> parentedFeatures : parented.entrySet()) {
	        cdsFeatures.add(
	                makeCDSFeature(parentedFeatures.getValue(), regionSequence));
	    }
	    
	    return cdsFeatures;
	}
	
	// TODO currently assumes they are in the correct order!
	@SuppressWarnings("unchecked")
	private CDSFeature makeCDSFeature(List<LocatedFeature> features, Sequence regionSequence) throws OutOfRangeException {
	    
	    StringBuilder bases = new StringBuilder();
	    RangeVector rv = new RangeVector();
	    
	    boolean isFwd = ( features.get(0).strand > 0) ? true : false;
	    
	    int min = Integer.MAX_VALUE;
        int max = 0;
	    
	    for (LocatedFeature feature : features) {
	        
	        rv.add(new Range(feature.fmin, feature.fmax));
            bases.append(regionSequence.dna.subSequence(feature.fmin, feature.fmax));
            
            if (feature.fmin < min) {
                min = feature.fmin;
            }
            if (feature.fmax > max) {
                max = feature.fmax;
            }
            
	    }
	    
	    String b = bases.toString();
        
        if (! isFwd) 
            b = Bases.reverseComplement(b);
        
        CDSFeature cdsFeature = new CDSFeature(isFwd, rv, min + 1, max, b);
	    
	    return cdsFeature;
	}
	
	@SuppressWarnings("unchecked")
	private CDSFeature makeCDSFeature(LocatedFeature feature, Sequence regionSequence) throws OutOfRangeException {
	    boolean isFwd = ( feature.strand > 0) ? true : false;
        RangeVector rv = new RangeVector();
        rv.add(new Range(feature.fmin, feature.fmax));
        String bases = regionSequence.dna.substring(feature.fmin, feature.fmax);
        if (! isFwd) 
            bases = Bases.reverseComplement(bases);
        CDSFeature cdsFeature = new CDSFeature(isFwd, rv, feature.fmin + 1, feature.fmax, bases);
        return cdsFeature;
	}
	
	@SuppressWarnings("unchecked")
	@Deprecated
    public List<CDSFeature> genesToCDSFeature(
	        List<Gene> genes, 
	        Sequence regionSequence) {
	    
	    List<CDSFeature> cdsFeatures = new ArrayList<CDSFeature>();
        
	    logger.info("Genes : " + genes.size());
	    
        for (Gene gene : genes) {
            
            //logger.info(gene.uniqueName);
            
            for (Transcript t : gene.transcripts) {
                
                boolean isFwd = ( gene.strand > 0) ? true : false;
                RangeVector rv = new RangeVector();
                
                int min = Integer.MAX_VALUE;
                int max = 0;
                
                StringBuilder bases = new StringBuilder();
                
                for (Exon e : t.exons) {
                    try {
                        
                        rv.add(new Range(e.fmin, e.fmax));
                        bases.append(regionSequence.dna.subSequence(e.fmin, e.fmax));
                        
                        if (e.fmin < min) {
                            min = e.fmin;
                        }
                        if (e.fmax > max) {
                            max = e.fmax;
                        }
                        
                    } catch (OutOfRangeException e1) {
                        throw new RuntimeException(e1);
                    }
                }
                
                String b = bases.toString();
                
                if (! isFwd) 
                    b = Bases.reverseComplement(b);
                
                CDSFeature cdsFeature = new CDSFeature(isFwd, rv, min + 1, max, b);
                cdsFeatures.add(cdsFeature);
                
            }
            
        }
        
        logger.info("CDSFeatures : " + cdsFeatures.size());
        
        assert(cdsFeatures.size() >= genes.size());
        
        return cdsFeatures;
	}
	
	public List<MappedVCFRecord> query(
			String region, 
			int start, 
			int end, 
			List<CDSFeature> cdsFeatures, 
			VariantFilterOptions options) throws IOException {
		List<MappedVCFRecord> records = new ArrayList<MappedVCFRecord>();
		
		
		//logger.info("BEGIN QUERY " + region + ":" + start + "-" + end);
		
		logger.info(
	              String.format(
	              "FILTER\t%s-%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s",
	              start, 
	              end,
	              options.isEnabled(VariantFilterOption.SHOW_SYNONYMOUS), 
	              options.isEnabled(VariantFilterOption.SHOW_NON_SYNONYMOUS), 
	              options.isEnabled(VariantFilterOption.SHOW_DELETIONS), 
	              options.isEnabled(VariantFilterOption.SHOW_INSERTIONS), 
	              options.isEnabled(VariantFilterOption.SHOW_MULTI_ALLELES), 
	              options.isEnabled(VariantFilterOption.SHOW_NON_OVERLAPPINGS),
	              options.isEnabled(VariantFilterOption.SHOW_NON_VARIANTS)));
		
		//logger.info(options);
		
		VCFRecord record;
		while((record = abstractReader.getNextRecord(region, start, end)) != null) {
			//logger.info(record);
			
			if (showRecord(record, cdsFeatures, options, record.getPos())) {
				records.add(processRecord(record));
			} else {
				logger.warn("not showing " + record.getPos());
			}
		}
		
		return records;
	}
	
	
	
	
	public List<String> getSeqNames() {
		return Arrays.asList(abstractReader.getSeqNames());
	}
	
	
	protected boolean showRecord(
			VCFRecord record, 
			List<CDSFeature> cdsFeatures, 
			VariantFilterOptions options, 
			int basePosition) {
		
		if (!options.isEnabled(VariantFilterOption.SHOW_DELETIONS) //.showDeletions
				&& record.getAlt().isDeletion(isVcf_v4()))
			return false;

		if (!options.isEnabled(VariantFilterOption.SHOW_INSERTIONS) //.showInsertions
				&& record.getAlt().isInsertion(isVcf_v4()))
			return false;

		if (!options.isEnabled(VariantFilterOption.SHOW_NON_OVERLAPPINGS) //.showNonOverlappings
				&& ! VCFview.isOverlappingFeature(cdsFeatures, basePosition))
			return false;

		if (!options.isEnabled(VariantFilterOption.SHOW_NON_VARIANTS) /*.showNonVariants*/ && record.getAlt().isNonVariant())
			return false;

		short isSyn = record.getSynFlag(cdsFeatures, basePosition);
		logger.info("ISSYNONYMOUS\t"+record.getPos() +"\t" + isSyn);
		
		if (options.isEnabled(VariantFilterOption.MARK_NEW_STOPS) //.markNewStops
				&& !record.getAlt().isDeletion(isVcf_v4())
				&& !record.getAlt().isInsertion(isVcf_v4())
				&& record.getAlt().length() == 1
				&& record.getRef().length() == 1) {

			if (isSyn == 2)
				record.setMarkAsNewStop(true);
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
	
	protected MappedVCFRecord processRecord (VCFRecord record) {
		
		MappedVCFRecord mappedRecord = new MappedVCFRecord();
		
		mappedRecord.markAsNewStop = record.isMarkAsNewStop();
		
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