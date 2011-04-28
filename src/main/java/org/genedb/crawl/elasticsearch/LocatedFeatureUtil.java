package org.genedb.crawl.elasticsearch;

import java.lang.reflect.Field;

import org.apache.log4j.Logger;
import org.genedb.crawl.model.Coordinates;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.LocatedFeature;

public class LocatedFeatureUtil {
	
	private static Logger logger = Logger.getLogger(LocatedFeatureUtil.class);
	
	public static LocatedFeature fromFeature(Feature feature) {
		
		LocatedFeature lFeature = new LocatedFeature();
		
		for (Field field : Feature.class.getFields()) {
			try {
				Object o = field.get(feature);
				if (o != null) {
					field.set(lFeature, o);
				}
				
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		boolean found = false;
		if (feature.coordinates != null) {
			for (Coordinates c : feature.coordinates) {
				if (c.toplevel) {
					lFeature.fmax = c.fmax;
					lFeature.fmin = c.fmin;
					lFeature.region = c.region;
					lFeature.phase = c.phase;
					lFeature.strand = c.strand;
					found = true;
					break;
				}
			}
		}
		
		if (! found) {
			logger.warn("Feature " + feature.uniqueName + "does not appear to have any coordinates that are present on a top level feature. Probablty an orthologue or paralogue.");
		}
		
		return lFeature;
	}
	

}
