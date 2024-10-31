package org.hascoapi.entity.pojo;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.hascoapi.Constants;
import org.hascoapi.annotations.PropertyField;
import org.hascoapi.annotations.PropertyValueType;
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.FirstLabel;
import org.hascoapi.utils.NameSpaces;
import org.hascoapi.utils.SPARQLUtils;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.vocabularies.RDF;
import org.hascoapi.vocabularies.RDFS;
import org.hascoapi.vocabularies.VSTOI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonFilter("studyObjectFilter")
public class StudyObject extends HADatAcThing {

    public static String LOCATION = "http://semanticscience.org/resource/SIO_000317";
    public static String TIME = "http://semanticscience.org/resource/SIO_000417";

    public static String INDENT1 = "     ";
    public static String INSERT_LINE1 = "INSERT DATA {  ";
    public static String DELETE_LINE1 = "DELETE WHERE {  ";
    public static String LINE3 = INDENT1 + "a         hasco:StudyObject;  ";
    public static String DELETE_LINE3 = " ?p ?o . ";
    public static String LINE_LAST = "}  ";
    public static String PREFIX = "OBJ-";

    private static final Logger log = LoggerFactory.getLogger(StudyObject.class);

    // Within mapIdStudyObjects
    //    0 -> studyObject URI
    public static final String STUDY_OBJECT_URI = "STUDY_OBJECT_URI";
    //    1 -> originalID of object in the object’s scope (if any)
    public static final String SUBJECT_ID = "SUBJECT_ID";
    //    2 -> URI of object in the object’s scope (if any)
    public static final String SCOPE_OBJECT_URI = "SCOPE_OBJECT_URI";
    public static final String SCOPE_OBJECT_SOC_URI = "SCOPE_OBJECT_SOC_URI";
    //    3 -> studyObjectType
    public static final String STUDY_OBJECT_TYPE = "STUDY_OBJECT_TYPE";
    //    4 -> soc's type
    public static final String SOC_TYPE = "SOC_TYPE";
    //         soc's label
    public static final String SOC_LABEL = "SOC_LABEL";
    public static final String SOC_URI = "SOC_URI";
    public static final String OBJECT_ORIGINAL_ID = "OBJECT_ORIGINAL_ID";
    public static final String OBJECT_TIME = "OBJECT_TIME";
 
    @PropertyField(uri="hasco:originalID")
    String originalId;
    
    @PropertyField(uri="hasco:isMemberOf", valueType=PropertyValueType.URI)
    String isMemberOfUri;
    
    @PropertyField(uri="hasco:hasRole", valueType=PropertyValueType.URI)
    String roleUri = "";
    
    @PropertyField(uri="hasco:hasObjectScope", valueType=PropertyValueType.URI)
    List<String> scopeUris = new ArrayList<String>();
    
    @PropertyField(uri="hasco:hasTimeObjectScope", valueType=PropertyValueType.URI)
    List<String> timeScopeUris = new ArrayList<String>();
    
    @PropertyField(uri="hasco:hasSpaceObjectScope", valueType=PropertyValueType.URI)
    List<String> spaceScopeUris = new ArrayList<String>();

    @PropertyField(uri="vstoi:hasSIRManagerEmail")
    String hasSIRManagerEmail;
    
    public StudyObject() {
        this("", "");
    }

    public StudyObject(String uri, String isMemberOfUri) {
        setUri(uri);
        setTypeUri("");
        setHascoTypeUri("");
        setOriginalId("");
        setLabel("");
        setIsMemberOfUri(isMemberOfUri);
        setComment("");
        setHasSIRManagerEmail("");
    }

