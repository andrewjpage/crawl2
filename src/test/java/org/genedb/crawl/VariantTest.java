package org.genedb.crawl;

import java.io.IOException;
import java.util.List;

import org.genedb.crawl.json.JsonIzer;
import org.genedb.crawl.model.MappedVCFRecord;

import uk.ac.sanger.artemis.components.variant.VariantReaderFacade;
import junit.framework.TestCase;

public class VariantTest extends TestCase {
	
	JsonIzer jsonIzer = JsonIzer.getJsonIzer();
	
	public void test1() throws IOException {
		VariantReaderFacade reader = VariantReaderFacade.getReader("http://www.genedb.org/artemis/NAR/Spneumoniae/4882_6_10_variant.bcf");
		run(reader);
	}
	
	public void test2() throws IOException {
		VariantReaderFacade reader = VariantReaderFacade.getReader("./src/test/resources/data/test.vcf.gz");
		run(reader);
	}
	
	private void run(VariantReaderFacade reader) throws IOException {
		System.out.println(reader.getSeqNames());
		
		for (String seqName : reader.getSeqNames()) {
			List<MappedVCFRecord> records = reader.query(seqName, 1, 100000);
			System.out.println(jsonIzer.toJson(records));
		}
	}
	
}
