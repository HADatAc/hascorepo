package org.hascoapi.entity.pojo;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.hascoapi.Constants;
import org.hascoapi.annotations.PropertyField;
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.NameSpaces;
import org.hascoapi.utils.SPARQLUtils;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.vocabularies.RDF;
import org.hascoapi.vocabularies.RDFS;
import org.hascoapi.vocabularies.VSTOI;

import java.util.ArrayList;
import java.util.List;

@JsonFilter("annotationFilter")
public class Annotation extends HADatAcThing implements Comparable<Annotation>  {

    @PropertyField(uri="vstoi:belongsTo")
    private String belongsTo;

    @PropertyField(uri="vstoi:hasAnnotationStem")
    private String hasAnnotationStem;

    @PropertyField(uri="vstoi:hasPosition")
    private String hasPosition;

    @PropertyField(uri="vstoi:hasContentWithStyle")
    private String hasContentWithStyle;

    @PropertyField(uri="vstoi:hasSIRManagerEmail")
    private String hasSIRManagerEmail;

    public String getBelongsTo() {
        return belongsTo;
    }

    public void setBelongsTo(String belongsTo) {
        this.belongsTo = belongsTo;
    }

    public String getHasAnnotationStem() {
        return hasAnnotationStem;
    }

    public void setHasAnnotationStem(String hasAnnotationStem) {
        this.hasAnnotationStem = hasAnnotationStem;
    }

    public AnnotationStem getAnnotationStem() {
        if (hasAnnotationStem == null || hasAnnotationStem.equals("")) {
            return null;
        }
        AnnotationStem annotationStem = AnnotationStem.find(hasAnnotationStem);
        return annotationStem;
    }

    public String getHasPosition() {
        return hasPosition;
    }

    public void setHasPosition(String hasPosition) {
        this.hasPosition = hasPosition;
    }

    public String getHasContentWithStyle() {
        return hasContentWithStyle;
    }

    @JsonIgnore
    public String getRendering() {
        if (getAnnotationStem() == null || getAnnotationStem().getHasContent() == null || getAnnotationStem().getHasContent().isEmpty()) {
            return "";
        }
        if (getHasContentWithStyle() == null) {
            return getAnnotationStem().getHasContent();
        }
        String rendering = getHasContentWithStyle();
        if (rendering.indexOf(Constants.META_VARIABLE_CONTENT) == -1) {
            return rendering;
        }
        return rendering.replaceAll(Constants.META_VARIABLE_CONTENT,getAnnotationStem().getHasContent());
    }

    public void setHasContentWithStyle(String hasContentWithStyle) {
        this.hasContentWithStyle = hasContentWithStyle;
    }

    public String getHasSIRManagerEmail() {
        return hasSIRManagerEmail;
    }

    public void setHasSIRManagerEmail(String hasSIRManagerEmail) {
        this.hasSIRManagerEmail = hasSIRManagerEmail;
    }

    public static int getNumberAnnotationsByContainer(String instrumentUri) {
        String query = "";
        query += NameSpaces.getInstance().printSparqlNameSpaceList();
        query += " select (count(?uri) as ?tot) where { " +
                " ?type rdfs:subClassOf* vstoi:Annotation . " +
                " ?uri a ?type ." +
                " ?uri vstoi:belongsTo <" + instrumentUri + ">. " +
                "}";

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

    public static List<Annotation> findByContainerWithPages(String containerUri, int pageSize, int offset) {
        List<Annotation> annotations = new ArrayList<Annotation>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                "SELECT ?uri WHERE { " +
                " ?type rdfs:subClassOf* vstoi:Annotation . " +
                " ?uri a ?type . } " +
                " ?uri vstoi:belongsTo <" + containerUri + ">. " +
                " LIMIT " + pageSize +
                " OFFSET " + offset;

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            if (soln != null && soln.getResource("uri").getURI() != null) {
                Annotation annotation = Annotation.find(soln.getResource("uri").getURI());
                annotations.add(annotation);
            }
        }
        return annotations;
    }

    public static List<Annotation> findByContainer(String containerUri) {
        //System.out.println("findByContainer: [" + containerUri + "]");
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                " ?type rdfs:subClassOf* vstoi:Annotation . " +
                " ?uri a ?type ." +
                " ?uri vstoi:belongsTo <" + containerUri + ">. " +
                "} ";

        return findByQuery(queryString);
    }

