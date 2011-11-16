package org.genedb.crawl.elasticsearch.index.das;

import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.modelling.RegionFeatureBuilder;

import uk.ac.ebi.das.jdas.adapters.features.FeatureAdapter;
import uk.ac.ebi.das.jdas.exceptions.ValidationException;
import uk.ac.ebi.das.jdas.schema.entryPoints.SEGMENT;

/**
 * A utility to output the contents of a DAS source into a tab delimited text file. 
 * 
 * @author gv1
 *
 */
public class DASFileBuilder extends DASIndexBuilder {
    
    public void run () throws IOException, JAXBException, SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException, ValidationException {
        
        DasFetcher fetcher = new DasFetcher(url, source);
        
        for (SEGMENT segment : fetcher.getEntryPoints()) {
            
            if (region != null) {
                if (! region.equals(segment.getId())) {
                    continue;
                }
            }
            
            List<FeatureAdapter> features = fetcher.getFeatures(segment, segment.getStart(), segment.getStop());
            
            indexFeatures(null, segment, features);
        }
        
    }
    
    protected void indexFeatures(Organism o, SEGMENT segment, List<FeatureAdapter> features) throws ValidationException {
        
        for (FeatureAdapter featureAdapter : features) {
            
            int fmin = interbase ? featureAdapter.getStart() : featureAdapter.getStart() -1;
            int fmax = featureAdapter.getEnd();
            String id = featureAdapter.getId();
            String region = segment.getId();
            String type = featureAdapter.getType().getId();
            
            String phase = featureAdapter.getPhase();
            String strand = featureAdapter.getOrientation();
            String score = featureAdapter.getScore();
            
            
            System.out.println(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\tID=%s", region, source, type, fmin, fmax, score, strand, phase, id));
            
        }
        
    }
    
    public static void main(String[] args) throws Exception {
        new DASFileBuilder().prerun(args).closeIndex();
    }

    
}
