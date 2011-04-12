package org.genedb.crawl.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

public class MappedQueryRecordElementList {
	
	@XmlAttribute
	public String name;
	
	@XmlElements({
        @XmlElement(name="f", type=Integer.class),
        @XmlElement(name="f", type=String.class),
        @XmlElement(name="f", type=Float.class),
        @XmlElement(name="f", type=Double.class)
	})
	public List<?> fields;
	
}
