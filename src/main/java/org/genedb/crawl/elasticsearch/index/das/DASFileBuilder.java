package org.genedb.crawl.elasticsearch.index.das;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.genedb.crawl.model.Organism;
import org.kohsuke.args4j.Option;

import uk.ac.ebi.das.jdas.adapters.features.FeatureAdapter;
import uk.ac.ebi.das.jdas.exceptions.ValidationException;
import uk.ac.ebi.das.jdas.schema.entryPoints.SEGMENT;

/**
 * A utility to output the contents of a DAS source into a tab delimited text
 * file.
 * 
 * @author gv1
 * 
 */
public class DASFileBuilder extends DASIndexBuilder {

    private static Logger logger   = Logger.getLogger(DASFileBuilder.class);

    @Option(name = "-f", aliases = { "--file" }, usage = "The file to save the output to.", required = true)
    public String         file;

    @Option(name = "-seq", aliases = { "--sequence" }, usage = "Whether to fetch the sequence as well (untested).", required = false)
    public boolean        sequence = false;

    private DasFetcher    fetcher;
    private FileWriter    writer;

    public void run() throws IOException, JAXBException, SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException, ValidationException {

        fetcher = new DasFetcher(url, source);
        writer = new FileWriter(file);

        writer.append("##gff-version 3\n");

        List<SEGMENT> segments = fetcher.getEntryPoints();

        for (SEGMENT segment : segments) {
            
            if (region != null) {
                if (!region.equals(segment.getId())) {
                    continue;
                }
            }
            
            logger.info("Getting features for " + segment.getId());
            List<FeatureAdapter> features = fetcher.getFeatures(segment, segment.getStart(), segment.getStop());
            indexFeatures(null, segment, features);
        }

        /*
         * @TODO this has not been tested because the
         * http://das.sanger.ac.uk/das/pbg does not appear to support this
         * feature.
         */
        if (sequence) {

            writer.append("##FASTA");

            for (SEGMENT segment : segments) {

                writer.append("> " + segment.getId());
                BigInteger start = segment.getStart();
                BigInteger stop = segment.getStop();

                // @NOTE this is probably way too small a value for the
                // increment, causing too many requests
                // larger values will necessitate splitting the results on
                // multiple lines (which is not yet
                // done below).
                final BigInteger increment = BigInteger.valueOf(60);

                logger.info(String.format("Getting sequence for %s (%s-%s).", segment.getId(), start, stop));

                for (BigInteger x = start; x.compareTo(stop) < 0; x.add(increment)) {

                    BigInteger fmin = x;
                    BigInteger fmax = x.add(increment);

                    logger.info(String.format("%s-%s", fmin, fmax));

                    if (fmax.compareTo(stop) >= 0) {
                        fmax = stop;
                    }

                    String sequence = fetcher.getSequence(segment, fmin, fmax);
                    writer.append(sequence + "\n");

                }

            }
        }

        writer.close();
    }

    @Override
    protected void indexFeatures(Organism o, SEGMENT segment, List<FeatureAdapter> features) throws ValidationException, IOException {

        StringBuffer sb = new StringBuffer();

        for (FeatureAdapter featureAdapter : features) {

            // if the DAS source is not interbase, then must subtract one from
            // its fmin
            int fmin = interbase ? featureAdapter.getStart() : featureAdapter.getStart() - 1;

            int fmax = featureAdapter.getEnd();
            String id = featureAdapter.getId();
            String region = segment.getId();
            String type = featureAdapter.getType().getId();

            String phase = featureAdapter.getPhase();
            String strand = featureAdapter.getOrientation();
            String score = featureAdapter.getScore();
            
            sb.append(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\tID=%s\n", region, source, type, fmin, fmax, score, strand, phase, id));
            
        }
        
        writer.append(String.format("##sequence-region segment.getId() %s %s\n", segment.getStart(), segment.getStop()));
        writer.append(sb.toString());

    }

    public static void main(String[] args) throws Exception {
        new DASFileBuilder().prerun(args).closeIndex();
    }

}
