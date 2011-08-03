package uk.ac.sanger.artemis.components.variant;

import java.util.List;

import uk.ac.sanger.artemis.Feature;
import uk.ac.sanger.artemis.FeatureVector;

public class MyVCFRecord extends VCFRecord {
	
	
	public short isSynonymous(List<CDSFeature> features, int basePosition)
	  {
	    char variant = getAlt().toString().toLowerCase().charAt(0);
	    
	    for (CDSFeature f : features) {
	    	short isSyn = checkSyn(f, basePosition, variant);
	    	if(isSyn > - 1)
		    	return isSyn;
	    }
	    
	    return 3;
	  }
	
}
