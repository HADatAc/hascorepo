package org.hascoapi.utils;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.riot.RiotNotFoundException;
import org.apache.jena.shared.NotFoundException;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.hascoapi.entity.pojo.NameSpace;

import com.typesafe.config.ConfigFactory;

public class Triplestore {

    String username = null;
    String password = null;
    String kbURL = null;
    boolean verbose = false;

    public Triplestore(String un, String pwd, String kb, boolean ver) {
        username = un;
        password = pwd;
        kbURL = kb;
        verbose = ver;
    }

    public static Long playTotalTriples() {
        Triplestore ts = new Triplestore(
                "user", "password",
                ConfigFactory.load().getString("hadatac.solr.triplestore"),
                false);
        return ts.totalTriples();
    }

    public Long totalTriples() {
        try {
            String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                    "SELECT (COUNT(*) as ?tot) WHERE { ?s ?p ?o . }";

            ResultSetRewindable resultsrw = SPARQLUtils.select(
                    CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

            QuerySolution soln = resultsrw.next();

            return Long.valueOf(soln.getLiteral("tot").getValue().toString()).longValue();
        } catch (Exception e) {
            return (long) -1;
        }
    }

    public String clean(int mode) {
        String message = "";
        message += Feedback.println(mode,"   Triples before [clean]: " + totalTriples());
        message += Feedback.println(mode, " ");

        String queryString = "";
        queryString += NameSpaces.getInstance().printSparqlNameSpaceList();
        queryString += "DELETE WHERE { ?s ?p ?o . } ";
        UpdateRequest req = UpdateFactory.create(queryString);
        UpdateProcessor processor = UpdateExecutionFactory.createRemote(req,
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_UPDATE));
        try {
            processor.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        message += Feedback.println(mode, " ");
        message += Feedback.println(mode, " ");
        message += Feedback.print(mode, "   Triples after [clean]: " + totalTriples());

        return message;
    }

    public String cleanStudy(int mode, String study) {
        String message = "";
        message += Feedback.println(mode,"   Triples before [clean]: " + totalTriples());
        NameSpace.deleteTriplesByNamedGraph(study);
        message += Feedback.print(mode, "   Triples after [clean]: " + totalTriples());

        return message;
    }

    public String getLang(String contentType) {
        if (contentType.contains("turtle")) {
            return "TTL";
        } else if (contentType.contains("rdf+xml")) {
            return "RDF/XML";
        } else {
            return "";
        }
    }

    /*
     *   contentType correspond to the mime type required for curl to process the data provided. For example, application/rdf+xml is
     *   used to process rdf/xml content.
     */
    public Long loadLocalFile(int mode, String filePath, String contentType, String graphUri) {
        Long total = totalTriples();
        try {
            System.out.println("MetadataContext: (filePath) [" + filePath + "]");
            System.out.println("MetadataContext: (contentType) [" + contentType + "]");
            System.out.println("MetadataContext: (graphUri) [" + graphUri + "]");
            File file = new File(filePath);
            if (file.exists()) {
                String kbUrl = kbURL + CollectionUtil.getCollectionName(CollectionUtil.Collection.SPARQL_GRAPH.get());
                GSPClient gspClient = new GSPClient(kbUrl);
                gspClient.postFile(file, contentType, graphUri);
            }
        } catch (NotFoundException e) {
            System.out.println("NotFoundException: file " + filePath);
            System.out.println("NotFoundException: " + e.getMessage());
            e.printStackTrace();
        } catch (RiotNotFoundException e) {
            System.out.println("RiotNotFoundException: file " + filePath);
            System.out.println("RiotNotFoundException: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Exception: graphUri [" + graphUri + "]");
            System.out.println("Exception: file " + filePath);
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }

        Long newTotal = totalTriples();
        return (newTotal - total);
    }

    public String loadOntologies(int mode) {
        String message = "";
        Long total = totalTriples();
        message += Feedback.println(mode, "   Triples before [loadOntologies]: " + total);
        message += Feedback.println(mode," ");

        System.out.println("Inside Triplestore.loadOntologies()");
        ConcurrentHashMap<String, NameSpace> namespaces = NameSpaces.getInstance().getNamespaces();
        for (String abbrev : namespaces.keySet()) {
            NameSpace ns = namespaces.get(abbrev);
            String nsURL = ns.getSource();
            System.out.println("  - loading [" + nsURL + "]");
            if (abbrev != null && nsURL != null && !nsURL.equals("") && ns.getSourceMime() != null) {
                String path = "";
                ns.loadTriples(nsURL, true);
                path = nsURL;
                Long newTotal = totalTriples();
                message += Feedback.println(mode, "   Added " + (newTotal - total) + " triples from " + path + " .");
                System.out.println("  - added " + (newTotal - total) + " triples from " + path + " .");
                total = newTotal;
                ns.setNumberOfLoadedTriples();
                //ns.updateFromTripleStore();
            }
        }
        message += Feedback.println(mode," ");
        message += Feedback.println(mode, "   Triples after [loadOntologies]: " + totalTriples());
        //NameSpaces.getInstance().reload();

        return message;
    }
}
