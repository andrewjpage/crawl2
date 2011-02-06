package org.genedb.crawl.search.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.genedb.crawl.business.GFFAnnotatationAndFastaExtractor;
import org.genedb.crawl.business.GFFFeature;
import org.genedb.crawl.business.GFFFeature.GFFAttributeMap;
import org.genedb.crawl.business.GFFFeature.GFFAttributeMapList;
import org.genedb.crawl.model.Coordinates;
import org.genedb.crawl.model.Cv;
import org.genedb.crawl.model.Cvterm;
import org.genedb.crawl.model.Dbxref;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.FeatureProperty;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.Orthologue;
import org.genedb.crawl.model.Pub;

public class GFFFileToFeatureListConverter {
	
	private Logger logger = Logger.getLogger(GFFFileToFeatureListConverter.class);
	
	GoGetter goGetter;
	
	public List<Feature> features = new ArrayList<Feature>();
	
	public GFFFileToFeatureListConverter(File gffFile, File tmpFolder, GoGetter goGetter) throws IOException {
		
		this.goGetter = goGetter;
		
		GFFAnnotatationAndFastaExtractor extractor = new GFFAnnotatationAndFastaExtractor(gffFile, tmpFolder);
		
		File annotationFile = extractor.getAnnotationFile();
		// File fastaFile = extractor.getFastaFile();
		
		parseFile(annotationFile);
		
	}
	
	
	
	public void parseFile(File file) throws IOException {
		
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line;
		while((line = br.readLine()) != null) { 
			
			//logger.info(line);
			
			GFFFeature gffFeature = new GFFFeature(line);
			
			logger.info("Generating Feature :" + gffFeature.id);
			
			LocatedFeature feature = new LocatedFeature();
			feature.uniqueName = gffFeature.id;
			features.add(feature);
			
			
			// for easy querying we are storing the locations of this feature as properties
			feature.fmin = gffFeature.start;
			feature.fmax = gffFeature.end;
			feature.region = gffFeature.seqid;
			
			
			// but for web service display we are also storing a coordinates array
			Coordinates coordinates = new Coordinates();
			feature.coordinates = new ArrayList<Coordinates>();
			feature.coordinates.add(coordinates);
			coordinates.region = gffFeature.seqid;
			if (gffFeature.phase != null) {
				coordinates.phase = gffFeature.phase;
			}
			coordinates.fmin = gffFeature.start;
			coordinates.fmax = gffFeature.end;
			
			
			
			feature.properties = new ArrayList<FeatureProperty>();
			
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
						
					} else if (key.equals("gO")) {
						
						
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
									
									FeatureProperty fp = new FeatureProperty();
									fp.name = key + "." + subattr.getKey();
									fp.value = (String) subattr.getValue();
									
									feature.properties.add(fp);
								}
							}
							
						}
					}
				
				} else {
					
					String stringValue = (String) value; 
					
					if ( key.equals("Derives_from") || key.equals("Parent") || key.equals("Part_of") )  {
						
						feature.parent = stringValue;
						feature.parentRelationshipType = key;
						
						logger.trace(String.format("Adding %s as a parent of %s.", stringValue, feature.uniqueName));
					
					} else if (key.equals("Dbxref")) {	
						
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
						
						
						
					} else {
						
						FeatureProperty fp = new FeatureProperty();
						fp.name = key;
						fp.value = stringValue;
						
						feature.properties.add(fp);
						
					}
					
				}
			} 
			
		}
		
	}
	
}