    public StudyObject(String uri,
            String typeUri,
            String hascoTypeUri,
            String originalId,
            String label,
            String isMemberOfUri,
            String comment,
            List<String> scopeUris,
            List<String> timeScopeUris,
            List<String> spaceScopeUris,
            String hasSIRManagerEmail) {
        setUri(uri);
        setTypeUri(typeUri);
        setHascoTypeUri(hascoTypeUri);
        setOriginalId(originalId);
        setLabel(label);
        setIsMemberOfUri(isMemberOfUri);
        setComment(comment);
        setScopeUris(scopeUris);
        setTimeScopeUris(timeScopeUris);
        setSpaceScopeUris(spaceScopeUris);
        setHasSIRManagerEmail(hasSIRManagerEmail);
    }

    public StudyObject(String uri,
            String typeUri,
            String hascoTypeUri,
            String originalId,
            String label,
            String isMemberOfUri,
            String comment,
            String hasSIRManagerEmail) { 
        setUri(uri);
        setTypeUri(typeUri);
        setHascoTypeUri(hascoTypeUri);
        setOriginalId(originalId);
        setLabel(label);
        setIsMemberOfUri(isMemberOfUri);
        setComment(comment);
        setHasSIRManagerEmail(hasSIRManagerEmail);
    }

    @JsonIgnore
    public StudyObjectType getStudyObjectType() {
        if (typeUri == null || typeUri.equals("")) {
            return null;
        }
        return StudyObjectType.find(typeUri);
    }

    public boolean isLocation() {
        if (typeUri == null || typeUri.equals("")) {
            return false;
        }
        return (typeUri.equals(LOCATION));
    }

    public boolean isTime() {
        if (typeUri == null || typeUri.equals("")) {
            return false;
        }
        return (typeUri.equals(TIME));
    }

    public String getRoleUri() {
        return roleUri;
    }
    public void setRoleUri(String roleUri) {
        this.roleUri = roleUri;
    }

    public String getOriginalId() {
        return originalId;
    }
    public void setOriginalId(String originalId) {
        this.originalId = originalId;
    }
    public String getOriginalIdLabel() {
    	if (originalId != null && !originalId.isEmpty()) {
    		return originalId;
    	}
    	return uri;
    }

    public String getIsMemberOfUri() {
        return isMemberOfUri;
    }
    public void setIsMemberOfUri(String isMemberOfUri) {
        this.isMemberOfUri = isMemberOfUri;
    }
    public StudyObjectCollection getIsMemberOf() {
        if (isMemberOfUri == null) {
            return null;
        }
        return StudyObjectCollection.find(isMemberOfUri);
    }	

    public String getHasSIRManagerEmail() {
        return hasSIRManagerEmail;
    }
    public void setHasSIRManagerEmail(String hasSIRManagerEmail) {
        this.hasSIRManagerEmail = hasSIRManagerEmail;
    }	

    public List<String> getScopeUris() {
        return scopeUris;
    }
    public void setScopeUris(List<String> scopeUris) {
        this.scopeUris = scopeUris;
    }
    public void addScopeUri(String scopeUri) {
        this.scopeUris.add(scopeUri);
    }

    public List<String> getTimeScopeUris() {
        return timeScopeUris;
    }
    public void setTimeScopeUris(List<String> timeScopeUris) {
        this.timeScopeUris = timeScopeUris;
    }
    public void addTimeScopeUri(String timeScopeUri) {
        this.timeScopeUris.add(timeScopeUri);
    }

    public List<String> getSpaceScopeUris() {
        return spaceScopeUris;
    }
    public void setSpaceScopeUris(List<String> spaceScopeUris) {
        this.spaceScopeUris = spaceScopeUris;
    }
    public void addSpaceScopeUri(String spaceScopeUri) {
        this.spaceScopeUris.add(spaceScopeUri);
    }

