package org.genedb.crawl.business;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import  uk.ac.sanger.artemis.io.GFFStreamFeature;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class GFFFeature {
	
	private Logger logger = Logger.getLogger(GFFFeature.class);
	
	public String seqid;
	public String source;
	public String type;
	public int start;
	public int end;
	public String score;
	public Strand strand;
	public Integer phase;
	
	public GFFAttributeMap attributes = new GFFAttributeMap(this);
	
	public String id;
	
	public enum Strand {
		POSITIVE ("+"),
		NEGATIVE ("-"),
		NOT_STRANDED ("."),
		UNKNOWN ("?");
		
		private String text;
		
		Strand(String text) {
			this.text = text;
		}
		
		public String getStrand() {
			return text;
		}
		
		public int getStrandInt() {
			if (text.equals("+")) {
				return 1;
			} else if (text.equals("-")) {
				return 2;
			}
			return 0;
		}
		
		public static Strand fromText(String text) {
			if (text != null) {
				for (Strand b : Strand.values()) {
					if (text.equalsIgnoreCase(b.text)) {
						return b;
					}
				}
			}
			return null;
		}
		
	};
	
	public GFFFeature(String line) {
		this(line, true);
	}
	
	
	public GFFFeature(String line, boolean parseAttributes) {
		
		logger.trace(line);
		
		String[] columns = line.split("\t");
		
		seqid = columns[0];
		source = columns[1];
		type = columns[2];
		start = new Integer (columns[3]);
		end = new Integer (columns[4]);
		score = columns[5];
		
		strand = Strand.fromText(columns[6]);
		
		//logger.info(columns[7]);
		
		try {
			phase = Integer.parseInt( columns[7] );
		} catch (NumberFormatException nfe) {
			if (type.equals("exon") || type.equals("CDS")) {
				logger.warn(String.format("%s features should have phase : \n\t %s", type, line));
			}
		}
		
		//logger.info(seqid);
		
		String attrs = columns[8];
		
		if (! attrs.endsWith(";")) {
			attrs += ";";
		}
		
		if (parseAttributes) {
			this.attributes.parseAttributes(attrs);
		}
		
		
		//parseAttributes(attrs);
		
	}
	
	/*
	 * Pf3D7_01        chado   polypeptide     39205   40430   .       -       .       ID=PFA0010c:pep;Derives_from=PFA0010c:mRNA;Dbxref=OrthoMCLDB
	:PFA0010c%2CPlasmoDB:PFA0010c%2CUniProtKB:Q9NFB5;colour=2;private=ed+Skyes+2007;timelastmodified=06.07.2009+01:35:35+BST;blastp_file=/nfs/pa
	thdata/Plasmodium/falciparum/3D7/workshop/DATABASES/apicomplexans:blastp/MAL1.embl.seq.00149.out%2C%uniprot:blastp/MAL1.embl.seq.00002.out;f
	eature_id=810;isObsolete=false;literature=PMID:10562315%2CPMID:17719658%2CPMID:2842673%2CPMID:10430943;comment=rif+%28repetitive+intersperse
	d+family%29+genes+were+originally+identified+by+Weber%3B+they+encode+clonally+variant+RIFIN+proteins%2C+which+are+likely+expressed+on+the+in
	fected+erythrocyte+surface%2C+in+Maurer's+clefts+and+on+merozoites.+Originally+classed+with+stevors%2C+rifs+are+clearly+a+distinct+family%2C
	+with+150+or+so+copies+per+haploid+genome.+Surface+expression+indicates+possible+role+in+immune+evasion.;
	 * */
//	private void parseAttributes(String attributes) {
//		
//		int start = 0;
//		int end = attributes.indexOf(";");
//		
//		while (end > 0) {
//			
//			String attribute = attributes.substring(start, end);
//			
//			//logger.info(attribute);
//			
//			int equals = attribute.indexOf("=");
//			
//			if (equals == -1) {
//				start = end + 1;
//				end = attributes.indexOf(";", start);
//				continue;
//			}
//			
//			String key = attribute.substring(0, equals);
//			String value = GFFStreamFeature.decode(attribute.substring(equals+1));
//			
//			if (key.equals("gO")) {
//				logger.debug(value);
//			}
//			
//			this.attributes.put(key, value);
//			
//			start = end + 1;
//			end = attributes.indexOf(";", start);
//			
//		}
//		
//	}
//	
	
	public String toString() {
		StringBuffer s = new StringBuffer();
		
		s.append(seqid + "\t");
		s.append(source + "\t");
		s.append(type + "\t");
		s.append(start + "\t");
		s.append(end + "\t");
		s.append(score+ "\t");
		s.append(strand.getStrand() + "\t");
		s.append(phase + "\t");
		
		String[] attrs = new String[attributes.map.size()];
		int i = 0;
		for (Entry<String, Object> entry : attributes.map.entrySet() ) {
			attrs[i] = entry.getKey() + "=" + entry.getValue().toString();
			i++;
		}
		
		s.append(StringUtils.join(attrs, ";"));
		
		return s.toString();
	}
	
	
	public class GFFAttributeMap {
		public Map<String,Object> map = new LinkedHashMap<String, Object>();
		public boolean decode = true;
		
		public GFFFeature feature;
		
		public GFFAttributeMap() {
			// deliberately empty
		}
		
		public GFFAttributeMap(GFFFeature feature) {
			this.feature = feature;
		}
		
		public void put (String key, String obj) {
			
			if (obj.contains(";") && obj.contains(",")) {
				//logger.info("[" + key + "]" + obj);
				// then it's a list of records
				GFFAttributeMapList list = new GFFAttributeMapList();
				list.parseAttributes(obj);
				
				map.put(key, list);
				
			} else {
				map.put(key, obj);
				
				// attempt to store an ID for this feature.
				if (key.equals("ID")) {
					if (this.feature != null) {
						this.feature.id = obj;
					}
				}
				
			}
		}
		
		public void parseAttributes(String attrs) {
			//logger.info(attrs);
			
			int start = 0;
			int end = attrs.indexOf(";");
			
			while (end > 0) {
				
				String attribute = attrs.substring(start, end).trim();
				//logger.info(attribute);
				if (decode) {
					attribute = GFFStreamFeature.decode(attribute);
				}
				
				//logger.info(attribute);
				
				int equals = attribute.indexOf("=");
				
				if (equals == -1) {
					start = end + 1;
					end = attrs.indexOf(";", start);
					continue;
				}
				
				String key = attribute.substring(0, equals);
				String value = attribute.substring(equals+1);
				
				
				put(key, value);
				
				start = end + 1;
				end = attrs.indexOf(";", start);
				
			}
			
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			for (Entry<String,Object> entry : map.entrySet()) {
				sb.append(entry.getKey());
				sb.append("=");
				sb.append(entry.getValue());
			}
			if (decode) {
				return GFFStreamFeature.encode(sb.toString());
			}
			return sb.toString();
		}
		
	}
	
	public class GFFAttributeMapList {
		public List<GFFAttributeMap> list = new ArrayList<GFFAttributeMap>();
		
		public void parseAttributes(String attrs) {
			//logger.info(attrs);
			String[] split = attrs.split(",");
			
			for (String attr : split) {
				GFFAttributeMap map = new GFFAttributeMap();
				map.decode = false;
				map.parseAttributes(attr);
				list.add(map);
			}
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			for (GFFAttributeMap map : list) {
				sb.append(map.toString());
			}
			return sb.toString();
		}
	}
	
	
}
