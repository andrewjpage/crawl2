package org.genedb.crawl.elasticsearch.index.gff;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.genedb.crawl.elasticsearch.mappers.ElasticSearchFeatureMapper;
import org.genedb.crawl.elasticsearch.mappers.ElasticSearchRegionsMapper;
import org.genedb.crawl.model.Cvterm;
import org.genedb.crawl.model.Feature;
import org.genedb.crawl.model.LocatedFeature;
import org.genedb.crawl.model.Organism;
import org.genedb.crawl.modelling.LocatedFeatureUtil;
import org.genedb.crawl.modelling.RegionFeatureBuilder;

public class GFFAnnotatationAndFastaExtractor {

    private static Logger             logger      = Logger.getLogger(GFFAnnotatationAndFastaExtractor.class);
    ElasticSearchFeatureMapper        featureMapper;
    ElasticSearchRegionsMapper        regionsMapper;

    List<RegionFeatureBuilder>        sequences   = new ArrayList<RegionFeatureBuilder>();
    Map<String, LocatedFeature>       features    = new HashMap<String, LocatedFeature>();
    Map<String, LocatedFeature>       genes       = new HashMap<String, LocatedFeature>();
    Map<String, List<LocatedFeature>> transcripts = new HashMap<String, List<LocatedFeature>>();

    public GFFAnnotatationAndFastaExtractor(BufferedReader buf, Organism organism, ElasticSearchFeatureMapper featureMapper, ElasticSearchRegionsMapper regionsMapper) throws IOException {

        this.featureMapper = featureMapper;
        this.regionsMapper = regionsMapper;

        try {

            String line = "";
            boolean parsingAnnotations = true;
            RegionFeatureBuilder sequence = null;

            while ((line = buf.readLine()) != null) {
                logger.debug(line);

                if (line.startsWith("##sequence-region")) {
                    parsingAnnotations = true;
                }

                if (line.contains("##FASTA")) {
                    parsingAnnotations = false;
                }

                if (line.startsWith("#")) {
                    continue;
                }

                if (parsingAnnotations) {

                    LocatedFeature feature = new FeatureBeanFactory(organism, line).getFeature();

                    if (feature.type.name.equals("CDS")) {
                        logger.debug("changing type from CDS to exon");
                        feature.type.name = "exon";
                    }

                    LocatedFeature existingFeature = features.get(feature.uniqueName);

                    if (existingFeature == null) {
                        features.put(feature.uniqueName, feature);

                        // we create genes and transcripts for orphan exons
                        if (feature.type.name.equals("exon") && feature.parent == null) {
                            createGeneModelWithExon(feature);
                        } else {
                            createOrUpdate(feature);
                        }

                    } else {

                        // GFFs can reuse the same ID on different lines
                        // this means the same feature has different coordinates
                        // (and different annotations)
                        // in this case we check to see if this is a CDS, and if
                        // a gene model exists to append this to
                        // otherwise we just bolt on its coordinates

                        LocatedFeature gene = genes.get(feature.uniqueName);

                        // if it's an exon, and a gene for it exists, then
                        // append it to the model
                        if (feature.type.name.equals("exon") && gene != null) {
                            updateGeneModelWithExon(gene, feature);

                        } else if (feature.fmin != existingFeature.fmin || feature.fmax != existingFeature.fmax) {

                            // else, just add its coordinates (as long as they
                            // are not already there)
                            // for now we don't store any other info

                            logger.info("adding extra coordinates to " + existingFeature.uniqueName + " : " + feature.coordinates.get(0).fmin + "-" + feature.coordinates.get(0).fmax);

                            existingFeature.coordinates.add(feature.coordinates.get(0));
                            feature = existingFeature;

                        }

                    }

                } else {

                    if (line.startsWith(">")) {
                        String sequenceName = line.substring(1);

                        /* we ignore everything after a space */
                        int spacePos = sequenceName.indexOf(" ");
                        if (spacePos != -1) {
                            sequenceName = sequenceName.substring(0, spacePos);
                        }

                        sequence = new RegionFeatureBuilder(sequenceName, organism.ID);
                        logger.debug("Parsing sequence : " + sequenceName);
                        sequences.add(sequence);

                    } else if (sequence != null) {
                        sequence.addSequence(line);
                    }

                }

            }

            for (RegionFeatureBuilder regionBuilder : sequences) {
                Feature region = regionBuilder.getRegion();
                regionsMapper.createOrUpdate(region);
            }

        } finally {
            features = null;
            sequences = null;
            buf.close();
        }

    }

