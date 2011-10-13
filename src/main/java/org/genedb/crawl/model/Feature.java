package org.genedb.crawl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.genedb.crawl.json.JsonDateSerializer;

public class Feature implements Serializable {
	
    private static final long serialVersionUID = 5345245255772822999L;

    @XmlAttribute
	public String uniqueName;
	
	@XmlAttribute(required=false)
	public String name;
	
	@XmlAttribute(required=false)
	public String residues;
	
	@XmlElement(required=false)
	public List<String> genes;
	
	@XmlAttribute(required=false)
	public Integer feature_id;
	
	@XmlAttribute(required=false)
	public Integer organism_id;
	
	@XmlElement(name="synonym", required=false)
	@XmlElementWrapper(name="synonyms")
	public List<Synonym> synonyms;
	
	@XmlElement(name="coordinate", required=false)
	@XmlElementWrapper(name="coordinates")
	public List<Coordinates> coordinates;
	
	@XmlElement(name="property", required=false)
	@XmlElementWrapper(name="properties")
	public List<Property> properties;
	
	@XmlElement(name="pub", required=false)
	@XmlElementWrapper(name="pubs")
	public List<Pub> pubs;
	
	@XmlElement(name="dbxref", required=false)
	@XmlElementWrapper(name="dbxrefs")
	public List<Dbxref> dbxrefs;
	
	@XmlElement(name="domain", required=false)
    @XmlElementWrapper(name="domains")
    public List<LocatedFeature> domains;
	
	@XmlElement(name="change", required=false)
	@XmlElementWrapper(name="changes")
	public List<Change> changes;
	
	@XmlElement(required=false)
	@JsonSerialize(using=JsonDateSerializer.class)
	public Date timelastmodified;
	
	@XmlElement(required=false)
	@JsonSerialize(using=JsonDateSerializer.class)
	public Date timeaccessioned;

    @XmlElement(required=false)
	public boolean isObsolete;
	
	public void addDbxref(Dbxref dbxref) {
		if (dbxrefs == null) {
			dbxrefs = new ArrayList<Dbxref>();
		}
		dbxrefs.add(dbxref);
	}
	
	@XmlElement(name="term", required=false, type=Cvterm.class)
	@XmlElementWrapper(name="terms")
	public List<Cvterm> terms;
	
	public void addTerm(Cvterm term) {
		if (terms == null) {
			terms = new ArrayList<Cvterm>();
		}
		terms.add(term);
	}
	
	@XmlElement(required=false)
	public Cvterm type;
	
	@XmlElement(name="relationship", required=false)
	@XmlElementWrapper(name="relationships")
	public List<FeatureRelationship> relationships;
	
	@XmlElement(name="parents")
	public List<Feature> parents;
	
	@XmlElement(name="children")
	public List<Feature> children;
	
	@XmlElement
	public Cvterm relationshipType;
	
	@XmlElement(required=false)
	public Organism organism;
	
	@XmlElement(name="orthologue", required=false)
	@XmlElementWrapper(name="orthologues")
	public List<Orthologue> orthologues;
	
	@XmlElement(name="analysisFeature", required=false)
	public AnalysisFeature analysisFeature;
	
	public void addProduct(String product) {
		if (products == null) {
			products = new ArrayList<String>();
		}
		products.add(product);
	}
	
	@XmlElement(name="product", required=false)
	@XmlElementWrapper(name="products")
	public List<String> products = new ArrayList<String>();
	
	@XmlElement(required=false)
	public String relationship;
	
	@XmlElement(required=false)
    public Integer count;
	
}
