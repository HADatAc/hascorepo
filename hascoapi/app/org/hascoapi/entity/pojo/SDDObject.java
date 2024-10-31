package org.hascoapi.entity.pojo;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.hascoapi.Constants;
import org.hascoapi.annotations.PropertyField;
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.NameSpaces;
import org.hascoapi.utils.FirstLabel;
import org.hascoapi.utils.SPARQLUtils;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.vocabularies.PROV;
import org.hascoapi.vocabularies.RDF;
import org.hascoapi.vocabularies.RDFS;
import org.hascoapi.vocabularies.SIO;
import org.hascoapi.vocabularies.VSTOI;

@JsonFilter("sddObjectFilter")
public class SDDObject extends HADatAcThing {

    /* 
    public static String INDENT1 = "     ";
    public static String INSERT_LINE1 = "INSERT DATA {  ";
    public static String DELETE_LINE1 = "DELETE WHERE {  ";
    public static String LINE3 = INDENT1 + "a         hasco:SDDObject;  ";
    public static String DELETE_LINE3 = " ?p ?o . ";
    public static String LINE_LAST = "}  ";
    public static String PREFIX = "SDDO-";
    */

    private static Map<String, SDDObject> SDDOCache;

    @PropertyField(uri="hasco:partOfSchema")
    private String partOfSchema;

    @PropertyField(uri="hasco:schemaPosition")
    private String position;

    @PropertyField(uri="hasco:listPosition")
    private String listPosition;

    private int positionInt;

    private int tempPositionInt;

    @PropertyField(uri="hasco:hasEntity")
    private String entity;

    private String entityLabel;

    @PropertyField(uri="hasco:hasRole")
    private String role;

    private String roleLabel;

    @PropertyField(uri="sio:SIO_000668")
    private String inRelationTo;

    private String inRelationToLabel;

    @PropertyField(uri="hasco:relation")
    private String relation;
 
    private String relationLabel;
 
    @PropertyField(uri="prov:wasDerivedFrom")
    private String wasDerivedFrom;
 
    @PropertyField(uri = "vstoi:hasSIRManagerEmail")
    private String hasSIRManagerEmail;

    private String alternativeName = "";

    /**************
     * 
     *    CACHE
     * 
     **************/

    private static Map<String, SDDObject> getCache() {
        if (SDDOCache == null) {
            SDDOCache = new HashMap<String, SDDObject>();
        }
        return SDDOCache;
    }

    public static void resetCache() {
        SDDOCache = null;
    }

    /*********************** 
     * 
     *     CONSTRUCTORS
     * 
     ***********************/

    public SDDObject() {        
        SDDObject.getCache();
    }

    /* 
    public SDDObject(String uri,
                    String label,
                    String partOfSchema,
                    String position,
                    String entity,
                    String role,
                    String inRelationTo,
                    String inRelationToLabel,
                    String wasDerivedFrom,
                    String relation) {
        this.uri = uri;
        this.label = label;
        this.partOfSchema = partOfSchema;
        this.position = position;
        try {
            if (position != null && !position.equals("")) {
                positionInt = Integer.parseInt(position);
            } else {
                positionInt = -1;
            }
        } catch (Exception e) {
            positionInt = -1;
        }
        this.setEntity(entity);
        this.setRole(role);
        this.setInRelationTo(inRelationTo);
        this.setWasDerivedFrom(wasDerivedFrom);
        this.setRelation(relation);
        SDDObject.getCache();
    }
    */

    @JsonIgnore
    public String getUriNamespace() {
        return URIUtils.replaceNameSpaceEx(uri);
    }

