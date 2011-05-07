package uk.ac.sanger.artemis.components.variant;

import java.util.Arrays;
import java.util.List;

import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.modelling.LocatedFeatureUtil;
import org.genedb.crawl.mappers.RegionsMapper;

public class GeneFeature extends LocatedFeature {
	
	
	private RegionsMapper regionsMapper;
	
	private List<LocatedFeature> exons;
	
	private final List<String> exonTypes = Arrays.asList(new String[]{"cds", "exon"});
	
	public GeneFeature(LocatedFeature feature, RegionsMapper regionsMapper) {
		// make a shallow copy of the feature
		LocatedFeatureUtil.fromFeature(feature, this);
		this.regionsMapper = regionsMapper;
	}
	
	public List<LocatedFeature> getExons() {
		if (exons == null) {
			exons = regionsMapper.locations(region, fmin, fmax, false, exonTypes);
		}
		return exons;
	}
	
	
	
}