    public String getGroupId() {
        String query = "";
        query += NameSpaces.getInstance().printSparqlNameSpaceList();
        query += "SELECT ?id WHERE { \n" + 
        		 " ?grpUri hasco:ofSOC ?soc . \n" + 
        		 " ?grpUri hasco:hasGroupId ?id . \n" + 
        		 " <" + uri + "> hasco:isMemberOf ?soc . " + 
        		 " <" + uri + "> hasco:isGroupMember ?grpUri . " + 
        		 "}";
        try {
            ResultSetRewindable resultsrw = SPARQLUtils.select(
                    CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), query);

            if (resultsrw.hasNext()) {
                QuerySolution soln = resultsrw.next();
                return soln.getLiteral("id").getString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    	
    }
    
    public static int getNumberStudyObjects() {
        String query = "";
        query += NameSpaces.getInstance().printSparqlNameSpaceList();
        query += " select (count(?obj) as ?tot) where " + 
                " {?obj hasco:isMemberOf ?collection . " +
                "  ?obj a ?objType . " + 
                " FILTER NOT EXISTS { ?objType rdfs:subClassOf* hasco:StudyObjectCollection . } " + 
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

    public static List<String> retrieveScopeUris(String objUri) {
        List<String> retrievedUris = new ArrayList<String>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                "SELECT  ?scopeUri WHERE { " + 
                " <" + objUri + "> hasco:hasObjectScope ?scopeUri . " + 
                "}";

        //System.out.println("Study.retrieveScopeUris() queryString: \n" + queryString);

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        if (!resultsrw.hasNext()) {
            return retrievedUris;
        }
        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            if (soln != null) {
                try {
                    if (soln.getResource("scopeUri") != null && soln.getResource("scopeUri").getURI() != null) {
                        retrievedUris.add(soln.getResource("scopeUri").getURI());
                    }
                } catch (Exception e1) {
                }
            }
        }
        return retrievedUris;
    }

    public static List<String> retrieveTimeScopeUris(String objUri) {
        List<String> retrievedUris = new ArrayList<String>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                "SELECT  ?timeScopeUri WHERE { " + 
                " <" + objUri + "> hasco:hasTimeObjectScope ?timeScopeUri . " + 
                "}";

        //System.out.println("Study.retrieveTimeScopeUris() queryString: \n" + queryString);

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        if (!resultsrw.hasNext()) {
            return retrievedUris;
        }
        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            if (soln != null) {
                try {
                    if (soln.getResource("timeScopeUri") != null && soln.getResource("timeScopeUri").getURI() != null) {
                        retrievedUris.add(soln.getResource("timeScopeUri").getURI());
                    }
                } catch (Exception e1) {
                }
            }
        }
        return retrievedUris;
    }

