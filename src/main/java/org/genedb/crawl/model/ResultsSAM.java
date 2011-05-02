package org.genedb.crawl.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class ResultsSAM extends Results {
	
	@XmlElement(name="file", type=Alignment.class)
	@XmlElementWrapper(name="files")
	public List<Alignment> files;
	
	@XmlElement()
	public MappedCoverage coverage;
	
	@XmlElement()
	public MappedQuery query;
	
	@XmlElement()
	public List<MappedSAMSequence> sequences;
	
	@XmlElement()
	public MappedSAMHeader header;
	
	
}
