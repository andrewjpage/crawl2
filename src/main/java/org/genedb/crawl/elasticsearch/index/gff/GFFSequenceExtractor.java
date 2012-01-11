package org.genedb.crawl.elasticsearch.index.gff;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.genedb.crawl.elasticsearch.index.NonDatabaseDataSourceIndexBuilder;

public class GFFSequenceExtractor {

    private static Logger logger               = Logger.getLogger(GFFSequenceExtractor.class);

    StringBuffer          sequenceStringbuffer = new StringBuffer();

    public synchronized String read(String regionFilePath, String sequenceNameRequested) throws IOException {
        
        File regionFile = new File(regionFilePath);
        BufferedReader fileReader = NonDatabaseDataSourceIndexBuilder.getReader(regionFile);
        
        try {

            String line = "";
            boolean currentSequenceMatches = false;

            while ((line = fileReader.readLine()) != null) {

                if (line.startsWith(">")) {
                    String sequenceName = line.substring(1);
                    
                    /* we ignore everything after a space */
                    int spacePos = sequenceName.indexOf(" ");
                    if (spacePos != -1) {
                        sequenceName = sequenceName.substring(0, spacePos);
                    }
                    
                    currentSequenceMatches = sequenceName.equals(sequenceNameRequested);
                    logger.info(String.format("Foun %s", sequenceName));

                } else if (currentSequenceMatches) {
                    sequenceStringbuffer.append(line);
                }
            }

        } finally {
            fileReader.close();
        }

        logger.info(String.format("Sequence length %s", sequenceStringbuffer.length()));

        return sequenceStringbuffer.toString();

    }

}
