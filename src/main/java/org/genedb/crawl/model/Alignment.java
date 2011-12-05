package org.genedb.crawl.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMSequenceRecord;

/**
 * Represents a BAM or SAM file.
 */
public class Alignment extends BioDataFile {

    private static Logger           logger = Logger.getLogger(Alignment.class);

    @XmlAttribute(required = false)
    public String                   index;

    @XmlAttribute(required = false)
    public String                   qc_status;

    private SAMFileReader           reader;
    private File                    bamFile;
    private File                    indexFile;
    private URL                     bamFileURL;

    private List<MappedSAMSequence> sequences;

    @Override
    public void init() throws IOException {
        
        // we get the sequences at startup, and the close the reader and file when done
        // to avoid too many optn files
        
        sequences = new ArrayList<MappedSAMSequence>();
        for (SAMSequenceRecord ssr : getReader().getFileHeader().getSequenceDictionary().getSequences()) {

            MappedSAMSequence mss = new MappedSAMSequence();
            mss.length = ssr.getSequenceLength();
            mss.name = ssr.getSequenceName();
            mss.index = ssr.getSequenceIndex();
            
            logger.debug(String.format("%s %s %s", mss.name, mss.index, mss.length));
            
            sequences.add(mss);
        }

        reader.close();
        reader = null;
        
        
        //logger.info("got the sequences, and closed the file");        
    }

    @XmlTransient
    public SAMFileReader getReader() throws IOException {

        //logger.debug(this);

        if (reader == null) {

            if (file == null) {
                throw new RuntimeException("Could not generate a SAMFileReader because neither a file or url has been specified.");
            }

            if (index != null) {
                logger.debug("getting index: " + index);
                indexFile = getFile(index);
            }

            logger.debug("getting bam: " + file);

            if (file.startsWith("http")) {

                bamFileURL = new URL(file);
                reader = new SAMFileReader(bamFileURL, indexFile, false);

            } else {

                bamFile = new File(file);
                reader = new SAMFileReader(bamFile, indexFile);
            }

        }

        return reader;
    }

    private File getFile(String path) throws IOException {

        File f = null;

        if (path.startsWith("http://")) {
            URL url = new URL(path);
            f = download(url);
        } else {
            f = new File(path);
        }

        return f;
    }

    private File download(URL url) throws IOException {

        String fileName = "/tmp/" + fileID;

        // logger.info(fileName);

        File f = new File(fileName);

        OutputStream out = new FileOutputStream(f);

        InputStream in = url.openStream();
        byte[] buf = new byte[4 * 1024];
        int bytesRead;
        while ((bytesRead = in.read(buf)) != -1) {
            out.write(buf, 0, bytesRead);
            // logger.info("reading");
        }

        out.close();

        logger.info(String.format("Downloaded %s to %s", url, f));

        return f;

    }

    @Override
    public List<MappedSAMSequence> getSequences() throws IOException {
        return sequences;
    }

}