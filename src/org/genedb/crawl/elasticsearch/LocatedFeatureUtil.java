package org.genedb.crawl.elasticsearch;

import java.lang.reflect.Field;

import org.genedb.crawl.model.Coordinates;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.LocatedFeature;

public class LocatedFeatureUtil {
	
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
		
		Coordinates c = feature.coordinates.get(0);
		
		lFeature.fmax = c.fmax;
		lFeature.fmin = c.fmin;
		lFeature.region = c.region;
		lFeature.phase = c.phase;
		lFeature.strand = c.strand;
		
		
		return lFeature;
	}
}
