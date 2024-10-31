package org.hascoapi.entity.pojo;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

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
import org.hascoapi.annotations.PropertyField;
import org.hascoapi.annotations.PropertyValueType;
import org.hascoapi.Constants;
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.NameSpaces;
import org.hascoapi.utils.FirstLabel;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.vocabularies.PROV;
import org.hascoapi.vocabularies.RDF;
import org.hascoapi.vocabularies.RDFS;
import org.hascoapi.vocabularies.SIO;
import org.hascoapi.vocabularies.VSTOI;
import org.hascoapi.utils.SPARQLUtils;

@JsonFilter("sddAttributeFilter")
public class SDDAttribute extends HADatAcThing {

    @PropertyField(uri="hasco:partOfSchema")
    private String partOfSchema;

    @PropertyField(uri="hasco:schemaPosition")
    private String position;

    @PropertyField(uri="hasco:listPosition")
    private String listPosition;

    @PropertyField(uri="hasco:isVariableOf")
    private String sddoUri;

    private int positionInt;

    /* 
    tempPositionInt is set every time a new csv file is loaded. tempPositionIn = -1 indicates that the attribute is not valid for the given cvs
        - because an original position is out of range for the csv
        - because there is no original position and the given column does not match any of the labels in the CSV
    */
    private int tempPositionInt;

    @PropertyField(uri="hasco:hasEntity")
    private String entity;

    private String entityLabel;

    @PropertyField(uri="hasco:hasAttribute")
    private String attribute;

    private String attributeLabel;

    @PropertyField(uri="hasco:hasUnit")
    private String unit;

    private String unitLabel;

    @PropertyField(uri="hasco:hasEvent")
    private String sddeUri;

    @PropertyField(uri="sio:SIO_000668")
    private String inRelationTo;

    @PropertyField(uri="hasco:relation")
    private String relation;

    private Map<String, String> relations = new HashMap<String, String>();

    @PropertyField(uri="prov:wasDerivedFrom")
    private String wasDerivedFrom;

    @PropertyField(uri = "vstoi:hasSIRManagerEmail")
    private String hasSIRManagerEmail;

    private boolean isMeta;

    /*************** 
     * 
     *    CACHE
     * 
     ***************/
     
     private static Map<String, SDDAttribute> SDDACache;

     private SemanticDataDictionary sdd;

     private static Map<String, SDDAttribute> getCache() {
        if (SDDACache == null) {
            SDDACache = new HashMap<String, SDDAttribute>(); 
        }
	    return SDDACache;
    }

    public static void resetCache() {
	    SDDACache = null;
    }

    /********************** 
     * 
     *    CONSTRUCTORS 
     * 
     **********************/

    public SDDAttribute() {
	    SDDAttribute.getCache();
    }

    /*
    public SDDAttribute(String uri, String partOfSchema) {
        this.uri = uri;
        this.partOfSchema = partOfSchema;
        this.label = "";
        this.position = "";
        this.positionInt = -1;
        this.setEntity("");
        this.setAttribute("");
        this.setUnit("");
        this.sddeUri = "";
        this.sddoUri = "";
        this.isMeta = false;
	    SDDAttribute.getCache();
    }

    public SDDAttribute(String uri, 
            String label,
            String partOfSchema,
            String position, 
            String entity, 
            String attribute, 
            String unit, 
            String sddeUri, 
            String sddoUri) {
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
        this.setAttribute(attribute);
        this.setUnit(unit);
        this.sddeUri = sddeUri;
        this.sddoUri = sddoUri;
	    SDDAttribute.getCache();
    }
    */

    /***************************** 
     * 
     *    SETTERS AND GETTERS
     * 
     *****************************/

    @JsonIgnore
    public String getUriNamespace() {
        return URIUtils.replaceNameSpaceEx(uri.replace("<","").replace(">",""));
    }

    @JsonIgnore
    public void setSemanticDataDictionary(SemanticDataDictionary sdd) {
        this.sdd = sdd;
    }

