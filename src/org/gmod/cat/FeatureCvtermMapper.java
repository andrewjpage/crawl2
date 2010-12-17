package org.gmod.cat;

import java.util.List;

import org.genedb.crawl.model.Dbxref;
import org.genedb.crawl.model.Pub;

public interface FeatureCvtermMapper {
	public List<Pub> featureCvTermPubs(int feature_cvterm_id);
	public List<Dbxref> featureCvTermDbxrefs(int feature_cvterm_id);
}
