package org.genedb.crawl.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class Feature {
	
	@XmlAttribute
	public String uniqueName;
	
	@XmlAttribute(required=false)
	public String name;
	
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
	public List<FeatureProperty> properties;
	
	@XmlElement(name="pub", required=false)
	@XmlElementWrapper(name="pubs")
	public List<Pub> pubs;
	
	@XmlElement(name="dbxref", required=false)
	@XmlElementWrapper(name="dbxrefs")
	public List<Dbxref> dbxrefs;
	
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
	
	@XmlElement(required=false, name="type.cvterm")
	public Cvterm type;
	
	@XmlElement(name="type", required=false)
	public String getTypeName() {
		if (type != null) {
			return type.name;
		}
		return null;
	}
	
	@XmlElement(name="relationship", required=false)
	@XmlElementWrapper(name="relationships")
	public List<FeatureRelationship> relationships;
	
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
	public List<String> products;
	
	@XmlElement(required=false)
	public String relationship;
	
	
}
