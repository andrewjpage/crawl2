package org.genedb.crawl.business;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class GenerateLuceneOrganism {
	
	private Logger logger = Logger.getLogger(GenerateLuceneOrganism.class);
	
	private OrganismOptions options;
	private File indexDirectory;
	private IndexWriter writer;

	private IndexReader reader;
	
	public static final String prefix = "organism."; 
	final String common_name_field = prefix + "common_name";
	
	public static class OrganismOptions {

		@Option(name = "-h", aliases = {"--help"}, usage = "Print help")
	    public boolean help;
		
		@Option(name = "-i", aliases = {"--index_directory"}, usage = "The location of the index folder", required=true )
	    public String index_directory;
		
		@Option(name = "-c", aliases = {"--common_name"}, usage = "The organism common name", required=true )
	    public String common_name;
		
		@Option(name = "-g", aliases = {"--genus"}, usage = "The organism genus")
	    public String genus;
		
		@Option(name = "-s", aliases = {"--species"}, usage = "The organism species")
	    public String species;
		
		@Option(name = "-t", aliases = {"--taxon_id"}, usage = "The organism taxon ID")
	    public String taxon_id;
		
		@Option(name = "-tt", aliases = {"--translation_table"}, usage = "The organism translation table")
	    public String translation_table;
		
		@Argument
		public List<String> argument;
	}
	
	public GenerateLuceneOrganism(OrganismOptions options) {
		this.options = options;
	}
	
	/**
	 * @param args
	 * @throws ArgumentValidationException 
	 */
	public static void main(String[] args) {
		
		OrganismOptions options = new OrganismOptions();
        CmdLineParser parser = new CmdLineParser(options);
		
        try {
            parser.parseArgument(args);

            if(options.help) {
            	parser.setUsageWidth(80);
            	parser.printUsage(System.out);
            	System.exit(0);
            }
            
            GenerateLuceneOrganism generator = new GenerateLuceneOrganism(options);
            generator.run();

        } catch (CmdLineException e) {
        	System.out.println(e.getMessage());
            parser.setUsageWidth(80);
            parser.printUsage(System.out);
            System.exit(1);

        } catch (IOException e) {
			e.printStackTrace();
			parser.setUsageWidth(80);
            parser.printUsage(System.out);
            System.exit(1);
		}
		
	}
	
	void run() throws IOException {
		
		try {
		
			indexDirectory = new File(options.index_directory);
			FSDirectory dir = FSDirectory.open(indexDirectory);
			
			
			reader =  IndexReader.open(dir, false);
			
			
			logger.info(System.getProperty("Reading: " + reader.directory()));
			
			
			
			Document doc;
			TopDocs td = searchCommonName(options.common_name);
			
			int pos = firstIndexOf(td);
			
			if (pos != -1 ) {
				
				doc = this.getDoc(pos);
				reader.deleteDocument(pos); // must delete the document from the index it to update
				
			} else {
				
				doc = new Document();
				doc.add(new Field(common_name_field, options.common_name, Field.Store.YES, Field.Index.NOT_ANALYZED));
				
				
			}
			
			
			reader.close();
			
			
			// the delete above relies on there not being an open writer
			writer = new IndexWriter(dir, new StandardAnalyzer(Version.LUCENE_30),
			        false, IndexWriter.MaxFieldLength.LIMITED); 
			
			logger.info("Writing: " + writer.getDirectory());
			
			addOrReplaceFieldValue(doc,  "genus", options.genus);
			addOrReplaceFieldValue(doc, "species", options.species);
			addOrReplaceFieldValue(doc, "taxon_id", options.taxon_id);
			addOrReplaceFieldValue(doc, "translation_table", options.translation_table);
			
			
			writer.addDocument(doc);
			writer.optimize();
			
		} finally {
			
			if (writer != null) {
				writer.close();
			}
			
			
		}
		
	}
	

	
	private void addOrReplaceFieldValue(Document doc, String fieldName, String value) {
		final String prefixedFieldName = prefix + fieldName;
		if (value != null) {
			Field field = doc.getField(prefixedFieldName);
			if (field != null) {
				doc.removeField(prefixedFieldName);
			}
			logger.info(String.format("Setting %s field: %s", prefixedFieldName, value));
			doc.add(new Field(prefixedFieldName, value, Field.Store.YES, Field.Index.NOT_ANALYZED));
		}
		
	}
	
	
	private TopDocs searchCommonName(String commonName) throws IOException {
		TermQuery find_common_name = new TermQuery(new Term(common_name_field, commonName));
		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs td = searcher.search(find_common_name, reader.maxDoc());
		return td;
	}
	
	private int firstIndexOf(TopDocs topDocs) throws IOException {
		if (topDocs.totalHits > 0) {
			return topDocs.scoreDocs[0].doc;
		}
		return -1;
	}
	
	private Document getDoc(int doc) throws CorruptIndexException, IOException {
		 return reader.document(doc);
	}

	
	
	
	
	
}
