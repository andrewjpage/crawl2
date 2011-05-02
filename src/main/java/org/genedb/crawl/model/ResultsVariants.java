package org.genedb.crawl.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class ResultsVariants  extends Results {
	
	@XmlElement(name="file", type=Variant.class)
	@XmlElementWrapper(name="files")
	public List<Variant> files;
	
	@XmlElement()
	public List<MappedSAMSequence> sequences;
	
	@XmlElement()
	public List<MappedVCFRecord> records;
}