    public static Annotation findByContainerAndPosition(String containerUri, String positionUri) {
        //System.out.println("findByContainer: [" + instrumentUri + "]");
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                " ?type rdfs:subClassOf* vstoi:Annotation . " +
                " ?uri a ?type ." +
                " ?uri vstoi:belongsTo <" + containerUri + "> . " +
                " ?uri vstoi:hasPosition <" + positionUri + "> . " +
                "} ";

        return findOneByQuery(queryString);
    }

    private static List<Annotation> findByQuery(String queryString) {
        List<Annotation> annotations = new ArrayList<Annotation>();
        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        if (!resultsrw.hasNext()) {
            return null;
        }

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            Annotation Annotation = find(soln.getResource("uri").getURI());
            annotations.add(Annotation);
        }

        java.util.Collections.sort((List<Annotation>) annotations);
        return annotations;

    }

    private static Annotation findOneByQuery(String queryString) {
        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        if (!resultsrw.hasNext()) {
            return null;
        }

        QuerySolution soln = resultsrw.next();
        return find(soln.getResource("uri").getURI());
 
    }


    public static Annotation find(String uri) {
        Annotation annotation = null;
        Statement statement;
        RDFNode object;

        String queryString = "DESCRIBE <" + uri + ">";
        Model model = SPARQLUtils.describe(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.SPARQL_QUERY), queryString);

        StmtIterator stmtIterator = model.listStatements();

        if (!stmtIterator.hasNext()) {
            return null;
        }

        annotation = new Annotation();

        while (stmtIterator.hasNext()) {
            statement = stmtIterator.next();
            object = statement.getObject();
            String str = URIUtils.objectRDFToString(object);
            if (statement.getPredicate().getURI().equals(RDFS.LABEL)) {
                annotation.setLabel(str);
            } else if (statement.getPredicate().getURI().equals(RDF.TYPE)) {
                annotation.setTypeUri(str);
            } else if (statement.getPredicate().getURI().equals(RDFS.COMMENT)) {
                annotation.setComment(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HASCO_TYPE)) {
                annotation.setHascoTypeUri(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.BELONGS_TO)) {
                annotation.setBelongsTo(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_ANNOTATION_STEM)) {
                annotation.setHasAnnotationStem(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_POSITION)) {
                annotation.setHasPosition(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_CONTENT_WITH_STYLE)) {
                annotation.setHasContentWithStyle(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_SIR_MANAGER_EMAIL)) {
                annotation.setHasSIRManagerEmail(str);
            }
        }

        annotation.setUri(uri);

        return annotation;
    }

    /*
    static public boolean createAnnotation(String instrumentUri, String annotationUri, String position, String style, String hasAnnotationStem) {
        if (instrumentUri == null || instrumentUri.isEmpty()) {
            return false;
        }
        if (position == null || position.isEmpty()) {
            return false;
        }
        Annotation annotation = new Annotation();
        annotation.setUri(annotationUri);
        annotation.setLabel("Annotation " + position);
        annotation.setTypeUri(VSTOI.ANNOTATION);
        annotation.setHascoTypeUri(VSTOI.ANNOTATION);
        annotation.setComment("Annotation " + position + " of instrument with URI " + instrumentUri);
        annotation.setHasAnnotationStem(hasAnnotationStem);
        annotation.setBelongsTo(instrumentUri);
        annotation.setHasPosition(position);
        annotation.setHasStyle(style);
        if (hasAnnotationStem != null) {
            annotation.setHasAnnotationStem(hasAnnotationStem);
        }
        annotation.save();
        //System.out.println("Annotation.createAnnotationStemSlot: creating Annotation with URI [" + annotationUri + "]" );
        return true;
    }
    */

   /*
    public boolean updateAnnotationAnnotationStem(String hasAnnotationStem) {
        Annotation newAnnotation = new Annotation();
        newAnnotation.setUri(this.uri);
        newAnnotation.setLabel(this.getLabel());
        newAnnotation.setTypeUri(this.getTypeUri());
        newAnnotation.setComment(this.getComment());
        newAnnotation.setHascoTypeUri(this.getHascoTypeUri());
        newAnnotation.setBelongsTo(this.getBelongsTo());
        newAnnotation.setHasPosition(this.getHasPosition());
        newAnnotation.setHasStyle(this.getHasStyle());
        if (hasAnnotationStem != null && !hasAnnotationStem.isEmpty()) {
            newAnnotation.setHasAnnotationStem(hasAnnotationStem);
        }
        this.delete();
        newAnnotation.save();
        return true;
    }
    */

    @Override
    public int compareTo(Annotation another) {
        return this.getHasPosition().compareTo(another.getHasPosition());
    }

    @Override public void save() {
        saveToTripleStore();
    }

    @Override public void delete() {
        deleteFromTripleStore();
    }

}
