package org.hascoapi.entity.pojo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.hascoapi.annotations.PropertyField;
import org.hascoapi.utils.SPARQLUtils;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.NameSpaces;
import org.hascoapi.vocabularies.*;

@JsonFilter("annotationStemFilter")
public class AnnotationStem extends HADatAcThing implements SIRElement, Comparable<AnnotationStem>  {

    @PropertyField(uri="vstoi:hasStatus")
    private String hasStatus;

    @PropertyField(uri="hasco:hasImage")
    private String image;

    @PropertyField(uri="vstoi:hasContent")
    private String hasContent;

    @PropertyField(uri="vstoi:hasLanguage")
    private String hasLanguage;

    @PropertyField(uri="vstoi:hasVersion")
    private String hasVersion;

    @PropertyField(uri="prov:wasDerivedFrom")
    private String wasDerivedFrom;

    @PropertyField(uri="prov:wasGeneratedBy")
    private String wasGeneratedBy;

    @PropertyField(uri="vstoi:hasSIRManagerEmail")
    private String hasSIRManagerEmail;

    public String getHasStatus() {
        return hasStatus;
    }

    public void setHasStatus(String hasStatus) {
        this.hasStatus = hasStatus;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getHasContent() {
        return hasContent;
    }

    public void setHasContent(String hasContent) {
        this.hasContent = hasContent;
    }

    public String getHasLanguage() {
        return hasLanguage;
    }

    public void setHasLanguage(String hasLanguage) {
        this.hasLanguage = hasLanguage;
    }

    public String getHasVersion() {
        return hasVersion;
    }

    public void setWasDerivedFrom(String wasDerivedFrom) {
        this.wasDerivedFrom = wasDerivedFrom;
    }

    public String getWasDerivedFrom() {
        return wasDerivedFrom;
    }

    public void setWasGeneratedBy(String wasGeneratedBy) {
        this.wasGeneratedBy = wasGeneratedBy;
    }

    public String getWasGeneratedBy() {
        return wasGeneratedBy;
    }

    public void setHasVersion(String hasVersion) {
        this.hasVersion = hasVersion;
    }

    public String getHasSIRManagerEmail() {
        return hasSIRManagerEmail;
    }

    public void setHasSIRManagerEmail(String hasSIRManagerEmail) {
        this.hasSIRManagerEmail = hasSIRManagerEmail;
    }

    public static AnnotationStem find(String uri) {
        AnnotationStem annotationStem = null;
        Statement statement;
        RDFNode object;

        String queryString = "DESCRIBE <" + uri + ">";
        Model model = SPARQLUtils.describe(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.SPARQL_QUERY), queryString);

        StmtIterator stmtIterator = model.listStatements();

        if (!stmtIterator.hasNext()) {
            return null;
        }

        annotationStem = new AnnotationStem();

        while (stmtIterator.hasNext()) {
            statement = stmtIterator.next();
            object = statement.getObject();
            String str = URIUtils.objectRDFToString(object);
            if (statement.getPredicate().getURI().equals(RDFS.LABEL)) {
                annotationStem.setLabel(str);
            } else if (statement.getPredicate().getURI().equals(RDF.TYPE)) {
                annotationStem.setTypeUri(str);
            } else if (statement.getPredicate().getURI().equals(RDFS.COMMENT)) {
                annotationStem.setComment(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HASCO_TYPE)) {
                annotationStem.setHascoTypeUri(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_STATUS)) {
                annotationStem.setHasStatus(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_IMAGE)) {
                annotationStem.setImage(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_CONTENT)) {
                annotationStem.setHasContent(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_LANGUAGE)) {
                annotationStem.setHasLanguage(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_VERSION)) {
                annotationStem.setHasVersion(str);
            } else if (statement.getPredicate().getURI().equals(PROV.WAS_DERIVED_FROM)) {
                annotationStem.setWasDerivedFrom(str);
            } else if (statement.getPredicate().getURI().equals(PROV.WAS_GENERATED_BY)) {
                annotationStem.setWasGeneratedBy(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_SIR_MANAGER_EMAIL)) {
                annotationStem.setHasSIRManagerEmail(str);
            } 
        }

        annotationStem.setUri(uri);

        return annotationStem;
    }

    public static List<AnnotationStem> derivation(String annotationStemUri) {
        if (annotationStemUri == null || annotationStemUri.isEmpty()) {
            return null;
        }
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                " ?type rdfs:subClassOf* vstoi:AnnotationStem . " +
                " ?uri a ?type ." +
                " ?uri prov:wasDerivedFrom <" + annotationStemUri + "> . " +
                " ?uri vstoi:hasContent ?content . " +
                "} " +
                "ORDER BY ASC(?content) ";

        //System.out.println("Query: " + queryString);

        return findByQuery(queryString);
    }

    public static List<Annotation> usage(String annotationStemUri) {
        if (annotationStemUri == null || annotationStemUri.isEmpty()) {
            return null;
        }
        List<Annotation> annotations = new ArrayList<Annotation>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                " ?type rdfs:subClassOf* vstoi:Annotation . " +
                " ?uri a ?type ." +
                " ?uri vstoi:hasAnnotationStem <" + annotationStemUri + "> . " +
                " ?uri vstoi:belongsTo ?instUri . " +
                " ?instUri rdfs:label ?instLabel . " +
                "} " +
                "ORDER BY ASC(?instLabel) ";

        //System.out.println("Query: " + queryString);

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        if (!resultsrw.hasNext()) {
            return null;
        }

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            //System.out.println("inside Detector.usage(): found uri [" + soln.getResource("uri").getURI().toString() + "]");
            Annotation annotation = Annotation.find(soln.getResource("uri").getURI());
            annotations.add(annotation);
        }
        return annotations;
    }

    private static List<AnnotationStem> findByQuery(String queryString) {
        List<AnnotationStem> annotationStems = new ArrayList<AnnotationStem>();
        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        if (!resultsrw.hasNext()) {
            return null;
        }

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            AnnotationStem annotationStem = find(soln.getResource("uri").getURI());
            annotationStems.add(annotationStem);
        }

        java.util.Collections.sort((List<AnnotationStem>) annotationStems);
        return annotationStems;

    }

    @Override
    public int compareTo(AnnotationStem another) {
        return this.getLabel().compareTo(another.getLabel());
    }

    @Override public void save() {
        saveToTripleStore();
    }

    @Override public void delete() {
        deleteFromTripleStore();
    }

}
