package org.genedb.crawl.modelling;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.genedb.crawl.model.Coordinates;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.LocatedFeature;

public class LocatedFeatureUtil<T extends Feature> {
	
	private static Logger logger = Logger.getLogger(LocatedFeatureUtil.class);
	
	public static LocatedFeature fromFeature(Feature feature) {
		return fromFeature(feature, new LocatedFeature());
	}
	
	public static LocatedFeature fromFeature(Feature from, LocatedFeature to) {
		
		/**
		 * This might fail if you use a from feature that has properties not present in to. 
		 */
		for (Field field : from.getClass().getFields()) {
			
			try {
				Object o = field.get(from);
				if (o != null) {
					field.set(to, o);
				}
				
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		boolean found = false;
		if (from.coordinates != null) {
			for (Coordinates c : from.coordinates) {
			    
			    if (c == null)
			        continue;
				
			    if (c.toplevel == null)
			        continue;
			    
			    if (c.toplevel) {
					to.fmax = c.fmax;
					to.fmin = c.fmin;
					to.region = c.region;
					to.phase = c.phase;
					to.strand = c.strand;
					found = true;
					break;
				}
			}
		}
		
		if (! found) {
			logger.warn("Feature " + from.uniqueName + "does not appear to have any coordinates that are present on a top level feature. Probablty an orthologue or paralogue.");
		}
		
		return to;
	}
	
	public static <T extends LocatedFeature> T fromFeatureCoordinatesOnly(LocatedFeature from, T to) {
	    /**
         * This might fail if you use a from feature that has properties not present in to. 
         */
        for (Field field : from.getClass().getFields()) {
            
            try {
                Object o = field.get(from);
                if (o != null) {
                    field.set(to, o);
                }
                
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        boolean found = false;
        if (from.coordinates != null) {
            for (Coordinates c : from.coordinates) {
                
                if (c == null)
                    continue;
                
                if (c.toplevel == null)
                    continue;
                
                if (c.toplevel) {
                    to.fmax = c.fmax;
                    to.fmin = c.fmin;
                    to.region = c.region;
                    to.phase = c.phase;
                    to.strand = c.strand;
                    found = true;
                    break;
                }
            }
        }
        
        if (! found) {
            logger.warn("Feature " + from.uniqueName + "does not appear to have any coordinates that are present on a top level feature. Probablty an orthologue or paralogue.");
        }
        
        return to;
    }
    
	public static <T extends LocatedFeature> void copyCoordinates(LocatedFeature from, T to) {
	    to.fmax = from.fmax;
        to.fmin = from.fmin;
        to.region = from.region;
        to.phase = from.phase;
        to.strand = from.strand;
        to.coordinates = new ArrayList<Coordinates>();
        if (from.coordinates != null) {
            for (Coordinates c : from.coordinates) {
                to.coordinates.add(c);
            }
        }
	}
	
	
	public static <T extends LocatedFeature> T fromFeature(LocatedFeature from, T to) {
        
        /**
         * This might fail if you use a from feature that has properties not present in to. 
         */
        for (Field field : from.getClass().getFields()) {
            
            try {
                Object o = field.get(from);
                if (o != null) {
                    field.set(to, o);
                }
                
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        boolean found = false;
        if (from.coordinates != null) {
            for (Coordinates c : from.coordinates) {
                
                if (c == null)
                    continue;
                
                if (c.toplevel == null)
                    continue;
                
                if (c.toplevel) {
                    to.fmax = c.fmax;
                    to.fmin = c.fmin;
                    to.region = c.region;
                    to.phase = c.phase;
                    to.strand = c.strand;
                    found = true;
                    break;
                }
            }
        }
        
        if (! found) {
            logger.warn("Feature " + from.uniqueName + "does not appear to have any coordinates that are present on a top level feature. Probablty an orthologue or paralogue.");
        }
        
        return to;
    }
	

}
