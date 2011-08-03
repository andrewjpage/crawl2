package uk.ac.sanger.artemis.components.variant;

import java.util.List;

import org.apache.log4j.Logger;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.Sequence;

import uk.ac.sanger.artemis.sequence.AminoAcidSequence;
import uk.ac.sanger.artemis.sequence.Bases;

//public class VCFRecordAdapter {
//
//	private Logger logger = Logger.getLogger(VCFRecordAdapter.class);
//	
//	private VCFRecord record;
//	private Sequence regionSequence;
//	private short isSynonymous = -1;
//	
//	public boolean markAsNewStop;
//	
//	//public boolean markAsNewStop = false;
//	
//	public VCFRecordAdapter (VCFRecord record, Sequence regionSequence) {
//		this.record = record;
//		this.regionSequence = regionSequence;
//	}
//	
//	public VariantBase getAlt() {
//		return record.getAlt();
//	}
//	
//	public String getRef() {
//		return record.getRef();
//	}
//	
//	public String getChrom() {
//		return record.getChrom();
//	}
//	
//	public float getQuality() {
//		return record.getQuality();
//	}
//	
//	public int getPos() {
//		return record.getPos();
//	}
//	
//	public boolean isOverlappingFeature(List<GeneFeature> genes, int basePosition) {
//		
//		for (GeneFeature gene : genes) {
//			if (gene.fmin < basePosition && gene.fmax > basePosition) {
//				for (LocatedFeature exon : gene.getExons()) {
//					if (exon.fmin < basePosition && exon.fmax > basePosition) {
//						return true;
//					}
//				}
//			}
//		}
//		
//		return false;
//	}
//	
//	
//	/**
//	 * @param features
//	 * @param basePosition
//	 * @return 0 if non-synonymous; 1 if synonymous; 2 if non-synonymous and
//	 *         creates a stop codon 3 not within a gene
//	 */
//	public short isSynonymous(List<GeneFeature> features, int basePosition) {
//		
//		if (this.isSynonymous != -1) {
//			return isSynonymous;
//		}
//		
//		char variant = record.getAlt().toString().toLowerCase().charAt(0);
//		int intronlength = 0;
//
//		LocatedFeature lastExon = null;
//
//		for (GeneFeature feature : features) {
//
//			if (feature.fmin < basePosition && feature.fmax > basePosition) {
//
//				for (LocatedFeature exon : feature.getExons()) {
//					if (exon.fmin < basePosition && exon.fmax > basePosition) {
//
//						String featureBases = getFeatureBases(exon);
//						
//						boolean isForwardFeature = (exon.strand != -1);
//
//						if (lastExon != null) {
//							if (isForwardFeature)
//								intronlength += exon.fmin - lastExon.fmax - 1;
//							else
//								intronlength += lastExon.fmin - exon.fmax - 1;
//
//							if (intronlength < 0)
//								intronlength = 0;
//						}
//
//						int mod;
//						int codonStart;
//
//						if (isForwardFeature) {
//
//							mod = (basePosition - exon.fmin - intronlength) % 3;
//							codonStart = basePosition - exon.fmin
//									- intronlength - mod;
//
//						} else {
//							mod = (exon.fmax - basePosition - intronlength) % 3;
//							codonStart = exon.fmax - basePosition
//									- intronlength - mod;
//						}
//
//						try {
//							if (codonStart + 3 > intronlength)
//								return 0;
//
//							char codon[] = featureBases
//									.substring(codonStart, codonStart + 3)
//									.toLowerCase().toCharArray();
//
//							char aaRef = AminoAcidSequence.getCodonTranslation(
//									codon[0], codon[1], codon[2]);
//
//							if (!isForwardFeature) {
//								variant = Bases.complement(variant);
//							}
//
//							codon[mod] = variant;
//							char aaNew = AminoAcidSequence.getCodonTranslation(
//									codon[0], codon[1], codon[2]);
//
//							if (aaNew == aaRef)
//								return 1;
//							else if (AminoAcidSequence.isStopCodon(aaNew))
//								return 2;
//							else
//								return 0;
//						} catch (Exception e) {
//
//							for (LocatedFeature x : feature.getExons()) {
//								logger.error(String.format("%s %s %s %s",
//										x.uniqueName, x.fmin, x.fmax,
//										exon.uniqueName.equals(x.uniqueName)));
//							}
//
//							logger.error(String
//									.format("%s %s %s %s %s %s",
//											feature.uniqueName, feature.fmin,
//											feature.fmax, intronlength,
//											codonStart, mod));
//
//							throw new RuntimeException(e);
//						}
//					}
//
//					lastExon = exon;
//				}
//			}
//		}
//
//		return 3;
//	}
//
//	private String getFeatureBases(LocatedFeature feature) {
//		return regionSequence.dna.substring(feature.fmin, feature.fmax);
//	}
//
//}
