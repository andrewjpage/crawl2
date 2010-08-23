package org.genedb.crawl.business;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;


import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.LucenePackage;
import org.genedb.crawl.business.GFFFeature.GFFAttributeMap;
import org.genedb.crawl.business.GFFFeature.GFFAttributeMapList;
import org.genedb.crawl.business.GenerateLuceneIndex.GFFFilter;


public class GFFIndexer {
	
	private Logger logger = Logger.getLogger(GFFIndexer.class);
	
	private File indexDirectory;
	private IndexWriter writer;
	
	
	public void setIndexDirectory(File indexDirectory) throws IOException {
		this.indexDirectory = indexDirectory;
		
		logger.info(LucenePackage.get().getImplementationVersion());
		
		logger.info("Opening " + indexDirectory);
		
		FSDirectory dir = FSDirectory.open(indexDirectory);
		
		
		writer = new IndexWriter(dir, new StandardAnalyzer(Version.LUCENE_CURRENT),
		        true, IndexWriter.MaxFieldLength.LIMITED); 
		
		logger.info("Opened " + writer.getDirectory());
		
	}
	
	public void indexFile(File file) throws IOException {
		
		
		TabixReader reader = new TabixReader(file.getAbsolutePath());
		
		String sequenceName = file.getName().replace(GFFFilter.extension, "");
		
		logger.info("Sequence name : " + sequenceName);
		TabixReader.Iterator iter = reader.query(sequenceName);
		
		String line;
		while((line=iter.next()) != null) {
			//logger.info(line);
			Document doc = new Document();
			
			GFFFeature feature = new GFFFeature(line);
			//logger.info(feature.attributes.get("ID"));
			
			doc.add(new Field("seqid", feature.seqid, Field.Store.YES, Field.Index.NOT_ANALYZED));
			doc.add(new Field("source", feature.source, Field.Store.YES, Field.Index.NOT_ANALYZED));
			
			doc.add(new Field("start", Integer.toString(feature.start), Field.Store.YES, Field.Index.NOT_ANALYZED));
			doc.add(new Field("end", Integer.toString(feature.end), Field.Store.YES, Field.Index.NOT_ANALYZED));
			
			doc.add(new NumericField("numeric_start").setIntValue(feature.start));
			doc.add(new NumericField("numeric_end").setIntValue(feature.end));
			
			doc.add(new Field("score", feature.score, Field.Store.YES, Field.Index.NOT_ANALYZED));
			doc.add(new Field("type", feature.type, Field.Store.YES, Field.Index.NOT_ANALYZED));
			
			doc.add(new Field("strand", feature.strand.getStrand(), Field.Store.YES, Field.Index.NOT_ANALYZED));
			doc.add(new Field("phase", feature.phase, Field.Store.YES, Field.Index.NOT_ANALYZED));
			
			for (Entry<String, Object> entry : feature.attributes.map.entrySet()) {
				
				if (entry.getValue() instanceof GFFAttributeMapList) {
					
					GFFAttributeMapList submap = (GFFAttributeMapList) entry.getValue();
					
					logger.info(entry.getKey());
					
					for (GFFAttributeMap submapitem : submap.list) {
						
						Document subdoc = new Document();
						
						subdoc.add(new Field("FEATURE_ID", feature.attributes.map.get("ID").toString(), Field.Store.YES, Field.Index.NOT_ANALYZED )) ;
						subdoc.add(new Field("DOC_TYPE", "SUBATTRIBUTE", Field.Store.YES, Field.Index.NOT_ANALYZED )) ;
						subdoc.add(new Field("DOC_KEY", entry.getKey(), Field.Store.YES, Field.Index.NOT_ANALYZED )) ;
						
						
						for (Entry<String, Object> subentry : submapitem.map.entrySet()) {
							
							final String subkey = subentry.getKey();
							final String subvalue = (String) subentry.getValue();
								
							//<Pknowlesi:PKH_125620 link	PKH_125620:pep type=orthologous_to>
							boolean is_link = subentry.getKey().contains(" link"); 
							logger.info(String.format("%s\t%s\t%s", is_link, subkey, subvalue));
							
							if (is_link) {
								
								String linkOrgName = subkey.substring(0, subkey.indexOf(":"));
								String linkGeneName = subkey.substring(subkey.indexOf(":") + 1, subkey.indexOf(" "));
								
								String linkPepName = subvalue.substring(0, subvalue.indexOf(" "));
								String linkTypeName = subvalue.substring(subvalue.indexOf("=") + 1);
								
								subdoc.add(new Field("LINK", "true", Field.Store.YES, Field.Index.NOT_ANALYZED )) ;
								
								subdoc.add(new Field("LINK_ORGANISM", linkOrgName, Field.Store.YES, Field.Index.NOT_ANALYZED )) ;
								subdoc.add(new Field("LINK_GENE", linkGeneName, Field.Store.YES, Field.Index.NOT_ANALYZED )) ;
								subdoc.add(new Field("LINK_PEPTIDE", linkPepName, Field.Store.YES, Field.Index.NOT_ANALYZED )) ;
								subdoc.add(new Field("LINK_TYPE", linkTypeName, Field.Store.YES, Field.Index.NOT_ANALYZED )) ;
								
								
							} else {
								subdoc.add(new Field(subentry.getKey(), subentry.getValue().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
							}
							
							
							
						}
						
						writer.addDocument(subdoc);
					}
					
					
					
				} else {
					doc.add(new Field(entry.getKey(), entry.getValue().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				}
				
			}
			
			
			writer.addDocument(doc);
		}
		
		int num = writer.numDocs();
		
		logger.info(num);
		
		writer.commit();
		
		//System.out.println(writer.getReader().directory());
		
		
	}
	
	public void closeIndex() throws CorruptIndexException, IOException {
		logger.info("Closing " + writer.getDirectory());
		writer.optimize();
		writer.close();

	}
	
}