    public static List<String> retrieveTimeScopeTypeUris(String objUri) {
        List<String> retrievedUris = new ArrayList<String>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                "SELECT DISTINCT ?timeScopeUri ?timeScopeTypeUri WHERE { " + 
                " <" + objUri + "> hasco:hasTimeObjectScope ?timeScopeUri . " + 
                " ?timeScopeUri a ?timeScopeTypeUri . " +
                "}";

        //System.out.println("Study.retrieveTimeScopeUris() queryString: \n" + queryString);

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        if (!resultsrw.hasNext()) {
            return retrievedUris;
        }
        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            if (soln != null) {
                try {
                    if (soln.getResource("timeScopeTypeUri") != null && soln.getResource("timeScopeTypeUri").getURI() != null) {
                        retrievedUris.add(soln.getResource("timeScopeTypeUri").getURI());
                    }
                } catch (Exception e1) {
                }
            }
        }
        return retrievedUris;
    }

    public static List<String> retrieveSpaceScopeUris(String objUri) {
        List<String> retrievedUris = new ArrayList<String>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                "SELECT  ?spaceScopeUri WHERE { " + 
                " <" + objUri + "> hasco:hasSpaceObjectScope ?spaceScopeUri . " + 
                "}";

        //System.out.println("Study.retrieveSpaceScopeUris() queryString: \n" + queryString);

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        if (!resultsrw.hasNext()) {
            return retrievedUris;
        }
        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            if (soln != null) {
                try {
                    if (soln.getResource("spaceScopeUri") != null && soln.getResource("spaceScopeUri").getURI() != null) {
                        retrievedUris.add(soln.getResource("spaceScopeUri").getURI());
                    }
                } catch (Exception e1) {
                }
            }
        }
        return retrievedUris;
    }

    	public static StudyObject find(String uri) {
        
        if (uri == null || uri.isEmpty()) {
            System.out.println("[ERROR] No valid URI provided to retrieve Study object: " + uri);
            return null;
        }

		//System.out.println("Study.java : in find(): uri = [" + uri + "]");
	    StudyObject studyObject = null;
	    Statement statement;
	    RDFNode object;
	    
	    String queryString = "DESCRIBE <" + uri + ">";
	    Model model = SPARQLUtils.describe(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.SPARQL_QUERY), queryString);
		
		StmtIterator stmtIterator = model.listStatements();

		if (!stmtIterator.hasNext()) {
			return null;
		} else {
			studyObject = new StudyObject();
		}
		
		while (stmtIterator.hasNext()) {
		    statement = stmtIterator.next();
		    object = statement.getObject();
			String str = URIUtils.objectRDFToString(object);
			if (uri != null && !uri.isEmpty()) {
				if (statement.getPredicate().getURI().equals(RDFS.LABEL)) {
					studyObject.setLabel(str);
				} else if (statement.getPredicate().getURI().equals(RDF.TYPE)) {
					studyObject.setTypeUri(str); 
				} else if (statement.getPredicate().getURI().equals(HASCO.HASCO_TYPE)) {
					studyObject.setHascoTypeUri(str);
				} else if (statement.getPredicate().getURI().equals(RDFS.COMMENT)) {
					studyObject.setComment(str);
				} else if (statement.getPredicate().getURI().equals(HASCO.ORIGINAL_ID)) {
					studyObject.setOriginalId(str);
				} else if (statement.getPredicate().getURI().equals(HASCO.IS_MEMBER_OF)) {
					studyObject.setIsMemberOfUri(str);
				} else if (statement.getPredicate().getURI().equals(HASCO.HAS_OBJECT_SCOPE)) {
					studyObject.addScopeUri(str);
				} else if (statement.getPredicate().getURI().equals(HASCO.HAS_TIME_OBJECT_SCOPE)) {
					studyObject.addTimeScopeUri(str);
				} else if (statement.getPredicate().getURI().equals(HASCO.HAS_SPACE_OBJECT_SCOPE)) {
					studyObject.addSpaceScopeUri(str);
				} else if (statement.getPredicate().getURI().equals(VSTOI.HAS_SIR_MANAGER_EMAIL)) {
					studyObject.setHasSIRManagerEmail(str);
				}
			}
		}

		studyObject.setUri(uri);
		
		return studyObject;
	}

    /* 
    public static StudyObject find(String objUri) {
        StudyObject obj = null;
        if (objUri == null || objUri.trim().equals("")) {
            return obj;
        }
        objUri = objUri.trim();

        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                "SELECT  ?objType ?objHascoType ?originalId ?isMemberOf ?hasLabel " + 
                "        ?hasComment ?hasSIRManagerEmail WHERE { \n" + 
                "    <" + objUri + "> a ?objType . \n" + 
                "    <" + objUri + "> hasco:hascoType ?objHascoType . \n" + 
                "    <" + objUri + "> hasco:isMemberOf ?isMemberOf . \n" + 
                "    OPTIONAL { <" + objUri + "> hasco:originalID ?originalId } . \n" + 
                "    OPTIONAL { <" + objUri + "> rdfs:label ?hasLabel } . \n" + 
                "    OPTIONAL { <" + objUri + "> rdfs:comment ?hasComment } . \n" + 
                "    OPTIONAL { <" + objUri + "> vstoi:hasSIRManagerEmail ?hasSIRManagerEmail } . \n" + 
                "}";

        //System.out.println("StudyObject find() queryString:\n" + queryString);

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        if (!resultsrw.hasNext()) {
            //System.out.println("[WARNING] StudyObject. Could not find OBJ with URI: <" + objUri + ">");
            return obj;
        }

        String typeStr = "";
        String hascoTypeStr = "";
        String originalIdStr = "";
        String isMemberOfStr = "";
        String commentStr = "";
        String hasSIRManagerEmailStr = "";

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            if (soln != null) {

                try {
                    if (soln.getResource("objType") != null && soln.getResource("objType").getURI() != null) {
                        typeStr = soln.getResource("objType").getURI();
                    }
                } catch (Exception e1) {
                    typeStr = "";
                }

                try {
                    if (soln.getResource("objHascoType") != null && soln.getResource("objHascoType").getURI() != null) {
                        hascoTypeStr = soln.getResource("objHascoType").getURI();
                    }
                } catch (Exception e1) {
                    hascoTypeStr = "";
                }

                try {
                    if (soln.getLiteral("originalId") != null && soln.getLiteral("originalId").getString() != null) {
                        originalIdStr = soln.getLiteral("originalId").getString();
                    }
                } catch (Exception e1) {
                    originalIdStr = "";
                }

                try {
                    if (soln.getResource("isMemberOf") != null && soln.getResource("isMemberOf").getURI() != null) {
                        isMemberOfStr = soln.getResource("isMemberOf").getURI();
                    }
                } catch (Exception e1) {
                    isMemberOfStr = "";
                }

                try {
                    if (soln.getLiteral("hasComment") != null && soln.getLiteral("hasComment").getString() != null) {
                        commentStr = soln.getLiteral("hasComment").getString();
                    }
                } catch (Exception e1) {
                    commentStr = "";
                }

                try {
                    if (soln.getLiteral("hasSIRManagerEmail") != null && soln.getLiteral("hasSIRManagerEmail").getString() != null) {
                        hasSIRManagerEmailStr = soln.getLiteral("hasSIRManagerEmail").getString();
                    }
                } catch (Exception e1) {
                    hasSIRManagerEmailStr = "";
                }

                obj = new StudyObject(objUri,
                        typeStr,
                        hascoTypeStr,
                        originalIdStr,
                        FirstLabel.getLabel(objUri),
                        isMemberOfStr,
                        commentStr,
                        retrieveScopeUris(objUri),
                        retrieveTimeScopeUris(objUri),
                        retrieveSpaceScopeUris(objUri),
                        hasSIRManagerEmailStr);
            }
        }

        return obj;
    }
    */

    public static Map<String, String> buildCachedUriByOriginalId() {
        Map<String, String> cache = new HashMap<String, String>();
        
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                "SELECT DISTINCT ?objUri ?originalId WHERE { \n" + 
                " ?objUri hasco:originalID ?originalId . \n" + 
                "}";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            if (soln != null) {
                if (soln.get("objUri") != null && soln.get("originalId") != null) {
                    cache.put(soln.get("originalId").toString(), soln.get("objUri").toString());
                }
            }
        }
        
        //System.out.println("buildCachedUriByOriginalId: " + cache.size());

        return cache;
    }

    public static String findUribyOriginalId(String original_id) {
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                "SELECT  ?objuri WHERE { " + 
                "	?objuri hasco:originalID \"" + original_id + "\" . " + 
                "}";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        if (resultsrw.size() >= 1) {
            QuerySolution soln = resultsrw.next();
            if (soln != null) {
                if (soln.getResource("objuri") != null) {
                    return soln.getResource("objuri").toString();
                }
            }
        } else {
            System.out.println("[WARNING] StudyObject. Could not find OBJ URI for: " + original_id);
            return "";
        }

        return "";
    }
    
    public static String findUriBySocAndOriginalId(String socUri, String original_id) {
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                "SELECT ?objuri WHERE { " + 
                "	?objuri hasco:originalID ?id . " + 
                "	?objuri hasco:isMemberOf <" + socUri + "> . " + 
      		    "   filter contains(?id,\"" + original_id + "\") " +                   
                "}";

        //System.out.println("StudyObject: findUriBySocAndOriginalId => SOC=[" + socUri + "]  originalId: [" + original_id + "]");
        //System.out.println("StudyObject: findUriBySocAndOriginalId => query=[" + queryString + "]");
        //System.out.println("StudyObject: findUriBySocAndOriginalId => CollectionUtil=[" + CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY) + "]");

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        //System.out.println("StudyObject: findUriBySocAndOriginalId => resultSize=[" + resultsrw.size() + "]");
        if (resultsrw.size() >= 1) {
            QuerySolution soln = resultsrw.next();
            if (soln != null) {
                if (soln != null && soln.getResource("objuri") != null) {
                    //System.out.println("StudyObject: findUriBySocAndOriginalId => objuri=[" + soln.getResource("objuri").toString() + "]");
                    return soln.getResource("objuri").toString();
                }
            }
        } else {
            System.out.println("[WARNING] StudyObject. Could not find OBJ URI for  SOCURI=[" + socUri + "] and original ID =[" + original_id+ "]");
            return "";
        }

        return "";
    }
    
    public static Map<String, String> buildCachedObjectBySocAndScopeUri() {
        Map<String, String> cache = new HashMap<String, String>();
        
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                "SELECT DISTINCT ?objUri ?scopeUri ?socUri WHERE { \n" + 
                " ?objUri hasco:hasObjectScope ?scopeUri . \n" + 
                " ?objUri hasco:isMemberOf ?socUri . \n" +
                "}";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            if (soln != null) {
                if (soln.get("objUri") != null 
                        && soln.get("socUri") != null 
                        && soln.get("scopeUri") != null) {
                    String key = soln.get("socUri").toString() + ":" + soln.get("scopeUri").toString();
                    cache.put(key, soln.get("objUri").toString());
                    //System.out.println("buildCachedUriBySocAndScopeUri: [" + key + "][" + soln.get("objUri") + "]");
                }
            }
        }
        
        //System.out.println("buildCachedUriBySocAndScopeUri: " + cache.size());

        return cache;
    }

    public static String findUriBySocAndScopeUri(String socUri, String scopeUri) {
    	//System.out.println("StudyObject: findUriBySocAndScopeUri: SOCURI=[" + socUri + "]  SCOPEURI=[" + scopeUri + "]");
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                "SELECT  ?objuri WHERE { " + 
                "      VALUES ?scopeuri { <" + scopeUri + "> } . " + 
                "      ?objuri hasco:hasObjectScope ?scopeuri .  " + 
                "      ?objuri hasco:isMemberOf <" + socUri + "> . " +
                "}";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        if (resultsrw.size() >= 1) {
            QuerySolution soln = resultsrw.next();
            if (soln != null) {
                if (soln.getResource("objuri") != null) {
                    return soln.getResource("objuri").toString();
                }
            }
        } else {
            System.out.println("[WARNING] StudyObject. Could not find OBJ URI for SOCURI=[" + socUri + "] and Scope URI=[" + scopeUri + "]");
            return "";
        }

        return "";
    }
    
    public static Map<String, String> buildCachedScopeBySocAndObjectUri() {
        Map<String, String> cache = new HashMap<String, String>();
        
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                "SELECT DISTINCT ?scopeUri ?socUri ?objUri WHERE { \n" + 
                "  ?objUri hasco:hasObjectScope ?scopeUri . \n" + 
                "  ?scopeUri hasco:isMemberOf ?socUri . \n" +
                "}";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            if (soln != null) {
                if (soln.get("scopeUri") != null 
                        && soln.get("socUri") != null 
                        && soln.get("objUri") != null) {
                    String key = soln.get("socUri").toString() + ":" + soln.get("objUri").toString();
                    cache.put(key, soln.get("scopeUri").toString());
                    //System.out.println("buildCachedScopeBySocAndObjectUri: [" + key + "][" + soln.get("scopeUri") + "]");
                }
            }
        }
        
        //System.out.println("buildCachedScopeBySocAndObjectUri: " + cache.size());

        return cache;
    }

    /*
     *    this query traverses the grounding path backwards because the isMemberOf is of the scopeUri 
     *    rather than the isMemberOf of the objUri
     */
    public static String findScopeBySocAndObjectUri(String socUri, String objUri) {
    	//System.out.println("StudyObject: findScopeBySocAndObjectUri: SOCURI=[" + socUri + "]  OBJURI=[" + objUri + "]");
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                "SELECT  ?scopeUri WHERE { " + 
                "      VALUES ?objUri { <" + objUri + "> } . " + 
                "      ?objUri hasco:hasObjectScope ?scopeUri .  " + 
                "      ?scopeUri hasco:isMemberOf <" + socUri + "> . " +
                "}";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        if (resultsrw.size() >= 1) {
            QuerySolution soln = resultsrw.next();
            if (soln != null) {
                if (soln.getResource("scopeUri") != null) {
                    return soln.getResource("scopeUri").toString();
                }
            }
        } else {
            System.out.println("[WARNING] StudyObject. Could not find OBJ URI for SOCURI=[" + socUri + "] and Object URI=[" + objUri + "]");
            return "";
        }

        return "";
    }

    public static List<StudyObject> findByCollection(StudyObjectCollection soc) {
        if (soc == null) {
            return null;
        }
        List<StudyObject> objects = new ArrayList<StudyObject>();

        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                "SELECT ?uri WHERE { " + 
                "   ?uri hasco:isMemberOf  <" + soc.getUri() + "> . " +
                " } ";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            if (soln != null && soln.getResource("uri").getURI() != null) {
                StudyObject object = StudyObject.find(soln.getResource("uri").getURI());
                objects.add(object);
            }
        }
        return objects;
    }

    public static List<StudyObject> findByCollectionWithPages(StudyObjectCollection soc, int pageSize, int offset) {
        if (soc == null || soc.getUri() == null) {
            return null;
        }
        return findByCollectionWithPage(soc.getUri(), pageSize, offset);
    }

    public static List<StudyObject> findByCollectionWithPage(String socUri, int pageSize, int offset) {
        if (socUri == null || socUri.isEmpty()) {
            return null;
        }
        List<StudyObject> objects = new ArrayList<StudyObject>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + 
        		"SELECT ?uri WHERE { " + 
                "   ?uri hasco:isMemberOf  <" + socUri + "> . " +
                " } " + 
                " LIMIT " + pageSize + 
                " OFFSET " + offset;

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            if (soln != null && soln.getResource("uri").getURI() != null) {
                StudyObject object = StudyObject.find(soln.getResource("uri").getURI());
                objects.add(object);
            }
        }
        return objects;
    }

    public static int getNumberStudyObjectsByCollection(String socUri) {
        String query = "";
        query += NameSpaces.getInstance().printSparqlNameSpaceList();
        query += " select (count(?obj) as ?tot) where " + 
                " { ?obj hasco:isMemberOf <" + socUri + "> . ?obj a ?objType . " + 
                " FILTER NOT EXISTS { ?objType rdfs:subClassOf* hasco:StudyObjectCollection . } " + 
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

    public static int getNumberStudyObjectsByStudy(String studyuri) {
        String query = "";
        query += NameSpaces.getInstance().printSparqlNameSpaceList();
        query += " select (count(?uri) as ?tot) where { " + 
            //"   ?subtype rdfs:subClassOf* hasco:StudyObject .  " + 
            //"   ?uri a ?subtype .  " + 
            "   ?uri hasco:isMemberOf ?socuri . " +
            "   ?socuri hasco:isMemberOf <" + studyuri + "> . " + 
            " }";
        return GenericFind.findTotalByQuery(query);
    }
    
    public static String findByCollectionJSON(StudyObjectCollection soc) {
        if (soc == null) {
            return null;
        }
        
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                "SELECT ?uri ?label WHERE { " + 
                "   ?uri hasco:isMemberOf  <" + soc.getUri() + "> . " +
                "   OPTIONAL { ?uri rdfs:label ?label } . " +
                " } ";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        ResultSetFormatter.outputAsJSON(outputStream, resultsrw);

        try {
            return outputStream.toString("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    public static Map<String, Map<String, String>> findIdUriMappings(String studyUri) {
        //System.out.println("findIdUriMappings is called!");

        Map<String, Map<String, String>> mapIdUriMappings = new HashMap<String, Map<String, String>>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList()
                + " SELECT ?studyObject ?studyObjectType ?id ?obj ?subj_id ?soc ?socType WHERE { \n"
                + " { \n"
                + "     ?studyObject hasco:originalID ?id . \n"
                + "     ?studyObject rdf:type ?studyObjectType . \n"
                + "     ?studyObject hasco:isMemberOf ?soc . \n"
                + "     ?soc rdf:type ?socType . \n"
                + "     ?soc a hasco:SubjectGroup . \n"
                + "     ?soc hasco:isMemberOf* <" + studyUri + "> . \n"
                + " } UNION { \n"
                + "     ?studyObject hasco:originalID ?id . \n"
                + "     ?studyObject rdf:type ?studyObjectType . \n"
                + "     ?studyObject hasco:isMemberOf ?soc . \n"
                + "     ?soc rdf:type ?socType . \n"
                + "     ?soc a hasco:SampleCollection . \n"
                + "     ?soc hasco:isMemberOf* <" + studyUri + "> . \n"
                + "     ?studyObject hasco:hasObjectScope ?obj . \n"
                + "     ?obj hasco:originalID ?subj_id . \n"
                + " } \n"
                + " } \n";

        //System.out.println("findIdUriMappings() query: \n" + queryString);

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        try {
            while (resultsrw.hasNext()) {           
                QuerySolution soln = resultsrw.next();
                Map<String, String> details = new HashMap<String, String>();
                if (soln.get("studyObject") != null) {
                    details.put(STUDY_OBJECT_URI, soln.get("studyObject").toString());
                } else {
                    details.put(STUDY_OBJECT_URI, "");
                }
                if (soln.get("subj_id") != null) {
                    details.put(SUBJECT_ID, soln.get("subj_id").toString());
                } else {
                    details.put(SUBJECT_ID, "");
                }
                if (soln.get("obj") != null) {
                    details.put(SCOPE_OBJECT_URI, soln.get("obj").toString());
                } else {
                    details.put(SCOPE_OBJECT_URI, "");
                }
                if (soln.get("studyObjectType") != null) {
                    details.put(STUDY_OBJECT_TYPE, soln.get("studyObjectType").toString());
                } else {
                    details.put(STUDY_OBJECT_TYPE, "");
                }
                if (soln.get("socType") != null) {
                    details.put(SOC_TYPE, soln.get("socType").toString());
                } else {
                    details.put(SOC_TYPE, "");
                }
                if (soln.get("soc") != null) {
                    details.put(SOC_URI, soln.get("soc").toString());
                } else {
                    details.put(SOC_URI, "");
                }
                mapIdUriMappings.put(soln.get("id").toString(), details);
            }
        } catch (Exception e) {
            System.out.println("Error in findIdUriMappings(): " + e.getMessage());
        }

        //System.out.println("mapIdUriMappings: " + mapIdUriMappings.keySet().size());
        
        return mapIdUriMappings;
    }

    @Override
    public void save() {
        if (!getNamedGraph().isEmpty()) {
            setNamedGraph(Constants.DEFAULT_REPOSITORY);
        }
        saveToTripleStore();
        return;
    }

    @Override
    public void delete() {
        if (!getNamedGraph().isEmpty()) {
            setNamedGraph(Constants.DEFAULT_REPOSITORY);
        }
        deleteFromTripleStore();
        return;
    }

    @Override
    public void deleteFromTripleStore() {
        super.deleteFromTripleStore();
    }

}

