package org.genedb.crawl.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties({"chado_id"})
public class Dbxref implements Serializable {

    public int chado_id;

	@XmlElement
	public Db db;

	@XmlAttribute
	public String accession;

	@XmlAttribute
    public String version;

	@XmlAttribute
    public String description;
}
