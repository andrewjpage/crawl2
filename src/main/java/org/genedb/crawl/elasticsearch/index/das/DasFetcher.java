package org.genedb.crawl.elasticsearch.index.das;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import uk.ac.ebi.das.jdas.adapters.features.DasGFFAdapter;
import uk.ac.ebi.das.jdas.adapters.features.FeatureAdapter;
import uk.ac.ebi.das.jdas.adapters.sequence.DasSequenceAdapter;
import uk.ac.ebi.das.jdas.client.FeaturesClient;
import uk.ac.ebi.das.jdas.client.SequenceClient;
import uk.ac.ebi.das.jdas.exceptions.ValidationException;
import uk.ac.ebi.das.jdas.schema.entryPoints.DASEP;
import uk.ac.ebi.das.jdas.schema.entryPoints.ENTRYPOINTS;
import uk.ac.ebi.das.jdas.schema.entryPoints.SEGMENT;
import uk.ac.ebi.das.jdas.schema.sequence.DASSEQUENCE;
import uk.ac.ebi.das.jdas.schema.sequence.SEQUENCE;

public class DasFetcher {
	
	private static Logger logger = Logger.getLogger(DasFetcher.class);
	
	public URL url;
	public String source;
	
	public DasFetcher(URL url, String source) {
		this.url = url;
		this.source = source;
	}
	
	public List<SEGMENT> getEntryPoints() throws MalformedURLException, JAXBException {
		DASEP dasEP = DASEP(getSourceURL());
		
		ENTRYPOINTS entryPoints = dasEP.getENTRYPOINTS();
		
		List<SEGMENT> segments = new ArrayList<SEGMENT>();
		
		for (Object segmentObject : entryPoints.getContent()) {
			
			try {
				uk.ac.ebi.das.jdas.schema.entryPoints.SEGMENT segment =
					(uk.ac.ebi.das.jdas.schema.entryPoints.SEGMENT) segmentObject;
				segments.add(segment);
				logger.debug(String.format("ID %s - %d %d", segment.getId(), segment.getStart(),segment.getStop()));
			} catch (Exception e){
				logger.warn(e.getMessage());
			}
			
		}
		
		return segments;
	}
	
	public String getSequence(SEGMENT segment, BigInteger bigInteger, BigInteger bigInteger2) throws MalformedURLException, JAXBException {
		
		String query = String.format("%s:%d-%d", segment.getId(), bigInteger, bigInteger2);
		logger.debug(query);
		
		SequenceClient sequenceClient = new SequenceClient();
		List<String> segments = new ArrayList<String>();
		segments.add(query);
		
		DasSequenceAdapter dasSequenceAdaptor = sequenceClient.fetchData(getSourceURL(), segments);
		
		return (String) dasSequenceAdaptor.getSequence().get(0).getContent();
	}
	
	public List<FeatureAdapter> getFeatures(SEGMENT segment, BigInteger bigInteger, BigInteger bigInteger2) throws MalformedURLException, JAXBException, ValidationException {
		
		String query = String.format("%s:%d-%d", segment.getId(), bigInteger, bigInteger2);
		logger.debug(query);
		
		FeaturesClient featuresClient = new FeaturesClient();
        List<String> segments = new ArrayList<String>();
        segments.add(query);
        DasGFFAdapter dasGFF = featuresClient.fetchData(getSourceURL(), segments);
        DasGFFAdapter.SegmentAdapter segmentAdaptor = dasGFF.getGFF().getSegment().get(0);
        return segmentAdaptor.getFeature();
	}
	
	private String getServerURL() {
		return url.toString();
	}
	
	private String getSourceURL() {
		return url.toString() + "/" + source.toString();
	}
	
	private static DASEP DASEP(String serverURL) throws JAXBException, MalformedURLException {
		JAXBContext jc = JAXBContext.newInstance("uk.ac.ebi.das.jdas.schema.entryPoints");
		Unmarshaller unmarshaller = jc.createUnmarshaller();
        String url = serverURL + "/entry_points";
        return (DASEP) unmarshaller.unmarshal(new URL(url));
	}
	
}