    public String getPartOfSchema() {
        if (partOfSchema == null) {
            return "";
        } else {
            return partOfSchema;
        }
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
    @JsonIgnore
    public int getPositionInt() {
        return positionInt;
    }

    public String getListPosition() {
        return listPosition;
    }
    public void setListPosition(String listPosition) {
        this.listPosition = listPosition;
    }

    @JsonIgnore
    public int getTempPositionInt() {
        return tempPositionInt;
    }
    @JsonIgnore
    public void setTempPositionInt(int tempPositionInt) {
        this.tempPositionInt = tempPositionInt;
    }

    public String getEntity() {
        if (entity == null) {
            return "";
        } else {
            return entity;
        }
    }
    @JsonIgnore
    public String getEntityNamespace() {
        if (entity == "") {
            return "";
        }
        return URIUtils.replaceNameSpaceEx(entity.replace("<","").replace(">",""));
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
    public String getEntityLabel() {
        if (entityLabel.equals("")) {
            return URIUtils.replaceNameSpaceEx(entity);
        }
        return entityLabel;
    }
    @JsonIgnore
    public String getEntityViewLabel() {
        if (isMeta) {
            return "";
        }
        if (sddoUri != null && !sddoUri.equals("") && getObject() != null) {
            return "[" + getObject().getEntityLabel() + "]";
        }
        if (sddoUri == null || sddoUri.equals("")) {
            /* 
            if (sdd != null && (!sdd.getIdLabel().equals("") || !sdd.getOriginalIdLabel().equals(""))) {
                return "[inferred from DefaultObject]";
            }
            */
            return "";
        } else {
            return getEntityLabel();
        }
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

    public String getAttribute() {
        return attribute;
    }
    @JsonIgnore
    public String getAttributeNamespace() {
        return URIUtils.replaceNameSpaceEx(attribute.replace("<","").replace(">",""));
    }
    public void setAttribute(String attribute) {
        this.attribute = attribute;
        this.isMeta = true;
        /* 
        if (!SemanticDataDictionary.METASDDA.contains(URIUtils.replaceNameSpaceEx(attr))) {
            this.isMeta = false;
        }
        */    
    }
    @JsonIgnore
    public String getAttributeLabel() {
        return attributeLabel;
    }
    @JsonIgnore
    public String getAnnotatedAttribute() {
        String annotation;
        annotation = attributeLabel + " [" + URIUtils.replaceNameSpaceEx(attribute.replace("<","").replace(">","")) + "]";	
        return annotation;
    }

    public String getInRelationToUri() {
        String inRelationToUri = "";
        for (String key : relations.keySet()) {
            inRelationToUri = relations.get(key);
            break;
        }
        return inRelationToUri;
    }
    @JsonIgnore
    public String getInRelationToLabel() {
        String inRelationTo = getInRelationToUri();
        if (inRelationTo == null || inRelationTo.equals("")) {
            return "";
        } else {
            return FirstLabel.getPrettyLabel(inRelationTo);
        }
    }
    public String getInRelationToUri(String relationUri) {
        //System.out.println("[SDDA] relations: " + relations);
        if (relations.containsKey(relationUri)) {
            return relations.get(relationUri);
        }
        return "";
    }
    public void addRelation(String relationUri, String inRelationToUri) {
        relations.put(relationUri, inRelationToUri);
    }

    public String getUnit() {
        if (unit == null) {
            return "";
        } else {
            return unit;
        }
    }
    @JsonIgnore
    public String getUnitNamespace() {
        if (unit == "") {
            return "";
        }
        return URIUtils.replaceNameSpaceEx(unit.replace("<","").replace(">",""));
    }
    public void setUnit(String unit) {
        this.unit = unit;
        if (unit == null || unit.equals("")) {
            this.unitLabel = "";
        } else {
            this.unitLabel = FirstLabel.getPrettyLabel(unit);
        }
    }
    @JsonIgnore
    public String getUnitLabel() {
        if (unitLabel.equals("")) {
            return URIUtils.replaceNameSpaceEx(unit);
        }
        return unitLabel;
    }
    @JsonIgnore
    public String getAnnotatedUnit() {
        String annotation;
        if (unitLabel.equals("")) {
            if (unit == null || unit.equals("")) {
                return "";
            }
            annotation = URIUtils.replaceNameSpaceEx(unit);
        } else {
            annotation = unitLabel;
        }
        if (!getUnitNamespace().equals("")) {
            annotation += " [" + getUnitNamespace() + "]";
        } 
        return annotation;
    }

    public String getObjectUri() {
        return sddoUri;
    }
    public void setObjectUri(String sddoUri) {
        this.sddoUri = sddoUri;
    }
    public SDDObject getObject() {
        if (sddoUri == null || sddoUri.equals("")) {
            return null;
        }
        return SDDObject.find(sddoUri);
    }
    @JsonIgnore
    public String getObjectNamespace() {
        if (sddoUri == null || sddoUri.equals("")) {
            return "";
        }
        return URIUtils.replaceNameSpaceEx(sddoUri.replace("<","").replace(">",""));
    }
    @JsonIgnore
    public String getObjectViewLabel() {
        if (sddoUri == null || sddoUri.equals("")) {
            /* 
            if (sdd != null && (!sdd.getIdLabel().equals("") || !sdd.getOriginalIdLabel().equals(""))) {
                return "[DefaultObject]";
            }
            */
            return "";
        } else {
            SDDObject sddo = SDDObject.find(sddoUri);
            if (sddo == null || sddo.getLabel() == null || sddo.getLabel().equals("")) {
                return sddoUri;
            }
            return sddo.getLabel();
        }
    }

    public String getEventUri() {
        return sddeUri;
    }
    public void setEventUri(String sddeUri) {
        this.sddeUri = sddeUri;
    }
    public SDDObject getEvent() {
        if (sddeUri == null || sddeUri.equals("")) {
            return null;
        }
        return SDDObject.find(sddeUri);
    }
    
    public String getInRelationTo() {
        return inRelationTo;
    }
    public void setInRelationTo(String inRelationTo) {
        this.inRelationTo = inRelationTo;
    }

    public String getRelation() {
        return this.relation;
    }
    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getEventNamespace() {
        if (sddeUri == null || sddeUri.equals("")) {
            return "";
        }
        return URIUtils.replaceNameSpaceEx(sddeUri.replace("<","").replace(">",""));
    }

    @JsonIgnore
    public String getEventViewLabel() {
        if (isMeta) {
            return "";
        }
        if (sddeUri == null || sddeUri.equals("")) {
            /* 
            if (sdd != null && !sdd.getTimestampLabel().equals("")) {
                return "[value at label " + sdd.getTimestampLabel() + "]";
            }
            */
            return "";
        } else {
            //SDDEvent sdde = SDDEvent.find(sddeUri);
            SDDObject sdde = SDDObject.find(sddeUri);
            if (sdde == null || sdde.getLabel() == null || sdde.getLabel().equals("")) {
                return sddeUri;
            }
            return sdde.getLabel();
        }
    }

    public String getWasDerivedFrom() {
        return this.wasDerivedFrom;
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

    public static int getNumberSDDAs() {
        String query = "";
        query += NameSpaces.getInstance().printSparqlNameSpaceList();
        query += "select distinct (COUNT(?x) AS ?tot) where {" + 
                " ?x a <" + HASCO.SDD_ATTRIBUTE + "> } ";

        //System.out.println("Study query: " + query);

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

    public static SDDAttribute find(String uri) {
        if (uri == null || uri.isEmpty()) {
            System.out.println("[ERROR] No valid URI provided to retrieve SDDAttribute: " + uri);
            return null;
        }

		//System.out.println("Study.java : in find(): uri = [" + uri + "]");
	    SDDAttribute sdda = new SDDAttribute();
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
					sdda.setLabel(string);
				} else if (statement.getPredicate().getURI().equals(RDF.TYPE)) {
					sdda.setTypeUri(string); 
				} else if (statement.getPredicate().getURI().equals(HASCO.HASCO_TYPE)) {
					sdda.setHascoTypeUri(string); 
				} else if (statement.getPredicate().getURI().equals(HASCO.PART_OF_SCHEMA)) {
					sdda.setPartOfSchema(string);
				} else if (statement.getPredicate().getURI().equals(HASCO.HAS_SCHEMA_POSITION)) {
					sdda.setPosition(string);
				} else if (statement.getPredicate().getURI().equals(HASCO.LIST_POSITION)) {
					sdda.setListPosition(string);
				} else if (statement.getPredicate().getURI().equals(HASCO.IS_VARIABLE_OF)) {
					sdda.setObjectUri(string);
                } else if (statement.getPredicate().getURI().equals(HASCO.HAS_ENTITY)) {
                    sdda.setEntity(string);
                } else if (statement.getPredicate().getURI().equals(HASCO.HAS_ATTRIBUTE)) {
                    sdda.setAttribute(string);
                } else if (statement.getPredicate().getURI().equals(HASCO.HAS_UNIT)) {
                    sdda.setUnit(string);
                } else if (statement.getPredicate().getURI().equals(HASCO.HAS_EVENT)) {
                    sdda.setEventUri(string);
				} else if (statement.getPredicate().getURI().equals(SIO.IN_RELATION_TO)) {
					sdda.setInRelationTo(string);
				} else if (statement.getPredicate().getURI().equals(HASCO.HAS_RELATION)) {
					sdda.setRelation(string);
				} else if (statement.getPredicate().getURI().equals(RDFS.COMMENT)) {
					sdda.setComment(string);
                } else if (statement.getPredicate().getURI().equals(PROV.WAS_DERIVED_FROM)) {
					sdda.setWasDerivedFrom(string);
                } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_SIR_MANAGER_EMAIL)) {
					sdda.setHasSIRManagerEmail(string);
				}
			}
		}

        for (Map.Entry<String, String> entry : relationMap.entrySet() ) {
            sdda.addRelation(entry.getKey(), entry.getValue());
        }
	    SDDAttribute.getCache().put(uri,sdda);
        sdda.setUri(uri);
		
		return sdda;
	}

