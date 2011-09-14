package org.genedb.crawl.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import uk.ac.sanger.artemis.components.variant.VariantReaderAdapter;

/**
 * Represents a VCF or BVF file.
 * @author gv1
 *
 */
public class Variant extends BioDataFile {
	
    private List<MappedSAMSequence> sequences;
    private VariantReaderAdapter reader;
    
	@XmlTransient
	public VariantReaderAdapter getReader() throws IOException {
	    if (reader == null)
	        reader = VariantReaderAdapter.getReader(file);
		return reader;
	}

    @Override
    public List<MappedSAMSequence> getSequences() throws IOException { 
        return sequences;
    }
    
    @Override
    public void init() throws IOException {
        
        // we load these at startup, and close them again, to avoid the too many open files problam
        
        sequences = new ArrayList<MappedSAMSequence>();
        
        reader = getReader(); 
        
        for (String name : reader.getSeqNames()) {
            MappedSAMSequence mss = new MappedSAMSequence();
            mss.name = name;
            sequences.add(mss);
        }
        
        reader.close();
        reader = null;
    }
	
}
