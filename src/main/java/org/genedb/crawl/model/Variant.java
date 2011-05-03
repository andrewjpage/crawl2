package org.genedb.crawl.model;

import java.io.IOException;

import javax.xml.bind.annotation.XmlTransient;

import uk.ac.sanger.artemis.components.variant.VariantReaderFacade;

/**
 * Represents a VCF or BVF file.
 * @author gv1
 *
 */
public class Variant extends BioDataFile {
	
	@XmlTransient
	public VariantReaderFacade getReader() throws IOException {
		return VariantReaderFacade.getReader(file);
	}
	
}