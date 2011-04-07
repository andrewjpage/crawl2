package org.genedb.crawl.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.genedb.crawl.mappers.FeaturesMapper;
import org.genedb.crawl.model.Cvterm;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.HierarchyGeneFetchResult;

public class MapperUtil {
	
	private static Logger logger = Logger.getLogger(MapperUtil.class);
	
	private static Set<String> geneTypes = new HashSet<String>(Arrays.asList(new String[]{"gene", "pseudogene"}));
	
	public static final List<Feature> getGeneFeatures(FeaturesMapper featuresMapper, List<String> features) {
		Map <String, Feature> map = new HashMap<String, Feature>();
		List<HierarchyGeneFetchResult> possibleGenes = featuresMapper.getGeneForFeature(features);
		
		for (HierarchyGeneFetchResult result : possibleGenes) {
			
			String[] ftypes = new String[]{result.ftype,result.ftype2,result.ftype3};
			String[] fs = new String[]{result.f,result.f2,result.f3};
			
			if (! map.containsKey(result.f)) {
				//FeatureGenes fg = new FeatureGenes();
				//fg.feature = result.f;
				//fg.type = result.ftype;
				
				Feature feature = new Feature();
				feature.uniqueName = result.f;
				feature.genes = new ArrayList<String>();
				//feature.t
				
				Cvterm c = new Cvterm();
				c.name = result.ftype;
				
				map.put(result.f, feature);
			}
			
			for (int i = 0; i < ftypes.length; i++) {
				String ftype = ftypes[i];
				String f = fs[i];
				
				logger.debug(String.format("Type: %s, Feature %s .", ftype, f));
				
				if (geneTypes.contains(ftype)) {
					map.get(result.f).genes.add(f);
				}
				
			}
			
		}
		
		logger.debug(map);
		
		return new ArrayList<Feature>(map.values());
	}
	
}