    private void createGeneModelWithExon(LocatedFeature feature) {

        assert (feature.type.name.equals("exon"));

        LocatedFeature gene = makeGene(feature);

        LocatedFeature transcript = makeTranscript(gene, null);
        LocatedFeature exon = makeExon(feature, transcript);
        LocatedFeature polypeptide = makePolypeptide(feature, transcript);

        createOrUpdate(new LocatedFeature[] { gene, transcript, exon, polypeptide });

    }

    private void updateGeneModelWithExon(LocatedFeature gene, LocatedFeature feature) {

        assert (feature.type.name.equals("exon"));

        LocatedFeature transcript = makeTranscript(gene, feature);

        int minFmin = gene.fmin;
        int maxFmax = gene.fmax;

        for (LocatedFeature t : transcripts.get(gene.uniqueName)) {
            if (t.fmin < minFmin)
                minFmin = t.fmin;
            if (t.fmax > maxFmax)
                maxFmax = t.fmax;
        }

        gene.fmin = minFmin;
        gene.fmax = maxFmax;

        gene.coordinates.get(0).fmin = minFmin;
        gene.coordinates.get(0).fmax = maxFmax;

        logger.info("adding new exon to " + gene.uniqueName + " : " + feature.uniqueName + " " + feature.coordinates.get(0).fmin + "-" + feature.coordinates.get(0).fmax);
        logger.info("reset gene coordinates to " + gene.fmin + "-" + gene.fmax);

        LocatedFeature exon = makeExon(feature, transcript);
        LocatedFeature polypeptide = makePolypeptide(feature, transcript);

        createOrUpdate(new LocatedFeature[] { gene, transcript, exon, polypeptide });

    }

    private LocatedFeature makeGene(LocatedFeature feature) {

        assert (feature.type.name.equals("exon"));

        LocatedFeature gene = new LocatedFeature();
        LocatedFeatureUtil.copyCoordinates(feature, gene);
        gene.uniqueName = feature.uniqueName;
        gene.type = new Cvterm();
        gene.type.name = "gene";

        genes.put(gene.uniqueName, gene);
        transcripts.put(gene.uniqueName, new ArrayList<LocatedFeature>());

        return gene;

    }

    private LocatedFeature makeTranscript(LocatedFeature gene, LocatedFeature alternativeCoordinates) {
        
        List<LocatedFeature> geneTranscripts = transcripts.get(gene.uniqueName);

        int index = geneTranscripts.size() + 1;

        LocatedFeature transcript = new LocatedFeature();

        if (alternativeCoordinates != null) {
            LocatedFeatureUtil.copyCoordinates(alternativeCoordinates, transcript);
        } else {
            LocatedFeatureUtil.copyCoordinates(gene, transcript);
        }

        transcript.type = new Cvterm();
        transcript.type.name = "mRNA";
        transcript.uniqueName = gene.uniqueName + "." + index;
        transcript.parent = gene.uniqueName;
        transcript.parentRelationshipType = "part_of";
        
        geneTranscripts.add(transcript);

        return transcript;
    }

    private LocatedFeature makeExon(LocatedFeature originalGFFFeature, LocatedFeature transcript) {

        LocatedFeature exon = new LocatedFeature();
        LocatedFeatureUtil.copyCoordinates(originalGFFFeature, exon);

        exon.type = new Cvterm();
        exon.type.name = "exon";
        exon.uniqueName = transcript.uniqueName + ":exon";
        exon.parent = transcript.uniqueName;
        exon.parentRelationshipType = "part_of";
        
        return exon;
    }

    private LocatedFeature makePolypeptide(LocatedFeature originalGFFFeature, LocatedFeature transcript) {

        LocatedFeature polypeptide = LocatedFeatureUtil.fromFeature(originalGFFFeature, new LocatedFeature());

        polypeptide.type = new Cvterm();
        polypeptide.type.name = "polypeptide";

        polypeptide.uniqueName = transcript.uniqueName + ":pep";
        polypeptide.parent = transcript.uniqueName;
        polypeptide.parentRelationshipType = "derives_from";

        return polypeptide;
    }

    private void createOrUpdate(LocatedFeature[] features) {
        for (LocatedFeature feature : features)
            createOrUpdate(feature);
    }

    private void createOrUpdate(LocatedFeature feature) {
        logger.info(info(feature));
        featureMapper.createOrUpdate(feature);
    }

    private String info(LocatedFeature f) {
        return (f.uniqueName + " " + f.fmin + "-" + f.fmax + " " + f.type.name);
    }

}