    public String getPartOfSchema() {
        return partOfSchema;
    }
    public void setPartOfSchema(String partOfSchema) {
        this.partOfSchema = partOfSchema;
    }

    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
    }

    public String getListPosition() {
        return listPosition;
    }
    public void setListPosition(String listPosition) {
        this.listPosition = listPosition;
    }

    public int getPositionInt() {
        return positionInt;
    }
    public int getTempPositionInt() {
        return tempPositionInt;
    }

    public void setTempPositionInt(int tempPositionInt) {
        this.tempPositionInt = tempPositionInt;
    }

    public String getEntity() {
        return entity;
    }
    public void setEntity(String entity) {
        this.entity = entity;
        if (entity == null || entity.equals("")) {
            this.entityLabel = "";
        } else {
            this.entityLabel = FirstLabel.getPrettyLabel(entity);
        }
    }
    @JsonIgnore
    public String getEntityNamespace() {
        return URIUtils.replaceNameSpaceEx(entity);
    }
    @JsonIgnore
    public String getEntityLabel(Map<String, String> codeMappings) {
        if (entity == null || entityLabel.equals("")) {
            String newLabel = URIUtils.replaceNameSpaceEx(entity);
            if (newLabel.contains(":")) {
                if (codeMappings.containsKey(newLabel)){
                    return codeMappings.get(newLabel);
                } else {
                    return newLabel.split("\\:")[1];
                }
            } else {
                return newLabel;
            }
        } else {
            return entityLabel;
        }
    }
    @JsonIgnore
    public String getEntityLabel() {
        if (entity == null || entityLabel.equals("")) {
            return URIUtils.replaceNameSpaceEx(entity);
        }
        return entityLabel;
    }
    @JsonIgnore
    public String getAnnotatedEntity() {
        String annotation;
        if (entityLabel.equals("")) {
            if (entity == null || entity.equals("")) {
                return "";
            }
            annotation = URIUtils.replaceNameSpaceEx(entity);
        } else {
            annotation = entityLabel;
        }
        if (!getEntityNamespace().equals("")) {
            annotation += " [" + getEntityNamespace() + "]";
        }
        return annotation;
    }

    public String getRole() {
        return this.role;
    }
    public void setRole(String role) {
        //this.role = role;
        this.role = role;
        //System.out.println("New ROLE : " + role);
        if (role == null || role.equals("")) {
            this.roleLabel = "";
        } else {
            this.roleLabel = FirstLabel.getPrettyLabel(role);
        }
    }
    @JsonIgnore
    public String getRoleLabel() {
        if (role == null || roleLabel.equals("")) {
            return URIUtils.replaceNameSpaceEx(role);
        }
        return roleLabel;
    }

    public String getInRelationTo() {
        return inRelationTo;
    }
    @JsonIgnore
    public String getInRelationToNamespace() {
        return URIUtils.replaceNameSpaceEx(inRelationTo);
    }

    public void setInRelationTo(String inRelationTo) {
        this.inRelationTo = inRelationTo;
        if (inRelationTo == null || inRelationTo.equals("")) {
            this.inRelationToLabel = "";
        } else {
            this.inRelationToLabel = FirstLabel.getPrettyLabel(inRelationTo);
        }
    }
    @JsonIgnore
    public String getInRelationToLabel() {
        return inRelationToLabel;
    }

    public String getRelation() {
        return relation;
    }
    @JsonIgnore
    public String getRelationNamespace() {
        return URIUtils.replaceNameSpaceEx(relation);
    }
    public void setRelation(String relation) {
        this.relation = relation;
        //System.out.println("New RELATION : " + relation);
        if (relation == null || relation.equals("")) {
            this.relationLabel = "";
        } else {
            this.relationLabel = FirstLabel.getPrettyLabel(relation);
        }
    }
    @JsonIgnore
    public String getRelationLabel() {
        if (relationLabel == null || relationLabel.equals("")) {
            //System.out.println("RELATION label -- just relation : <" + relation + ">");
            //System.out.println("RELATION label -- just relation : <" + URIUtils.replaceNameSpaceEx(relation) + ">");
            return URIUtils.replaceNameSpaceEx(relation);
        }
        //System.out.println("RELATION label : <" + relationLabel + ">");
        return relationLabel;
    }

    public String getWasDerivedFrom() {
        return wasDerivedFrom;
    }
    public void setWasDerivedFrom(String wasDerivedFrom) {
        this.wasDerivedFrom = wasDerivedFrom;
    }

    public String getHasSIRManagerEmail() {
        return this.hasSIRManagerEmail;
    }
    public void setHasSIRManagerEmail(String hasSIRManagerEmail) {
        this.hasSIRManagerEmail = hasSIRManagerEmail;
    }

    @JsonIgnore
    public String getAlternativeName() {
        return alternativeName;
    }
    @JsonIgnore
    public void setAlternativeName(String alternativeName) {
        this.alternativeName = alternativeName;
    }

    public static SDDObject find(String uri) {
        if (uri == null || uri.isEmpty()) {
            System.out.println("[ERROR] No valid URI provided to retrieve SDDObject: " + uri);
            return null;
        }

		//System.out.println("Study.java : in find(): uri = [" + uri + "]");
	    SDDObject sddo = new SDDObject();
        Map<String,String> relationMap = new HashMap<>();

	    Statement statement;
	    RDFNode object;
	    
	    String queryString = "DESCRIBE <" + uri + ">";
	    Model model = SPARQLUtils.describe(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.SPARQL_QUERY), queryString);
		
		StmtIterator stmtIterator = model.listStatements();

		if (!stmtIterator.hasNext()) {
			return null;
		} 

		while (stmtIterator.hasNext()) {
		    statement = stmtIterator.next();
		    object = statement.getObject();
			String string = URIUtils.objectRDFToString(object);
            //System.out.println("Property value: [" + string + "]");
			if (uri != null && !uri.isEmpty()) {
				if (statement.getPredicate().getURI().equals(RDFS.LABEL)) {
					sddo.setLabel(string);
				} else if (statement.getPredicate().getURI().equals(RDF.TYPE)) {
					sddo.setTypeUri(string); 
				} else if (statement.getPredicate().getURI().equals(HASCO.HASCO_TYPE)) {
					sddo.setHascoTypeUri(string); 
				} else if (statement.getPredicate().getURI().equals(HASCO.PART_OF_SCHEMA)) {
					sddo.setPartOfSchema(string);
				} else if (statement.getPredicate().getURI().equals(HASCO.HAS_SCHEMA_POSITION)) {
					sddo.setPosition(string);
				} else if (statement.getPredicate().getURI().equals(HASCO.LIST_POSITION)) {
					sddo.setListPosition(string);
                } else if (statement.getPredicate().getURI().equals(HASCO.HAS_ENTITY)) {
                    sddo.setEntity(string);
                } else if (statement.getPredicate().getURI().equals(HASCO.HAS_ROLE)) {
                    sddo.setRole(string);
				} else if (statement.getPredicate().getURI().equals(SIO.IN_RELATION_TO)) {
					sddo.setInRelationTo(string);
				} else if (statement.getPredicate().getURI().equals(HASCO.HAS_RELATION)) {
					sddo.setRelation(string);
				} else if (statement.getPredicate().getURI().equals(RDFS.COMMENT)) {
					sddo.setComment(string);
                } else if (statement.getPredicate().getURI().equals(PROV.WAS_DERIVED_FROM)) {
					sddo.setWasDerivedFrom(string);
                } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_SIR_MANAGER_EMAIL)) {
					sddo.setHasSIRManagerEmail(string);
				}
			}
		}

	    SDDObject.getCache().put(uri,sddo);
        sddo.setUri(uri);
		
		return sddo;
	}

    /* 
    public static SDDObject find(String uri) {
        if (SDDObject.getCache().containsKey(uri)) {
            return SDDObject.getCache().get(uri);
        }

//        System.out.println("Looking for data acquisition schema object with uri: " + uri);

        SDDObject object = null;
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                "SELECT ?entity ?partOfSchema ?role ?inRelationTo ?relation ?inRelationToStr ?wasDerivedFrom ?alternativeName WHERE { \n" +
                "   <" + uri + "> a hasco:SDDObject . \n" +
                "   <" + uri + "> hasco:partOfSchema ?partOfSchema . \n" +
                "   OPTIONAL { <" + uri + "> hasco:hasEntity ?entity } . \n" +
                "   OPTIONAL { <" + uri + "> hasco:hasRole ?role } .  \n" +
                "   OPTIONAL { <" + uri + "> sio:SIO_000668 ?inRelationTo } . \n" +
                "   OPTIONAL { <" + uri + "> hasco:Relation ?relation } . \n" +
                "   OPTIONAL { <" + uri + "> ?relation ?inRelationTo . ?inRelationTo a hasco:SDDObject } . \n" +
                "   OPTIONAL { <" + uri + "> hasco:inRelationToLabel ?inRelationToStr } . \n" +
                "   OPTIONAL { <" + uri + "> hasco:wasDerivedFrom ?wasDerivedFrom } . \n" +
                "   OPTIONAL { <" + uri + "> dcterms:alternativeName ?alternativeName } . \n" +
                "}";

//        System.out.println("SDDObject find(String uri) query: " + queryString);

        ResultSetRewindable resultsrw = SPARQLUtils.select(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.SPARQL_QUERY), queryString);

        if (!resultsrw.hasNext()) {
            //System.out.println("[WARNING] SDDObject. Could not find object with uri: " + uri);
            SDDObject.getCache().put(uri, null);
            return null;
        }

        QuerySolution soln = resultsrw.next();
        String labelStr = "";
        String partOfSchemaStr = "";
        String positionStr = "";
        String entityStr = "";
        String roleStr = "";
        String inRelationToStr = "";
        String inRelationToLabelStr = "";
        String relationStr = "";
        String wasDerivedFromStr = "";
        String alternativeName = "";

        try {
            if (soln != null) {

                labelStr = FirstLabel.getPrettyLabel(uri);

                try {
                    if (soln.getResource("entity") != null && soln.getResource("entity").getURI() != null) {
                        entityStr = soln.getResource("entity").getURI();
                    }
                } catch (Exception e1) {
                    entityStr = "";
                }

                try {
                    if (soln.getResource("partOfSchema") != null && soln.getResource("partOfSchema").getURI() != null) {
                        partOfSchemaStr = soln.getResource("partOfSchema").getURI();
                    }
                } catch (Exception e1) {
                    partOfSchemaStr = "";
                }

                try {
                    if (soln.getResource("role") != null && soln.getResource("role").getURI() != null) {
                        roleStr = soln.getResource("role").getURI();
                    }
                } catch (Exception e1) {
                    roleStr = "";
                }

                try {
                    if (soln.getResource("inRelationTo") != null && soln.getResource("inRelationTo").getURI() != null) {
                        inRelationToStr = soln.getResource("inRelationTo").getURI();
                    }
                } catch (Exception e1) {
                    inRelationToStr = "";
                }

                try {
                    if (soln.getResource("inRelationToStr") != null) {
                        inRelationToLabelStr = soln.getResource("inRelationToStr").toString();
                    }
                } catch (Exception e1) {
                    inRelationToLabelStr = "";
                }

                try {
                    if (soln.getLiteral("wasDerivedFrom") != null) {
                        wasDerivedFromStr = soln.getLiteral("wasDerivedFrom").toString();
                    }
                } catch (Exception e1) {
                    wasDerivedFromStr = "";
                }

                try {
                    if (soln.getLiteral("alternativeName") != null) {
                        alternativeName = soln.getLiteral("alternativeName").toString();
                    }
                } catch (Exception e1) {
                    alternativeName = "";
                }

                try {
                    if (soln.getResource("relation") != null && soln.getResource("relation").getURI() != null) {
                        relationStr = soln.getResource("relation").getURI();
                    }
                } catch (Exception e1) {
                    relationStr = "";
                }

                object = new SDDObject(uri,
                        labelStr,
                        partOfSchemaStr,
                        positionStr,
                        entityStr,
                        roleStr,
                        inRelationToStr,
                        inRelationToLabelStr,
                        wasDerivedFromStr,
                        relationStr);
                object.setAlternativeName(alternativeName);
            }
        } catch (Exception e) {
            System.out.println("[ERROR] SDDObject.find() e.Message: " + e.getMessage());
        }

        SDDObject.getCache().put(uri, object);
        return object;
    }
    */

    public static List<String> findUriBySchema(String schemaUri) {
        //System.out.println("Looking for data acquisition schema objects for <" + schemaUri + ">");

        List<String> objectUris = new ArrayList<String>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                "SELECT ?uri WHERE { \n" +
                "   ?uri a hasco:SDDObject . \n" +
                "   ?uri hasco:partOfSchema <" + schemaUri + "> . \n" +
                "}";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        if (!resultsrw.hasNext()) {
            //System.out.println("[WARNING] SDDObject. Could not find objects for schema: " + schemaUri);
            return objectUris;
        }

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            try {
                if (soln != null && soln.getResource("uri") != null && soln.getResource("uri").getURI() != null) {
                    String uriStr = soln.getResource("uri").getURI();
                    if (uriStr != null) {
                        objectUris.add(uriStr);
                    }
                }
            }  catch (Exception e) {
                System.out.println("[ERROR] SDDObject.findBySchema() e.Message: " + e.getMessage());
            }
        }

        return objectUris;
    }

    public static List<SDDObject> findBySchema(String schemaUri) {
        //System.out.println("Looking for data acquisition schema objects for <" + schemaUri + ">");

        List<SDDObject> objects = new ArrayList<SDDObject>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                "SELECT ?uri ?label ?hasEntity ?hasRole ?inRelationTo ?relation WHERE { \n" +
                "   ?uri a hasco:SDDObject . \n" +
                "   ?uri hasco:partOfSchema <" + schemaUri + "> . \n" +
                "   ?uri rdfs:label ?label . \n" +
                "} " +
                "ORDER BY ?label";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        if (!resultsrw.hasNext()) {
            //System.out.println("[WARNING] SDDObject. Could not find objects for schema: " + schemaUri);
            return objects;
        }

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            try {
                if (soln != null && soln.getResource("uri") != null && soln.getResource("uri").getURI() != null) {

                    SDDObject obj = SDDObject.find(soln.getResource("uri").getURI());
                    if (obj != null) {
                        objects.add(obj);
                    }
                }
            }  catch (Exception e) {
                System.out.println("[ERROR] SDDObject.findBySchema() e.Message: " + e.getMessage());
            }
        }

        return objects;
    }

    public static SDDObject findByLabelInSchema(String schemaUri, String label) {
        //System.out.println("SDDObject: label = [" + label + "]");
        List<SDDObject> schemaList = findBySchema(schemaUri);
        for (SDDObject daso : schemaList) {
            //System.out.println("SDDObject: label in daso = [" + daso.getLabel() + "]");
            if (daso.getLabel() != null && daso.getLabel().equals(label)) {
                return daso;
            }
        }
        return null;
    }

    public static String findUriFromRole(String newInRelationTo, List<SDDObject> objects) {
        if (newInRelationTo == null) {
            return "";
        }
        if (newInRelationTo.equals("DefaultObject")) {
            return URIUtils.replacePrefixEx("hasco:DefaultObject");
        }
        for (SDDObject daso : objects) {
            if (daso.getRole().equals(newInRelationTo)) {
                return URIUtils.replacePrefixEx(daso.getUri());
            }
        }
        return "";
    }

    /* 
    @Override
    public boolean saveToTripleStore() {
        if (uri == null || uri.equals("")) {
            System.out.println("[ERROR] Trying to save SDDO without assigning an URI");
            return false;
        }
        if (partOfSchema == null || partOfSchema.equals("")) {
            System.out.println("[ERROR] Trying to save SDDO without assigning SDD's URI");
            return false;
        }

        deleteFromTripleStore();

        String insert = "";
        insert += NameSpaces.getInstance().printSparqlNameSpaceList();
        insert += INSERT_LINE1;

        if (!getNamedGraph().isEmpty()) {
            insert += " GRAPH <" + getNamedGraph() + "> { ";
        } else {
            insert += " GRAPH <" + Constants.DEFAULT_REPOSITORY + "> { ";
        }

        insert += this.getUri() + " a hasco:SDDObject . ";
        insert += this.getUri() + " rdfs:label  \"" + label + "\" . ";
        if (partOfSchema.startsWith("http")) {
            insert += this.getUri() + " hasco:partOfSchema <" + partOfSchema + "> .  ";
        } else {
            insert += this.getUri() + " hasco:partOfSchema " + partOfSchema + " .  ";
        }
        if (!role.equals("")) {
            insert += this.getUri() + " hasco:hasRole  \"" + role + "\" . ";
        }
        if (!entity.equals("")) {
            insert += this.getUri() + " hasco:hasEntity "  + entity + " .  ";
        }
        if (!inRelationTo.equals("")) {
            String inRelationToStr =  URIUtils.replacePrefixEx(inRelationTo);
            if (inRelationToStr.startsWith("<")) {
                insert += this.getUri() + " sio:SIO_000668 " +  inRelationToStr + " .  ";
            } else {
                insert += this.getUri() + " sio:SIO_000668 <" + inRelationToStr + "> .  ";
            }
        }
        if (!relation.equals("")) {
            String relationStr =  URIUtils.replacePrefixEx(relation);
            if (relationStr.startsWith("<")) {
                insert += this.getUri() + " hasco:Relation " +  relationStr + " .  ";
            } else {
                insert += this.getUri() + " hasco:Relation <" + relationStr + "> .  ";
            }
        }

        // CLOSING NAMEDGRAPH
        insert += " } ";

        insert += LINE_LAST;

        try {
            UpdateRequest request = UpdateFactory.create(insert);
            UpdateProcessor processor = UpdateExecutionFactory.createRemote(
                    request, CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_UPDATE));
            processor.execute();
        } catch (QueryParseException e) {
            System.out.println("QueryParseException due to update query: " + insert);
            throw e;
        }

        return true;
    }
    */

    @Override
    public void save() {
        //System.out.println("Saving SDDObject [" + uri + "]");
        saveToTripleStore();
    }

    @Override
    public void delete() {
        deleteFromTripleStore();
    }

    @Override
    public void deleteFromTripleStore() {
        super.deleteFromTripleStore();
        SDDAttribute.resetCache();
    }

}

