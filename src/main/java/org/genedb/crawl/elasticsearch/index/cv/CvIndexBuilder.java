package org.genedb.crawl.elasticsearch.index.cv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.genedb.crawl.elasticsearch.index.NonDatabaseDataSourceIndexBuilder;
import org.genedb.crawl.model.Cv;
import org.genedb.crawl.model.Cvterm;
import org.genedb.crawl.model.CvtermRelationship;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.kohsuke.args4j.Option;
import org.obo.dataadapter.DefaultOBOParser;
import org.obo.dataadapter.OBOParseEngine;
import org.obo.datamodel.Link;
import org.obo.datamodel.OBOClass;
import org.obo.datamodel.OBOSession;
import org.obo.util.TermUtil;

public class CvIndexBuilder extends NonDatabaseDataSourceIndexBuilder {

    private static Logger       logger        = Logger.getLogger(CvIndexBuilder.class);

    @Option(name = "-cv", aliases = { "--controlled_vocabularies" }, usage = "The path(s) to the CV file(s)", required = true)
    public List<String>         cvFiles;

    @Option(name = "-ns", aliases = { "--namespaces" }, usage = "The namespaces to be loaded", required = true)
    public List<String>         namespaces;

    @Option(name = "-vn", aliases = { "--vocabulary_name" }, usage = "The name of the controlled vocabulary to load these files as", required = true)
    public String               vocabularyName;

    @Option(name = "-re", aliases = { "--relationships" }, usage = "The relationship types to be loaded (by default, all are loaded)", required = false)
    public List<String>         relationships = Arrays.asList(new String[] { "*" });

    /**
     * We're going to use this to build the graph.
     */
    private Map<String, Cvterm> allterms      = new HashMap<String, Cvterm>();

    @Override
    public void run() throws Exception {

        init();

        Set<String> namespacesSet = new HashSet<String>(namespaces);
        Set<String> relationshipsSet = new HashSet<String>(relationships);
        
        /*
         * Setup the OBO parser. 
         */
        DefaultOBOParser parser = new DefaultOBOParser();
        OBOParseEngine engine = new OBOParseEngine(parser);

        engine.setPaths(cvFiles);
        engine.parse();

        OBOSession session = parser.getSession();
        
        /*
         * Index and build graph.
         */
        indexOntology(namespacesSet, session);
        buildGraph(relationshipsSet, allterms);

    }
    
    /**
     * Goes through the ontologies and drops them into elasticsearch.
     * 
     * @param namespacesSet
     * @param session
     */
    private void indexOntology(Set<String> namespacesSet, OBOSession session) {

        for (OBOClass term : TermUtil.getTerms(session)) {
            if (term.getNamespace() != null) {

                String nameSpace = term.getNamespace().getID();

                if (namespacesSet.contains(nameSpace)) {

                    Cvterm cvterm = new Cvterm();
                    cvterm.name = term.getName();
                    cvterm.accession = term.getID();

                    cvterm.parents = parseLinks(term.getParents(), true);
                    cvterm.children = parseLinks(term.getChildren(), false);

                    cvterm.definition = term.getDefinition();

                    cvterm.cv = new Cv();
                    cvterm.cv.name = vocabularyName;

                    termsMapper.createOrUpdate(cvterm);
                    allterms.put(cvterm.accession, cvterm);

                } else {
                    logger.debug(String.format("%s namespace not matching, skipping...", term.getName()));
                }
            } else {
                logger.debug(String.format("%s has no namespace, skipping...", term.getName()));
            }
        }

    }
    
    /**
     * 
     * Generates a list of children or parent links.
     * 
     * @param links
     * @param parent
     * @return
     */
    private List<CvtermRelationship> parseLinks(Collection<Link> links, boolean parent) {
        List<CvtermRelationship> rels = new ArrayList<CvtermRelationship>();
        for (Link link : links) {
            CvtermRelationship cvr = new CvtermRelationship();
            cvr.relationship = link.getType().getName();
            if (parent)
                cvr.link = link.getParent().getID();
            else
                cvr.link = link.getChild().getID();
            rels.add(cvr);
        }
        return rels;
    }

    /**
     * 
     * This is a quick test to generate the graph. In practice this would
     * actually need to be run at load time, and rather than building off
     * the allterms hash, it would be built from the elastic search indices
     * generated above, and/or the controlled vocabularies stored in Chado.
     * 
     * @param relationshipsSet
     * @param allterms
     */
    private void buildGraph(Set<String> relationshipsSet, Map<String, Cvterm> allterms) {
        
        DirectedGraph<Cvterm, DefaultEdge> graph = new DefaultDirectedGraph<Cvterm, DefaultEdge>(DefaultEdge.class);

        for (Map.Entry<String, Cvterm> cvTermEntry : allterms.entrySet()) {
            Cvterm cvterm = cvTermEntry.getValue();
            logger.info("Vertex " + cvterm.accession);
            graph.addVertex(cvterm);
        }

        for (Map.Entry<String, Cvterm> cvTermEntry : allterms.entrySet()) {
            Cvterm cvterm = cvTermEntry.getValue();
            for (CvtermRelationship cvr : cvterm.children) {

                if ((!relationshipsSet.contains("*")) && (!relationshipsSet.contains(cvr.relationship)))
                    continue;

                Cvterm child = allterms.get(cvr.link);
                assert (child != null);

                logger.info("Edge " + cvterm.accession + "---(" + cvr.relationship + ")--->" + child.accession);
                graph.addEdge(cvterm, child);
            }
        }

        logger.info("Graph complete");
    }
    

    public static void main(String[] args) throws Exception {
        new CvIndexBuilder().prerun(args).closeIndex();
    }

}
