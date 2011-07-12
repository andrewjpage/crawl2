package org.genedb.crawl.elasticsearch.index;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.genedb.crawl.elasticsearch.index.json.ReferenceIndexBuilder;
import org.genedb.crawl.elasticsearch.mappers.ElasticSearchRegionsMapper;
import org.genedb.crawl.model.Alignments;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.Reference;
import org.genedb.crawl.model.Sequence;

import junit.framework.TestCase;

public class ReferenceTest extends TestCase {
	
	static Logger logger = Logger.getLogger(ReferenceTest.class);
	
	String propFile = "resource-elasticsearch-local.properties";
	String jsonFile = "src/test/resources/alignments-vrtrack.json";
	
	public void test1() throws Exception {
		
		String[] args = new String[] {
			"-pe", propFile,
			"-r" , jsonFile
		};
		
		ReferenceIndexBuilder builder = new ReferenceIndexBuilder();
		builder.prerun(args);
		
		ElasticSearchRegionsMapper regionsMapper = builder.regionsMapper;
		
		regionsMapper.waitForStatus(EnumSet.of(ClusterHealthStatus.GREEN, ClusterHealthStatus.YELLOW));
		
		Alignments store = builder.jsonIzer.fromStringOrFile(jsonFile, Alignments.class);
		
		List<String> excludes = new ArrayList<String>();
		excludes.add("non_existent_feature");
		
		Pattern p = Pattern.compile("ID=[^;]+");
		
		
		
		for (Reference r : store.references) {
			logger.info("verifying " + r.organism.common_name);
			String file = r.file;
			BufferedReader buf = builder.getReader(new File(file));
			
			Set<String> ids = new HashSet<String>();
			Map<String,String> idLines = new HashMap<String,String>();
			
			boolean fasta = false;
			
			String line = null;
			int featureLines = 0;
			while ((line=buf.readLine())!=null) {
				
				if (line.startsWith("##sequence-region")) {
					fasta = false;
					continue;
				}
				if (line.startsWith(">")) {
					fasta = true;
					continue;
				}
				
				if ( (!fasta) && (!line.startsWith("#")) && (!line.startsWith(">"))) {
					
					logger.info(line);
					
					Matcher m = p.matcher(line);
					m.find();
					
					
					
					String id = m.group();
					
					id = id.replaceFirst("ID=", "");
					
					if (id.startsWith("\"") && id.endsWith("\"")) {
						id = id.substring(1, id.length() - 1);
					}
					
					logger.info("Found id " + id);
					
					if (ids.contains(id)) {
						logger.warn("already seen " + id + " here " + idLines.get(id));
						logger.warn("now seen " + id + " here " + line);
						continue;
					}
					
					ids.add(id);
					idLines.put(id, line);
					
					featureLines++;
					
					
				}
			}
			
			
			
			int featureCount = 0;
			
			Set<String> locatedIDs = new HashSet<String>();
			
			List<Feature> regions = regionsMapper.inorganism(r.organism.ID, null, null, null);
			for (Feature region : regions) {
				Sequence sequence = regionsMapper.sequence(region.uniqueName);
				
				//logger.info(String.format("%s %s %s", region.uniqueName, 1, (int) sequence.length));
				List<LocatedFeature> locatedFeatures = regionsMapper.locations(
						region.uniqueName, 1, (int) sequence.length, true, excludes);
				//logger.info(locatedFeatures.size());
				
				
				for (LocatedFeature feature : locatedFeatures) {
					locatedIDs.add(feature.uniqueName);
					featureCount++;
				}
				
			}
			
			boolean allPresentAndAccountedFor = true;
			
			for (String id : ids) {
				if (locatedIDs.contains(id)) {
					logger.warn("Found id " + id + " in ES.");
				} else {
					logger.error("Did not find id " + id + " in ES!");
					logger.error(id + " : " + idLines.get(id));
					allPresentAndAccountedFor = false;
				}
			}
			
			assertTrue(allPresentAndAccountedFor);
			
			logger.info(ids.size() + " == " + locatedIDs.size());
			
			logger.info(String.format("%s GFF lines %d == %d features in ES %d", file, featureLines, featureCount, r.organism.ID));
			assertEquals(featureLines, featureCount);
			
		}
		
		builder.closeIndex();
		
	}
	
}