    // Given a study URI, 
    // returns a list of SDDA's
    // (we need to go study -> data acqusition(s) -> data acqusition schema(s) -> data acquisition schema attributes)
    public static List<SDDAttribute> findByStudy(String studyUri){
        //System.out.println("Looking for data acquisition schema attributes from study " + studyUri);
        if (studyUri.startsWith("http")) {
            studyUri = "<" + studyUri + ">";
        }
        List<SDDAttribute> attributes = new ArrayList<SDDAttribute>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                "SELECT ?uri ?hasEntity ?schemaUri ?attrUri" + 
                " ?hasUnit ?hasSDDO ?hasSDDE ?hasSource ?isPIConfirmed WHERE { " + 
                "    ?sdd hasco:isSDDOf " + studyUri + " .  " +
                "    ?sdd hasco:hasSchema ?schemaUri .  "+
                "    ?uri hasco:partOfSchema ?schemaUri .  " +
                "    ?uri a hasco:SDDAttribute . " + 
                "    ?uri hasco:hasAttribute ?attrUri . " +
                "} ";
        //System.out.println("[SDDA] query string = \n" + queryString);

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        if (!resultsrw.hasNext()) {
            //System.out.println("[WARNING] SDDAttribute. Could not find SDDA's with attribute: " + studyUri);
            return attributes;
        }

        String uriStr = "";

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            if (soln != null) {

                try {
                    if (soln.getResource("uri") != null && soln.getResource("uri").getURI() != null) {
                        uriStr = soln.getResource("uri").getURI();
                        SDDAttribute attr = find(uriStr);
                        attributes.add(attr);
                    }
                } catch (Exception e1) {
                    System.out.println("[ERROR] SDDAttribute. URI: " + uriStr);
                }
            }
        }
        attributes.sort(Comparator.comparing(SDDAttribute::getPositionInt));

