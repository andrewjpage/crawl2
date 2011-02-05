package org.genedb.crawl.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class Cvterm {
	
	
	public Cv cv;
	
	@XmlAttribute
	public String name;
	
	@XmlAttribute
	public String accession;
	
	/**
	 *  This is not a primitive boolean so it can be nullable (or else the GSON sets it to false if unset).
	 */
	@XmlAttribute(name="is_not")
	public Boolean is_not;
	
	@XmlElement(name="dbxref")
	@XmlElementWrapper(name="dbxrefs")
	public List<Dbxref> dbxrefs;
	
	@XmlElement(name="prop")
	@XmlElementWrapper(name="props")
	public List<CvtermProp> props;
	
	@XmlElement(name="pub")
	@XmlElementWrapper(name="pubs")
	public List<Pub> pubs = new ArrayList<Pub>();
	
	public void addPub(Pub pub) {
		if (pubs == null) {
			pubs = new ArrayList<Pub>();
		}
		pubs.add(pub);
	}
	
	
}
