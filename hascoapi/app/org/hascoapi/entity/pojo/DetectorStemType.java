package org.hascoapi.entity.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonFilter;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.hascoapi.utils.SPARQLUtils;
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.NameSpaces;
import org.hascoapi.vocabularies.RDFS;
import org.hascoapi.vocabularies.VSTOI;

@JsonFilter("detectorStemTypeFilter")
public class DetectorStemType extends HADatAcClass implements Comparable<DetectorStemType> {

    static String className = "vstoi:Detector";

    private String url;

    public DetectorStemType () {
        super(className);
    }

    public String getURL() {
        return url;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public String getSuperLabel() {
        DetectorStemType superInsType = DetectorStemType.find(getSuperUri());
        if (superInsType == null || superInsType.getLabel() == null) {
            return "";
        }
        return superInsType.getLabel();
    }

    /** 
    public static List<DetectorStemType> find() {
        List<DetectorStemType> detectorStemTypes = new ArrayList<DetectorStemType>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                " ?uri rdfs:subClassOf* " + className + " . " +
                "} ";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            DetectorStemType detectorStemType = find(soln.getResource("uri").getURI());
            detectorStemTypes.add(detectorStemType);
        }

        java.util.Collections.sort((List<DetectorStemType>) detectorStemTypes);
        return detectorStemTypes;

    }

    public static Map<String,String> getMap() {
        List<DetectorStemType> list = find();
        Map<String,String> map = new HashMap<String,String>();
        for (DetectorStemType typ: list)
            map.put(typ.getUri(),typ.getLabel());
        return map;
    }
    */

    public static DetectorStemType find(String uri) {
        DetectorStemType detectorStemType = null;
        Model model;
        Statement statement;
        RDFNode object;

        String queryString = "DESCRIBE <" + uri + ">";
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), query);
        model = qexec.execDescribe();

        detectorStemType = new DetectorStemType();
        StmtIterator stmtIterator = model.listStatements();

        while (stmtIterator.hasNext()) {
            statement = stmtIterator.next();
            object = statement.getObject();
            if (statement.getPredicate().getURI().equals(RDFS.LABEL)) {
                detectorStemType.setLabel(object.asLiteral().getString());
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_WEB_DOCUMENTATION)) {
                detectorStemType.setURL(object.asLiteral().getString());
            } else if (statement.getPredicate().getURI().equals(RDFS.SUBCLASS_OF)) {
                detectorStemType.setSuperUri(object.asResource().getURI());
            }
        }

        detectorStemType.setUri(uri);
        detectorStemType.setLocalName(uri.substring(uri.indexOf('#') + 1));

        return detectorStemType;
    }

    public static int getNumberDetectorStemTypes() {
        String query = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT (count(?detectorStemType) as ?tot) WHERE { " +
                " ?detectorStemType rdfs:subClassOf* " + className + " . " +
                "} ";

        try {
            ResultSetRewindable resultsrw = SPARQLUtils.select(
                    CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), query);

            if (resultsrw.hasNext()) {
                QuerySolution soln = resultsrw.next();
                return Integer.parseInt(soln.getLiteral("tot").getString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int compareTo(DetectorStemType another) {
        if (this.getLabel() != null && another.getLabel() != null) {
            return this.getLabel().compareTo(another.getLabel());
        }
        return this.getLocalName().compareTo(another.getLocalName());
    }

}
