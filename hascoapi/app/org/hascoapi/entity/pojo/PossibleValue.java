package org.hascoapi.entity.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFilter;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.hascoapi.Constants;
import org.hascoapi.annotations.PropertyField;
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.SPARQLUtils;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.utils.NameSpaces;
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.vocabularies.RDFS;
import org.hascoapi.vocabularies.VSTOI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonFilter("possibleValueFilter")
public class PossibleValue extends HADatAcClass implements Comparable<PossibleValue>, Cloneable {

    private static final Logger log = LoggerFactory.getLogger(PossibleValue.class);
    static String className = "hasco:PossibleValue";

    private static Map<String, PossibleValue> possibleValueCache;

    //public static String INSERT_LINE1 = "INSERT DATA {  ";
    //public static String DELETE_LINE1 = "DELETE WHERE {  ";
    //public static String LINE3 = " a    sio:SIO_000614;  ";
    //public static String DELETE_LINE3 = " ?p ?o . ";
    //public static String LINE_LAST = "}  ";

    @PropertyField(uri="hasco:partOfSchema")
    private String partOfSchema;

    @PropertyField(uri="hasco:listPosition")
    private String listPosition;

    @PropertyField(uri="hasco:isPossibleValueOf")
    private String isPossibleValueOf;

    @PropertyField(uri="hasco:hasVariable")
    private String hasVariable;

    @PropertyField(uri="hasco:hasCode")
    private String hasCode;

    @PropertyField(uri="hasco:hasCodeLabel")
    private String hasCodeLabel;

    @PropertyField(uri="hasco:hasClass")
    private String hasClass;

    @PropertyField(uri="hasco:hasResource")
    private String hasResource;

    @PropertyField(uri="hasco:otherFor")
    private String hasOtherFor;

    @PropertyField(uri = "vstoi:hasSIRManagerEmail")
    private String hasSIRManagerEmail;

    /*********************
     * 
     *    CONSTRUCT(S)
     * 
     *********************/

     public PossibleValue () {
        super(className);
    }

    /**************
     * 
     *    CACHE
     * 
     **************/

     private static Map<String, PossibleValue> getCache() {
        if (possibleValueCache == null) {
            possibleValueCache = new HashMap<String, PossibleValue>();
        }
        return possibleValueCache;
    }

    public static void resetCache() {
        possibleValueCache = null;
    }

    /*****************************
     * 
     *    GETTERS AND SETTERS 
     * 
     *****************************/

    public String getPartOfSchema() {
        return partOfSchema;
    }
    public void setPartOfSchema(String partOfSchema) {
        this.partOfSchema = partOfSchema;
    }

    public String getListPosition() {
        return listPosition;
    }
    public void setListPosition(String listPosition) {
        this.listPosition = listPosition;
    }

    public String getIsPossibleValueOf() {
        return isPossibleValueOf;
    }
    public void setIsPossibleValueOf(String isPossibleValueOf) {
        this.isPossibleValueOf = isPossibleValueOf;
    }

    public String getHasVariable() {
        return hasVariable;
    }
    public void setHasVariable(String hasVariable) {
        this.hasVariable = hasVariable;
    }

    public String getHasCode() {
        return hasCode;
    }
    public void setHasCode(String hasCode) {
        this.hasCode = hasCode;
    }
    public String getHasCodeLabel() {
        if (hasClass == null || hasClass.isEmpty()) {
            return hasCodeLabel;
        }
        if (hasCodeLabel != null && !hasCodeLabel.isEmpty()) {
            return hasCodeLabel;
        }
        Attribute attr = Attribute.find(this.hasClass);
        if (attr != null && attr.getLabel() != null) {
            return "[" + attr.getLabel() + "]";
        }
        return "";
    }
    public void setHasCodeLabel(String hasCodeLabel) {
        this.hasCodeLabel = hasCodeLabel;
    }

    public String getHasClass() {
        return hasClass;
    }
    public String getPrettyHasClass() {
        return URIUtils.replaceNameSpaceEx(hasClass);
    }
    public void setHasClass(String hasClass) {
        this.hasClass = hasClass;
    }

    public String getHasResource() {
        return hasResource;
    }
    public void setHasResource(String hasResource) {
        this.hasResource = hasResource;
    }