        return attributes;
    }

    public static List<String> findUriBySchema(String schemaUri) {
        //System.out.println("Looking for data acquisition schema attribute URIs for <" + schemaUri + ">");

        List<String> attributeUris = new ArrayList<String>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                "SELECT ?uri  WHERE { \n" + 
                " ?uri a hasco:SDDAttribute . \n" + 
                " ?uri hasco:partOfSchema <" + schemaUri + "> . \n" + 
                "} ";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        if (!resultsrw.hasNext()) {
            //System.out.println("[WARNING] SDDAttribute. Could not find attributes for schema: <" + schemaUri + ">");
            return attributeUris;
        }

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            try {
                if (soln.getResource("uri") != null && soln.getResource("uri").getURI() != null) {
                    String uri = soln.getResource("uri").getURI();
                    attributeUris.add(uri);
                }
            } catch (Exception e1) {
                System.out.println("[ERROR] SDDAttribute.findBySchema() URI: <" + schemaUri + ">");
                e1.printStackTrace();
            }
        }
        return attributeUris;
    }

    public static List<SDDAttribute> findBySchema(String schemaUri) {
        //System.out.println("Looking for data acquisition schema attributes for <" + schemaUri + ">");

        List<SDDAttribute> attributes = new ArrayList<SDDAttribute>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                "SELECT ?uri ?label WHERE { \n" + 
                " ?uri a hasco:SDDAttribute . \n" + 
                " ?uri hasco:partOfSchema <" + schemaUri + "> . \n" + 
                " ?uri rdfs:label ?label . \n" + 
                " } " + 
                " ORDER BY ?label";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        if (!resultsrw.hasNext()) {
            //System.out.println("[WARNING] SDDAttribute. Could not find attributes for schema: <" + schemaUri + ">");
            return attributes;
        }

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            try {
                if (soln.getResource("uri") != null && soln.getResource("uri").getURI() != null) {
                    String uri = soln.getResource("uri").getURI();
                    SDDAttribute attr = find(uri);
                    attributes.add(attr);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
                System.out.println("[ERROR] SDDAttribute.findBySchema() URI: <" + schemaUri + ">");
                e1.printStackTrace();
            }
        }
        attributes.sort(Comparator.comparing(SDDAttribute::getPositionInt));

        return attributes;
    }

    @Override
    public void save() {
        //System.out.println("Saving SDDAttribute [" + uri + "]");
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


