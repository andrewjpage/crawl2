package org.genedb.crawl.bam;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
//import org.codehaus.jackson.type.TypeReference;
import org.genedb.crawl.json.JsonIzer;
import org.genedb.crawl.model.Alignment;
import org.genedb.crawl.model.AlignmentSequenceAlias;
import org.genedb.crawl.model.Alignments;

import org.genedb.crawl.model.Variant;

public class BioDataFileStoreInitializer {

    private static Logger               logger         = Logger.getLogger(BioDataFileStoreInitializer.class);

    private JsonIzer                    jsonIzer       = new JsonIzer();

    private BioDataFileStore<Alignment> alignmentStore = new BioDataFileStore<Alignment>();
    private BioDataFileStore<Variant>   variantStore   = new BioDataFileStore<Variant>();

    private Map<String, String>         sequences      = new HashMap<String, String>();
    private Alignments                  mergedAlignments;

    public BioDataFileStore<Alignment> getAlignments() {
        return alignmentStore;
    }

    public BioDataFileStore<Variant> getVariants() {
        return variantStore;
    }

    public void setAlignmentFiles(String alignmentFiles) throws JsonParseException, JsonMappingException, IOException {
        
        if (alignmentFiles == null) {
            return;
        }
        
        mergedAlignments = new Alignments();
        mergedAlignments.alignments = new ArrayList<Alignment>();
        mergedAlignments.variants = new ArrayList<Variant>();

        for (String alignmentFile : alignmentFiles.split(",")) {
            processFile(new File(alignmentFile.trim()));
        }
        
        if (mergedAlignments.alignments.size() > 0)
            alignmentStore = new BioDataFileStore<Alignment>(mergedAlignments.alignments, sequences);
        
        if (mergedAlignments.variants.size() > 0)
            variantStore = new BioDataFileStore<Variant>(mergedAlignments.variants, sequences);

    }

    private void processFile(File alignmentFile) throws JsonParseException, JsonMappingException, IOException {

        logger.info(String.format("Alignment file : %s", alignmentFile));
        
        if (! alignmentFile.isFile()) {
            logger.warn(alignmentFile +  " is not a file!");
            return;
        }

        logger.info("Making jsons for " + alignmentFile);

        Alignments currentAlignments = (Alignments) jsonIzer.fromJson(alignmentFile, Alignments.class);

        if (currentAlignments.sequences != null) {
            for (AlignmentSequenceAlias alias : currentAlignments.sequences) {
                sequences.put(alias.reference, alias.alignment);
            }
        }

        if (currentAlignments.alignments != null) {
            mergedAlignments.alignments.addAll(currentAlignments.alignments);
        }

        if (currentAlignments.variants != null) {
            mergedAlignments.variants.addAll(currentAlignments.variants);
        }
    }

}
