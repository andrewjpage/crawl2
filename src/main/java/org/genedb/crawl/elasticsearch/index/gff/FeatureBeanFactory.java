package org.genedb.crawl.elasticsearch.index.gff;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.genedb.crawl.elasticsearch.index.gff.GFFFeature.GFFAttributeMap;
import org.genedb.crawl.elasticsearch.index.gff.GFFFeature.GFFAttributeMapList;
import org.genedb.crawl.model.Coordinates;
import org.genedb.crawl.model.Cv;
import org.genedb.crawl.model.Cvterm;
import org.genedb.crawl.model.Dbxref;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.Property;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.model.Orthologue;
import org.genedb.crawl.model.Pub;

public class FeatureBeanFactory {
	
	private Logger logger = Logger.getLogger(FeatureBeanFactory.class);
	
	private LocatedFeature feature;

	
	
	public LocatedFeature getFeature() {
		return feature;
	}
	
	public FeatureBeanFactory(Organism organism, String line) {
		
		GFFFeature gffFeature = new GFFFeature(line);
		
		feature = new LocatedFeature();
		feature.organism_id = organism.ID;
		
		feature.uniqueName = gffFeature.id;
		
		
		// for easy querying we are storing the locations of this feature as properties
		feature.fmin = gffFeature.start;
		feature.fmax = gffFeature.end;
		feature.region = gffFeature.seqid;
		feature.phase = gffFeature.phase.getPhase();
		feature.strand = gffFeature.strand.getStrandInt();
		
		// but for web service display we are also storing a coordinates array
		Coordinates coordinates = new Coordinates();
		feature.coordinates = new ArrayList<Coordinates>();
		feature.coordinates.add(coordinates);
		coordinates.region = gffFeature.seqid;
		coordinates.toplevel = true;
		if (gffFeature.phase != null) {
			coordinates.phase = gffFeature.phase.getPhase();
			coordinates.strand = gffFeature.strand.getStrandInt();
		}
		coordinates.fmin = gffFeature.start;
		coordinates.fmax = gffFeature.end;
		
		Cvterm type = new Cvterm();
		type.name = gffFeature.type;
		feature.type = type;
		
		feature.properties = new ArrayList<Property>();
		
		for (Entry<String, Object> entry : gffFeature.attributes.map.entrySet()) {
			
			String key = entry.getKey();
			Object value = entry.getValue();
			
			if (value instanceof GFFAttributeMapList) {
				GFFAttributeMapList submap = (GFFAttributeMapList) entry.getValue();
				
				if(key.equals("orthologous_to")) {
					
					logger.debug("Scanning orthologues for " + feature.uniqueName);
					
					Orthologue orthologue = null;
					
					if (feature.orthologues == null) {
						feature.orthologues = new ArrayList<Orthologue>();
					}
					
					
					for (GFFAttributeMap submapitem : submap.list) {
						
						for (Entry<String, Object> subattr : submapitem.map.entrySet()) {
							
							logger.debug(String.format("Orthologue %s >> %s.", subattr.getKey(), (String) subattr.getValue()));
							
							// a new orthologue must be generated for each link.
							if (subattr.getKey().contains("link")) {
								
								orthologue = new Orthologue();
								feature.orthologues.add(orthologue);
								
								String[] vals = ((String) subattr.getValue()).split(" ");
								
								if (vals.length == 2) {
									orthologue.uniqueName = vals[0];
									
									if (vals[1].contains("type=")) {
										orthologue.orthologyType = vals[1].replace("type=", "");
									}
									
									
								} else {
									orthologue.uniqueName = vals[1];
								}
								
							} if (subattr.getKey().equals("cluster_name")) {
								
								orthologue.clusterName = (String) subattr.getValue();
								
							} else if (subattr.getKey().equals("program")) {
								
								orthologue.program = (String) subattr.getValue();
								
							} else if (subattr.getKey().equals("product")) {
								
								orthologue.addProduct((String) subattr.getValue()); 
								
							}  
							
						}
					
					}
					
				} else if (key.equals("go")) {
					
					
					Cvterm cvterm = null;
					// feature.addTerm(cvterm);
					
					for (GFFAttributeMap submapitem : submap.list) {
						for (Entry<String, Object> subattr : submapitem.map.entrySet()) {
							
							String subattrval = (String) subattr.getValue();
							
							if (subattr.getKey().equals("aspect")) {
								
								cvterm = new Cvterm();
								feature.addTerm(cvterm);
								
								
								Cv cv = new Cv();
								if (subattrval.equals("P")) {
									cv.name = "biological_process";
								} else if (subattrval.equals("C")) {
									cv.name = "cellular_component";
								} else if (subattrval.equals("F")) {
									cv.name = "molecular_function";
								}
								
								cvterm.cv = cv;
								
								
							} else if (subattr.getKey().equals("GOid")) { 
								
								if (subattrval.contains("GO:")) {
									cvterm.accession =  subattrval.replace("GO:", "");
								} else {
									cvterm.accession =  subattrval;
								}
								
							} else if (subattr.getKey().equals("term")) {
								
								cvterm.name =  subattrval;
								
							} else if (subattr.getKey().equals("db_xref")) {
								
								Pub pub = new Pub();
								pub.accession = subattrval;
								pub.database = "pubmed";
								pub.uniqueName = subattrval;
								
								cvterm.addPub(pub);
							}
							
						}
					}
					
					
					
					
				} else {
					for (GFFAttributeMap submapitem : submap.list) {
						
						for (Entry<String, Object> subattr : submapitem.map.entrySet()) {
							
							
							if (subattr.getValue() instanceof String) {
								
								logger.debug(String.format("Subattribute %s : %s.", subattr.getKey(), (String) subattr.getValue()));
								
								Property fp = new Property();
								fp.name = key + "." + subattr.getKey();
								fp.value = (String) subattr.getValue();
								
								feature.properties.add(fp);
							}
						}
						
					}
				}
			
			} else {
				
				String stringValue = (String) value; 
				
				//logger.debug(key + ":" + value);
				
				if ( key.equals("derives_from") || key.equals("parent") || key.equals("part_of") )  {
					
				    
					feature.parent = stringValue;
					
					if (key.equals("parent")) {
					    feature.parentRelationshipType = "part_of";
					} else {
					    feature.parentRelationshipType = key;
					}
					
					logger.trace(String.format("Adding %s as a parent of %s, relationship %s.", stringValue, feature.uniqueName, key));
				
				} else if (key.equals("dbxref")) {	
					
					String[] refs = stringValue.split(",");
					for (String ref : refs) {
						String[] refSplit = ref.split(":");
						if (refSplit.length == 2) {
							Dbxref dbxref = new Dbxref();
							dbxref.database = refSplit[0];
							dbxref.accession = refSplit[1];								
							feature.addDbxref(dbxref);
						}							
					}
					
					
				} else if (key.equals("product")) {
					
					if (stringValue.contains("term=")) {
						String product = stringValue.replace("term=", "");
						feature.addProduct(product);
					} else {
						feature.addProduct(stringValue);
					}
					
				} else if (key.equals("timelastmodified")) {
					
					
					try {
						SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss z");
						Date date;
						date = df.parse(stringValue);
						feature.timelastmodified = date;
					} catch (ParseException e) {
						logger.error("Could not parse date : " + stringValue);
						e.printStackTrace();
					}
					
					
					//logger.error("feature.timelastmodified  " + feature.timelastmodified );
				
				} else if (key.equals("isobsolete")) {
					
					feature.isObsolete = Boolean.parseBoolean(stringValue);
				
				} else if (key.equals("translation")) {
					
					feature.residues = stringValue;
				
				} else if (key.equals("translation")) {
				    
					
				} else {
					
					Property fp = new Property();
					fp.name = key;
					fp.value = stringValue;
					
					feature.properties.add(fp);
					
				}
				
			}
		} 
		
	}
	
	
}