    public String getHasOtherFor() {
        return hasOtherFor;
    }
    public String getPrettyHasOtherFor() {
        return URIUtils.replaceNameSpaceEx(hasOtherFor);
    }
    public void setHasOtherFor(String hasOtherFor) {
        this.hasOtherFor = hasOtherFor;
    }

    public String getHarmonizedCode() {
        if (this.hasClass == null || this.hasClass.isEmpty()) {
            return "";
        }
        return Attribute.findHarmonizedCode(this.hasClass);
    }

    public String getHasSIRManagerEmail() {
        return this.hasSIRManagerEmail;
    }
    public void setHasSIRManagerEmail(String hasSIRManagerEmail) {
        this.hasSIRManagerEmail = hasSIRManagerEmail;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone(); // Shallow copy
    }

    public static List<PossibleValue> find() {
        List<PossibleValue> codebook = new ArrayList<PossibleValue>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                " ?uri rdfs:subClassOf* " + className + " . " +
                "} ";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            PossibleValue code = find(soln.getResource("uri").getURI());
            codebook.add(code);
        }

        java.util.Collections.sort((List<PossibleValue>) codebook);
        return codebook;
    }

    public static List<PossibleValue> findBySchema(String schemaUri) {

        //System.out.println("SchemaUri: " + schemaUri);

        //log.debug("PossibleValue.findBySchema: SchemaUri=" + schemaUri);

        List<PossibleValue> possibleValues = new ArrayList<PossibleValue>();

        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList()
                + " SELECT ?uri WHERE { \n"
                + " ?uri hasco:hascoType hasco:PossibleValue . \n"
                + " ?uri hasco:partOfSchema <" + schemaUri + "> . \n"
                + " OPTIONAL { ?uri rdfs:label ?label . } \n"
                + " OPTIONAL { ?uri hasco:hasCode ?code . } \n"
                + " } \n"
                + " ORDER BY ?label ?code ";
        /* 
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList()
                + " SELECT ?uri WHERE { \n"
                + " ?uri a hasco:PossibleValue . \n"
                + " ?uri hasco:isPossibleValueOf ?daso_or_dasa . \n"
                + " ?daso_or_dasa hasco:partOfSchema <" + schemaUri + "> . \n"
                + " OPTIONAL { ?uri hasco:hasVariable ?variable . } \n"
                + " OPTIONAL { ?uri hasco:hasCode ?code . } \n"
                + " } \n"
                + " ORDER BY ?variable ?code ";
        */

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        try {
            while (resultsrw.hasNext()) {
                String pvUri = "";
                QuerySolution soln = resultsrw.next();
                if (soln.get("uri") != null && !soln.get("uri").toString().isEmpty()) {
                    pvUri = soln.get("uri").toString();
                    if (pvUri != null) {
                        PossibleValue pv = find(pvUri);
                        if (pv != null) {
                            possibleValues.add(pv);
                        }
                    }
                }

            }
        } catch (Exception e) {
            //log.error("[ERROR] PossibleValue.findBySchema(): " + e.getMessage());
            e.printStackTrace();
        }

        return possibleValues;
    }

    public static List<PossibleValue> findByVariable(String variableUri) {

        //log.debug("      VariableUri: " + variableUri);

        List<PossibleValue> possibleValues = new ArrayList<PossibleValue>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList()
                + " SELECT ?uri WHERE { \n"
                + " ?uri hasco:hascoType hasco:PossibleValue . \n"
                + " ?uri hasco:isPossibleValueOf <" + variableUri + "> .  \n"
                + " } \n "
                + " ORDER BY ?uri ";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        try {
            while (resultsrw.hasNext()) {
                String pvUri = "";
                QuerySolution soln = resultsrw.next();
                if (soln.get("uri") != null && !soln.get("uri").toString().isEmpty()) {
                    pvUri = soln.get("uri").toString();
                    if (pvUri != null) {
                        PossibleValue pv = find(pvUri);
                        if (pv != null) {
                            possibleValues.add(pv);
                        }
                    }
                }

            }
        } catch (Exception e) {
            log.error("[ERROR] PossibleValue.findByVariable(): " + e.getMessage());
            e.printStackTrace();
        }

        return possibleValues;
    }

    public static String findCodeValue(String dasa_uri, String code) {
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList()
                + " SELECT ?codeClass ?codeResource WHERE {"
                + " ?possibleValue a hasco:PossibleValue . "
                + " ?possibleValue hasco:isPossibleValueOf <" + dasa_uri + "> . "
                + " ?possibleValue hasco:hasCode ?code . "
                + " ?possibleValue hasco:hasClass ?codeClass . "
                + " FILTER (lcase(str(?code)) = \"" + code + "\") "
                + " }";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        if (resultsrw.size() > 0) {
            QuerySolution soln = resultsrw.next();
            try {
                if (null != soln.getResource("codeClass")) {
                    String classUri = soln.getResource("codeClass").toString();
                    if (classUri.length() != 0) {
                        return URIUtils.replacePrefixEx(classUri);
                    }
                }
            } catch (Exception e1) {
                return null;
            }
        }

        return null;
    }

    public static List<String> findUriBySchema(String schemaUri) {
        //System.out.println("Looking for data acquisition schema objects for <" + schemaUri + ">");

        List<String> possibleValueUris = new ArrayList<String>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                "SELECT ?uri WHERE { \n" +
                "   ?uri hasco:hascoType hasco:PossibleValue . \n" +
                "   ?uri hasco:partOfSchema <" + schemaUri + "> . \n" +
                "}";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        if (!resultsrw.hasNext()) {
            //System.out.println("[WARNING] SDDObject. Could not find objects for schema: " + schemaUri);
            return possibleValueUris;
        }

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            try {
                if (soln != null && soln.getResource("uri") != null && soln.getResource("uri").getURI() != null) {
                    String uriStr = soln.getResource("uri").getURI();
                    if (uriStr != null) {
                        possibleValueUris.add(uriStr);
                    }
                }
            }  catch (Exception e) {
                System.out.println("[ERROR] PossibleValue.findBySchema() e.Message: " + e.getMessage());
            }
        }
        return possibleValueUris;
    }

    public static Map<String, Map<String, String>> findPossibleValues(String schemaUri) {
        Map<String, Map<String, String>> mapPossibleValues = new HashMap<String, Map<String, String>>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList()
                + " SELECT ?daso_or_dasa ?codeClass ?code ?codeLabel ?resource ?other WHERE { \n"
                + " ?possibleValue a hasco:PossibleValue . \n"
                + " ?possibleValue hasco:isPossibleValueOf ?daso_or_dasa . \n"
                + " ?possibleValue hasco:hasCode ?code . \n"
                + " ?daso_or_dasa hasco:partOfSchema <" + schemaUri + "> . \n"
                + " OPTIONAL { ?possibleValue hasco:hasClass ?codeClass } . \n"
                + " OPTIONAL { ?possibleValue hasco:hasResource ?resource } . \n"
                + " OPTIONAL { ?possibleValue hasco:hasCodeLabel ?codeLabel } . \n"
                + " }";

        //log.debug("----> findPossibleValues query: \n" + queryString);

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        try {
            while (resultsrw.hasNext()) {
                String classUri = "";
                QuerySolution soln = resultsrw.next();
                if (soln.get("codeClass") != null && !soln.get("codeClass").toString().isEmpty()) {
                    classUri = soln.get("codeClass").toString();
                } else if (soln.get("resource") != null && !soln.get("resource").toString().isEmpty()) {
                    classUri = soln.get("resource").toString();
                } else if (soln.get("codeLabel") != null && !soln.get("codeLabel").toString().isEmpty()) {
                    // No code class is given, use code label instead
                    classUri = soln.get("codeLabel").toString();
                }

                String daso_or_dasa = soln.getResource("daso_or_dasa").toString();
                String code = soln.getLiteral("code").toString();
                if (mapPossibleValues.containsKey(daso_or_dasa)) {
                    mapPossibleValues.get(daso_or_dasa).put(code.toLowerCase(), classUri);
                } else {
                    Map<String, String> indvMapPossibleValues = new HashMap<String, String>();
                    indvMapPossibleValues.put(code.toLowerCase(), classUri);
                    mapPossibleValues.put(daso_or_dasa, indvMapPossibleValues);
                }
            }
        } catch (Exception e) {
            //log.error("[ERROR] PossibleValue.findPossibleValues(): " + e.getMessage());
            e.printStackTrace();
        }

        return mapPossibleValues;
    }

    public static PossibleValue find(String uri) {
        PossibleValue code = null;
        Statement statement;
        RDFNode object;

        String queryString = "DESCRIBE <" + uri + ">";
        Model model = SPARQLUtils.describe(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.SPARQL_QUERY), queryString);

        code = new PossibleValue();
        StmtIterator stmtIterator = model.listStatements();

        while (stmtIterator.hasNext()) {
            statement = stmtIterator.next();
            object = statement.getObject();
            String str = URIUtils.objectRDFToString(object);
            if (statement.getPredicate().getURI().equals(RDFS.LABEL)) {
                code.setLabel(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HASCO_TYPE)) {
                code.setHascoTypeUri(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.PART_OF_SCHEMA)) {
                code.setPartOfSchema(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.LIST_POSITION)) {
                code.setListPosition(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.IS_POSSIBLE_VALUE_OF)) {
                code.setIsPossibleValueOf(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_CODE)) {
                code.setHasCode(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_VARIABLE)) {
                code.setHasVariable(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_CODE_LABEL)) {
                code.setHasCodeLabel(str);
            } else if (statement.getPredicate().getURI().equals(RDFS.SUBCLASS_OF)) {
                code.setSuperUri(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_CLASS)) {
                try {
                    code.setHasClass(str);
                } catch (Exception e) {
                }
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_OTHER_FOR)) {
                try {
                    code.setHasOtherFor(str);
                } catch (Exception e) {
                }
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_RESOURCE)) {
                code.setHasResource(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_SIR_MANAGER_EMAIL)) {
                code.setHasSIRManagerEmail(str);
            }
        }

        code.setUri(uri);
        code.setLocalName(uri.substring(uri.indexOf('#') + 1));
        if (code.getLabel() == null || code.getLabel().equals("")) {
            code.setLabel(code.getLocalName());
        }

        return code;
    }

    public boolean deleteHasClass() {
        String query = "";
        String uri = "";
        if (getUri() == null || getUri().equals("")) {
            return false;
        }

        query += NameSpaces.getInstance().printSparqlNameSpaceList();
        query += " DELETE WHERE { \n";

        if (getUri().startsWith("http")) {
            uri += "<" + this.getUri() + ">";
        } else {
            uri += this.getUri();
        }

        query += uri + " <http://hadatac.org/ont/hasco/hasClass> ?o . \n";
        query += " } ";

        try {
            UpdateRequest request = UpdateFactory.create(query);
            UpdateProcessor processor = UpdateExecutionFactory.createRemote(
                    request, CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_UPDATE));
            processor.execute();
        } catch (Exception e) {
            //log.error("[ERROR] Possiblevalue.java: QueryParseException due to update query: " + query);
            return false;
        }

        //log.debug("Deleted hasco:hasClass property of <" + getUri() + "> from triple store");
        return true;

    }
 
    /* 
    public boolean saveHasClass() {
        if (!deleteHasClass()) {
            return false;
        };

        String insert = "";
        insert += NameSpaces.getInstance().printSparqlNameSpaceList();
        insert += INSERT_LINE1;

        if (!getNamedGraph().isEmpty()) {
            insert += " GRAPH <" + getNamedGraph() + "> { ";
        } else {
            insert += " GRAPH <" + Constants.DEFAULT_REPOSITORY + "> { ";
        }

        insert += "<" + this.getUri() + ">  ";
        insert += LINE3;

        if (this.getHasClass() != null && !this.getHasClass().isEmpty()) {
            insert += " <http://hadatac.org/ont/hasco/hasClass> <" + this.getHasClass() + "> ;   ";
        }

        // CLOSING NAMEDGRAPH
        insert += " } ";

        insert += LINE_LAST;

        try {
            UpdateRequest request = UpdateFactory.create(insert);
            UpdateProcessor processor = UpdateExecutionFactory.createRemote(
                    request, CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_UPDATE));
            processor.execute();
        } catch (Exception e) {
            //log.error("[ERROR] PossibleValue.java: QueryParseException due to update query: " + insert);
            return false;
        }

        //log.debug("Added hasco:hasClass property of <" + getUri() + "> from triple store");
        return true;

    }
    */

    @Override
    public void save() {
        saveToTripleStore();
    }

    @Override
    public void delete() {
        deleteFromTripleStore();
    }


    @Override
    public int compareTo(PossibleValue another) {
        if (this.getLabel() != null && another.getLabel() != null) {
            return this.getLabel().compareTo(another.getLabel());
        }
        return this.getLocalName().compareTo(another.getLocalName());
    }
}
