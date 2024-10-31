package org.hascoapi.entity.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.ByteArrayOutputStream;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.text.WordUtils;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.hascoapi.Constants;
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.vocabularies.RDF;
import org.hascoapi.vocabularies.RDFS;
import org.hascoapi.vocabularies.VSTOI;
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.FirstLabel;
import org.hascoapi.utils.GSPClient;
import org.hascoapi.utils.NameSpaces;
import org.hascoapi.utils.SPARQLUtils;
import org.hascoapi.utils.URIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonFilter("studyObjectCollectionFilter")
public class StudyObjectCollection extends HADatAcThing implements Comparable<StudyObjectCollection> {

    private static final Logger log = LoggerFactory.getLogger(StudyObjectCollection.class);

    public static String INDENT1 = "   ";
    public static String INSERT_LINE1 = "INSERT DATA {  ";
    public static String DELETE_LINE1 = "DELETE WHERE {  ";
    public static String LINE3 = INDENT1 + "a hasco:StudyObjectCollection;  ";
    public static String DELETE_LINE3 = INDENT1 + " ?p ?o . ";
    public static String DELETE_LINE4 = "  hasco:hasLastCounter ?o . ";
    public static String LINE_LAST = "}  ";

    //private String studyUri = "";
    private String hasStatus = "";
    private String isMemberOfUri = "";
    private String hasVirtualColumnUri = "";
    private String hasRoleUri = "";
    private String hasSIRManagerEmail = "";
    private String hasLastCounter = "0";
    private String hasScopeUri = "";
    
    private List<String> spaceScopeUris = null;
    private List<String> timeScopeUris = null;
    private List<String> groupUris = null;
    private List<String> objectUris = new ArrayList<String>();

    public StudyObjectCollection() {
        this.uri = "";
        this.typeUri = "";
        this.hascoTypeUri = "";
        this.label = "";
        this.comment = "";
        this.isMemberOfUri = "";
        this.hasRoleUri = "";
        this.hasSIRManagerEmail = "";
        this.hasVirtualColumnUri = "";
        this.hasLastCounter = "0";
        this.hasScopeUri = "";
        this.spaceScopeUris = new ArrayList<String>();
        this.timeScopeUris = new ArrayList<String>();
        this.groupUris = new ArrayList<String>();
    }

    public StudyObjectCollection(
            String uri,
            String typeUri,
            String hascoTypeUri,
            String label,
            String comment,
            String isMemberOfUri,
            String hasVirtualColumnUri,
            String hasRoleUri,
            String hasSIRManagerEmail,
            String hasScopeUri,
            List<String> spaceScopeUris,
            List<String> timeScopeUris,
            List<String> groupUris,
            String hasLastCounter) {
        this.setUri(uri);
        this.setTypeUri(typeUri);
        this.setHascoTypeUri(hascoTypeUri);
        this.setLabel(label);
        this.setComment(comment);
        this.setIsMemberOfUri(isMemberOfUri);
        this.setVirtualColumnUri(hasVirtualColumnUri);
        this.setRoleUri(hasRoleUri);
        this.setHasSIRManagerEmail(hasSIRManagerEmail);
        this.setHasScopeUri(hasScopeUri);
        this.setSpaceScopeUris(spaceScopeUris);
        this.setTimeScopeUris(timeScopeUris);
        this.setGroupUris(groupUris);
        this.setLastCounter(hasLastCounter);
    }

    public StudyObjectCollection(String uri,
            String typeUri,
            String hascoTypeUri,
            String label,
            String comment,
            String studyUri) {
        this.setUri(uri);
        this.setTypeUri(typeUri);
        this.setHascoTypeUri(hascoTypeUri);
        this.setLabel(label);
        this.setComment(comment);
        this.setIsMemberOfUri(studyUri);
        this.setVirtualColumnUri("");
        this.setRoleUri("");
        this.setHasSIRManagerEmail("");
        this.setHasScopeUri("");
        this.setSpaceScopeUris(spaceScopeUris);
        this.setTimeScopeUris(timeScopeUris);
        this.setGroupUris(groupUris);
        this.hasLastCounter = "0";

    }

    @Override
    public boolean equals(Object o) {
        if((o instanceof StudyObjectCollection) && (((StudyObjectCollection)o).getUri().equals(this.getUri()))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getUri().hashCode();
    }

    @Override
    public int compareTo(StudyObjectCollection another) {
        if (this.getUri() == null || another.getUri() == null) {
            return 0;
        }
        return this.getUri().compareTo(another.getUri());
    }

    @JsonIgnore
    public StudyObjectCollectionType getStudyObjectCollectionType() {
        if (typeUri == null || typeUri.equals("")) {
            return null;
        }
        StudyObjectCollectionType socType = StudyObjectCollectionType.find(typeUri);
        return socType;    
    }

    public String getNextCounter() {
        increaseNextCounter();
        return hasLastCounter;
    }
    public void setLastCounter(String hasLastCounter) {
        this.hasLastCounter = hasLastCounter;
    }
    private void increaseNextCounter() {
        long longCounter = Long.parseLong(hasLastCounter) + 1;
        hasLastCounter = String.valueOf(longCounter);

        // in triple store, delete existing counter
        String query = "";

        String socUri = "";
        if (this.getUri().startsWith("<")) {
            socUri = this.getUri();
        } else {
            socUri = "<" + this.getUri() + ">";
        }

        query += NameSpaces.getInstance().printSparqlNameSpaceList();
        query += DELETE_LINE1;
        if (getNamedGraph() != null && !getNamedGraph().isEmpty()) {
            query += "GRAPH <" + getNamedGraph() + "> { " + socUri + "  ";
        } else {
            query += "GRAPH <" + Constants.DEFAULT_REPOSITORY + "> { " + socUri + "  ";
        }
        query += DELETE_LINE4;
        query += LINE_LAST+LINE_LAST;

        UpdateRequest request = UpdateFactory.create(query);
        UpdateProcessor processor = UpdateExecutionFactory.createRemote(
                request, CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_UPDATE));
        processor.execute();

        // in triple store, add new counter 
        String insert = "";

        insert += NameSpaces.getInstance().printSparqlNameSpaceList();
        insert += INSERT_LINE1;

        if (!getNamedGraph().isEmpty()) {
            insert += " GRAPH <" + getNamedGraph() + "> { ";
        } else {
            insert += " GRAPH <" + Constants.DEFAULT_REPOSITORY + "> { ";
        }

        insert += socUri + " hasco:hasLastCounter  \"" + this.hasLastCounter + "\" . ";

        // CLOSING NAMEDGRAPH
        insert += " } ";

        insert += LINE_LAST;

        try {
            request = UpdateFactory.create(insert);
            processor = UpdateExecutionFactory.createRemote(
                    request, CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_UPDATE));
            processor.execute();
        } catch (QueryParseException e) {
            System.out.println("QueryParseException due to update query: " + insert);
            throw e;
        }

    }


    public boolean isDomainCollection() {
        if (typeUri == null || typeUri.equals("")) {
            return false;
        }
        return (typeUri.equals(HASCO.SUBJECT_COLLECTION) || typeUri.equals(HASCO.SAMPLE_COLLECTION));
    }

    public boolean isLocationCollection() {
        if (typeUri == null || typeUri.equals("")) {
            return false;
        }
        return typeUri.equals(HASCO.LOCATION_COLLECTION);
    }

    public boolean isTimeCollection() {
        if (typeUri == null || typeUri.equals("")) {
            return false;
        }
        return typeUri.equals(HASCO.TIME_COLLECTION);
    }

    @JsonIgnore
    public List<String> getObjectUris() {
        return objectUris;
    }

    public String getUriFromOriginalId(String originalId) {
        if (originalId == null || originalId.equals("")) {
            return "";
        }
        for (StudyObject obj : this.getObjects()) {
            if (originalId.equals(obj.getOriginalId())) {
                return obj.getUri();
            }
        }
        return "";
    }

    @JsonIgnore
    public List<StudyObject> getObjects() {
        List<StudyObject> resp = new ArrayList<StudyObject>();
        if (objectUris == null || objectUris.size() <=0) {
            return resp;
        }
        for (String uri : objectUris) {
            StudyObject obj = StudyObject.find(uri);
            if (obj != null) {
                resp.add(obj);
            }
        }
        return resp;
    }

    @JsonIgnore
    public Map<String, StudyObject> getObjectsMap() {
        Map<String, StudyObject> resp = new HashMap<String, StudyObject>();
        if (objectUris == null || objectUris.size() <=0) {
            return resp;
        }
        for (String uri : objectUris) {
            StudyObject obj = StudyObject.find(uri);
            if (obj != null) {
                resp.put(uri, obj);
            }
        }
        return resp;
    }
    public void setObjectUris(List<String> objectUris) {
        this.objectUris = objectUris;
    }

    public void setHasStatus(String hasStatus) {
        this.hasStatus = hasStatus;
    }
    public String getHasStatus() {
        return this.hasStatus;
    }

    public void setIsMemberOfUri(String isMemberOfUri) {
        this.isMemberOfUri = isMemberOfUri;
    }
    public String getIsMemberOfUri() {
        return isMemberOfUri;
    }
    public Study getIsMemberOf() {
        if (isMemberOfUri == null || isMemberOfUri.isEmpty()) {
            return null;
        }
        return Study.find(isMemberOfUri);
    }

    public void setVirtualColumnUri(String vcUri) {
        this.hasVirtualColumnUri = vcUri;
    }
    public String getVirtualColumnUri() {
        return hasVirtualColumnUri;
    }
    public VirtualColumn getVirtualColumn() {
        if (hasVirtualColumnUri == null || hasVirtualColumnUri.isEmpty()) {
            return null;
        }
        return VirtualColumn.find(hasVirtualColumnUri);
    }
    @JsonIgnore
    public String getSOCReference() {
        VirtualColumn vc = getVirtualColumn();
        if (vc == null) {
            return "";
        }
        return vc.getSOCReference();
    }
    @JsonIgnore
    public String getGroundingLabel() {
        VirtualColumn vc = getVirtualColumn();
        if (vc == null) {
            return "";
        }
        return vc.getGroundingLabel();
    }

    public void setRoleUri(String roleUri) {
        this.hasRoleUri = roleUri;
    }
    public String getRoleUri() {
        return this.hasRoleUri;
    }
    public StudyRole getRole() {
        if (hasRoleUri == null || hasRoleUri.isEmpty()) {
            return null;
        }
        return StudyRole.find(hasRoleUri);
    }
    @JsonIgnore
    public String getRoleLabel() {
        StudyRole role = getRole();
        if (role == null) {
            return "";
        }
        return role.getLabel();
    }
 

    public void setHasSIRManagerEmail(String hasSIRManagerEmail) {
        this.hasSIRManagerEmail = hasSIRManagerEmail;
    }
    public String getHasSIRManagerEmail() {
        return hasSIRManagerEmail;
    }

    public void setHasScopeUri(String hasScopeUri) {
        this.hasScopeUri = hasScopeUri;
    }
    public String getHasScopeUri() {
        return hasScopeUri;
    }
    public StudyObjectCollection getHasScope() {
        if (hasScopeUri == null || hasScopeUri.equals("")) {
            return null;
        }
        return StudyObjectCollection.find(hasScopeUri);
    }

    public void setSpaceScopeUris(List<String> spaceScopeUris) {
        this.spaceScopeUris = spaceScopeUris;
    }
    public List<String> getSpaceScopeUris() {
        return spaceScopeUris;
    }
    public List<StudyObjectCollection> getSpaceScopes() {
        if (spaceScopeUris == null || spaceScopeUris.isEmpty()) {
            return null;
        }
        List<StudyObjectCollection> spaceScopes = new ArrayList<StudyObjectCollection>();
        for (String scopeUri : spaceScopeUris) {
            StudyObjectCollection oc = StudyObjectCollection.find(scopeUri);
            if (oc != null) {
                spaceScopes.add(oc);
            }
        }
        return spaceScopes;
    }

    public void setTimeScopeUris(List<String> timeScopeUris) {
        this.timeScopeUris = timeScopeUris;
    }
    public List<String> getTimeScopeUris() {
        return timeScopeUris;
    }
    public List<StudyObjectCollection> getTimeScopes() {
        if (timeScopeUris == null || timeScopeUris.equals("")) {
            return null;
        }
        List<StudyObjectCollection> timeScopes = new ArrayList<StudyObjectCollection>();
        for (String scopeUri : timeScopeUris) {
            StudyObjectCollection oc = StudyObjectCollection.find(scopeUri);
            if (oc != null) {
                timeScopes.add(oc);
            }
        }
        return timeScopes;
    }

    public List<String> getGroupUris() {
        return groupUris;
    }
    public void setGroupUris(List<String> groupUris) {
        this.groupUris = groupUris;
    }
    public List<SOCGroup> getGroups() {
        if (groupUris == null || groupUris.equals("")) {
            return null;
        }
        List<SOCGroup> groups = new ArrayList<SOCGroup>();
        for (String grpUri : groupUris) {
            SOCGroup grp = SOCGroup.find(grpUri);
            if (grp != null) {
                groups.add(grp);
            }
        }
        return groups;
    }

    public long getNumOfObjects() {
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT (COUNT(?obj) AS ?count) WHERE { \n" 
                + " ?obj hasco:isMemberOf <" + getUri() + "> . \n" 
                + "} \n";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        if (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            return soln.getLiteral("count").getLong();
        }

        return 0;
    }

    public boolean isConnected(StudyObjectCollection soc) {

        // Check if oc is valid
        if (soc.getUri() == null || soc.getUri().equals("")) {
            return false;
        }

        // Check if soc is in scope of current object collection
        if (this.hasScopeUri != null && !this.hasScopeUri.equals("")) {
            StudyObjectCollection domainScope = StudyObjectCollection.find(this.hasScopeUri);
            if (soc.equals(domainScope)) {
                return true;
            }
        }
        if (this.getTimeScopes() != null && this.getTimeScopes().size() > 0) {
            List<StudyObjectCollection> timeScopes = this.getTimeScopes();
            if (timeScopes.contains(soc)) {
                return true;
            }
        }

        // Check if current is in scope of soc
        if (soc.getHasScopeUri() != null && !soc.getHasScopeUri().equals("")) {
            StudyObjectCollection socDomainScope = StudyObjectCollection.find(soc.hasScopeUri);
            if (this.equals(socDomainScope)) {
                return true;
            }
        }
        if (soc.getTimeScopes() != null && soc.getTimeScopes().size() > 0) {
            List<StudyObjectCollection> socTimeScopes = soc.getTimeScopes();
            if (socTimeScopes.contains(this)) {
                return true;
            }
        }

        // otherwise there is no connection
        return false;
    }

    public boolean inUriList(List<String> selected) {
        String uriAdjusted = uri.replace("<","").replace(">","");
        for (String str : selected) {
            if (uriAdjusted.equals(str)) {
                return true;
            }
        }
        return false;
    }

    public int getCollectionSize(){
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                "SELECT (count(*) as ?count) WHERE { " + 
                "   ?uri hasco:isMemberOf  <" + this.getUri() + "> . " +
                " } ";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        int count = 0;
        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            if (soln != null && soln.getLiteral("count") != null) {
                count += soln.getLiteral("count").getInt();
            }
            else {
                System.out.println("[StudyObjectCollection] getCollectionSize(): Error!");
                return -1;
            }
        }
        return count;
    }// /getCollectionSize()

    private static List<String> retrieveSpaceScope(String socUri) {
        List<String> scopeUris = new ArrayList<String>();
        String scopeUri = ""; 
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                "SELECT ?spaceScopeUri WHERE { \n" + 
                " <" + socUri + "> hasco:hasSpaceScope ?spaceScopeUri . \n" + 
                "}";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            if (soln != null) {
                try {
                    if (soln.getResource("spaceScopeUri") != null && soln.getResource("spaceScopeUri").getURI() != null) {
                        scopeUri = soln.getResource("spaceScopeUri").getURI();
                        if (scopeUri != null && !scopeUri.equals("")) {
                            scopeUris.add(scopeUri);
                        }
                    }
                } catch (Exception e1) {
                }
            }
        }

        return scopeUris;
    }

    private static List<String> retrieveTimeScope(String socUri) {
        List<String> scopeUris = new ArrayList<String>();
        String scopeUri = "";
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                "SELECT  ?timeScopeUri WHERE { " + 
                " <" + socUri + "> hasco:hasTimeScope ?timeScopeUri . " + 
                "}";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            if (soln != null) {
                try {
                    if (soln.getResource("timeScopeUri") != null && soln.getResource("timeScopeUri").getURI() != null) {
                        scopeUri = soln.getResource("timeScopeUri").getURI();
                        if (scopeUri != null && !scopeUri.equals("")) {
                            scopeUris.add(scopeUri);
                        }
                    }
                } catch (Exception e1) {
                }
            }
        }

        return scopeUris;
    }

    private static List<String> retrieveGroup(String socUri) {
        List<String> groupUris = new ArrayList<String>();
        String groupUri = "";
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                "SELECT  ?groupUri WHERE { " + 
                " <" + socUri + "> hasco:hasGroup ?groupUri . " + 
                "}";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            if (soln != null) {
                try {
                    if (soln.getResource("groupUri") != null && soln.getResource("groupUri").getURI() != null) {
                        groupUri = soln.getResource("groupUri").getURI();
                        if (groupUri != null && !groupUri.equals("")) {
                            groupUris.add(groupUri);
                        }
                    }
                } catch (Exception e1) {
                }
            }
        }

        return groupUris;
    }

    public static List<StudyObjectCollection> findMatchingScopeCollections(String socUri) {

    	List<StudyObjectCollection> matchingSOCs = new ArrayList<StudyObjectCollection>(); 
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                "SELECT ?matchingUri WHERE { \n" + 
                "  ?matchingUri hasco:hasScope <" + socUri + "> . \n" + 
                "  ?matchingUri a <" + HASCO.MATCHING_COLLECTION + "> . \n" + 
                "}";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            if (soln != null) {
                try {
                    if (soln.getResource("matchingUri") != null && soln.getResource("matchingUri").getURI() != null) {
                        String matchingUri = soln.getResource("matchingUri").getURI();
                        if (matchingUri != null && !matchingUri.equals("")) {
                            matchingSOCs.add(StudyObjectCollection.find(matchingUri));
                        }
                    }
                } catch (Exception e1) {
                }
            }
        }

        return matchingSOCs;
    }

    public static StudyObjectCollection find(String uri) {
        StudyObjectCollection soc = null;
        Statement statement;
        RDFNode object;

        String queryString = "DESCRIBE <" + uri + ">";
        Model model = SPARQLUtils.describe(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.SPARQL_QUERY), queryString);

        StmtIterator stmtIterator = model.listStatements();

        if (!stmtIterator.hasNext()) {
            return null;
        }

        soc = new StudyObjectCollection();

        while (stmtIterator.hasNext()) {
            statement = stmtIterator.next();
            object = statement.getObject();
            String str = URIUtils.objectRDFToString(object);
            if (statement.getPredicate().getURI().equals(RDFS.LABEL)) {
                soc.setLabel(str);
            } else if (statement.getPredicate().getURI().equals(RDF.TYPE)) {
                soc.setTypeUri(str);
            } else if (statement.getPredicate().getURI().equals(RDFS.COMMENT)) {
                soc.setComment(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HASCO_TYPE)) {
                soc.setHascoTypeUri(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_LAST_COUNTER)) {
                soc.setLastCounter(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_STATUS)) {
                soc.setHasStatus(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_ROLE)) {
                soc.setRoleUri(str);;
            } else if (statement.getPredicate().getURI().equals(HASCO.IS_MEMBER_OF)) {
                soc.setIsMemberOfUri(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_VIRTUAL_COLUMN)) {
                soc.setVirtualColumnUri(str);;
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_SCOPE)) {
                soc.setHasScopeUri(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_SIR_MANAGER_EMAIL)) {
                soc.setHasSIRManagerEmail(str);
            } 
        }

        soc.setTimeScopeUris(retrieveTimeScope(uri));

        soc.setSpaceScopeUris(retrieveSpaceScope(uri));

        soc.setGroupUris(retrieveGroup(uri));

        soc.setUri(uri);

        return soc;
    }

    /* 
    public static StudyObjectCollection find(String socUri) {
        StudyObjectCollection soc = null;

        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                "SELECT ?socType ?hascoType ?comment ?isMemberOf ?hasScopeUri ?hasRoleLabel ?hasSIRManagerEmail ?hasVirtualColumnUri ?spaceScopeUri ?timeScopeUri ?lastCounter WHERE { \n" + 
                "    <" + socUri + "> a ?socType . \n" + 
                "    <" + socUri + "> hasco:isMemberOf ?isMemberOf . \n" + 
                "    OPTIONAL { <" + socUri + "> hasco:hascoType ?hascoType } . \n" + 
                "    OPTIONAL { <" + socUri + "> rdfs:comment ?comment } . \n" + 
                "    OPTIONAL { <" + socUri + "> hasco:hasScope ?hasScopeUri } . \n" + 
                "    OPTIONAL { <" + socUri + "> hasco:hasVirtualColumn ?hasVirtualColumnUri } . \n" + 
                "    OPTIONAL { <" + socUri + "> hasco:hasRoleLabel ?hasRoleLabel } . \n" + 
                "    OPTIONAL { <" + socUri + "> vstoi:hasSIRManagerEmail ?hasSIRManagerEmail } . \n" + 
                "    OPTIONAL { <" + socUri + "> hasco:hasLastCounter ?lastCounter } . \n" + 
                "}";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        if (!resultsrw.hasNext()) {
            System.out.println("[WARNING] StudyObjectCollection. Could not find OC with URI: <" + socUri + ">");
            return soc;
        }

        String typeStr = "";
        String hascoTypeStr = "";
        String labelStr = "";
        String isMemberOfUriStr = "";
        String commentStr = "";
        String hasScopeUriStr = "";
        String hasVirtualColumnUriStr = "";
        String hasRoleLabelStr = "";
        String hasSIRManagerEmailStr = "";
        String lastCounterStr = "0";
        List<String> spaceScopeUrisStr = new ArrayList<String>();
        List<String> timeScopeUrisStr = new ArrayList<String>();
        List<String> groupUrisStr = new ArrayList<String>();

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            if (soln != null) {

                try {
                    if (soln.getResource("socType") != null && soln.getResource("socType").getURI() != null) {
                        typeStr = soln.getResource("socType").getURI();
                    }
                } catch (Exception e1) {
                    typeStr = "";
                }

                try {
                    if (soln.getResource("hascoType") != null && soln.getResource("hascoType").getURI() != null) {
                        hascoTypeStr = soln.getResource("hascoType").getURI();
                    }
                } catch (Exception e1) {
                    hascoTypeStr = "";
                }

                labelStr = FirstLabel.getLabel(socUri);

                try {
                    if (soln.getResource("isMemberOfUri") != null && soln.getResource("isMemberOfUri").getURI() != null) {
                        isMemberOfUriStr = soln.getResource("isMemberOfUri").getURI();
                    }
                } catch (Exception e1) {
                    isMemberOfUriStr = "";
                }

                try {
                    if (soln.getResource("hasVirtualColumnUri") != null && soln.getResource("hasVirtualColumnUri").getURI() != null) {
                        hasVirtualColumnUriStr = soln.getResource("hasVirtualColumnUri").getURI();
                    }
                } catch (Exception e1) {
                    hasVirtualColumnUriStr = "";
                }

                try {
                    if (soln.getLiteral("comment") != null && soln.getLiteral("comment").getString() != null) {
                        commentStr = soln.getLiteral("comment").getString();
                    }
                } catch (Exception e1) {
                    commentStr = "";
                }

                try {
                    if (soln.getResource("hasScopeUri") != null && soln.getResource("hasScopeUri").getURI() != null) {
                        hasScopeUriStr = soln.getResource("hasScopeUri").getURI();
                    }
                } catch (Exception e1) {
                    hasScopeUriStr = "";
                }

                try {
                    if (soln.getLiteral("hasRoleLabel") != null && soln.getLiteral("hasRoleLabel").getString() != null) {
                        hasRoleLabelStr = soln.getLiteral("hasRoleLabel").getString();
                    }
                } catch (Exception e1) {
                    hasRoleLabelStr = "";
                }

                try {
                    if (soln.getLiteral("hasSIRManagerEmail") != null && soln.getLiteral("hasSIRManagerEmail").getString() != null) {
                        hasSIRManagerEmailStr = soln.getLiteral("hasSIRManagerEmail").getString();
                    }
                } catch (Exception e1) {
                    hasSIRManagerEmailStr = "";
                }

                try {
                    if (soln.getLiteral("lastCounter") != null && soln.getLiteral("lastCounter").getString() != null) {
                        lastCounterStr = soln.getLiteral("lastCounter").getString();
                    }
                } catch (Exception e1) {
                    lastCounterStr = "";
                }

                spaceScopeUrisStr = retrieveSpaceScope(socUri);

                timeScopeUrisStr = retrieveTimeScope(socUri);

                groupUrisStr = retrieveGroup(socUri);

                soc = new StudyObjectCollection(
                        socUri, 
                        typeStr,
                        hascoTypeStr, 
                        labelStr, 
                        commentStr, 
                        isMemberOfUriStr,  
                        hasVirtualColumnUriStr, 
                        hasRoleLabelStr,
                        hasSIRManagerEmailStr, 
                        hasScopeUriStr, 
                        spaceScopeUrisStr, 
                        timeScopeUrisStr,
                        groupUrisStr,
                        lastCounterStr);
            }
        }

        // retrieve URIs of objects that are member of the collection
        String queryMemberStr = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                "SELECT  ?uriMember WHERE { \n" + 
                "    ?uriMember hasco:isMemberOf <" + socUri + "> . \n" + 
                "}";

        ResultSetRewindable resultsrwMember = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryMemberStr);

        if (resultsrwMember.hasNext()) {
            String uriMemberStr = "";

            while (resultsrwMember.hasNext()) {
                QuerySolution soln = resultsrwMember.next();
                if (soln != null) {
                    try {
                        if (soln.getResource("uriMember") != null && soln.getResource("uriMember").getURI() != null) {
                            uriMemberStr = soln.getResource("uriMember").getURI();
                            soc.getObjectUris().add(uriMemberStr);
                        }
                    } catch (Exception e1) {
                        uriMemberStr = "";
                    }
                }
            }
        }

        return soc;
    }
    */

    public static List<StudyObjectCollection> findDomainByStudyUri(String studyuri) {
        List<StudyObjectCollection> socList = new ArrayList<StudyObjectCollection>();
        for (StudyObjectCollection soc : StudyObjectCollection.findStudyObjectCollectionsByStudy(studyuri)) {
            if (soc.isDomainCollection()) {
                socList.add(soc);
            }
        }
        return socList;
    }

    public static List<StudyObjectCollection> findStudyObjectCollectionsByStudy(String studyuri, int pageSize, int offset) {
        if (studyuri == null || studyuri.isEmpty()) {
            return new ArrayList<StudyObjectCollection>();
        }
        String query = 
                "SELECT ?uri " +
                " WHERE {  ?uri hasco:isMemberOf  <" + studyuri + "> .  " +
				"          ?uri rdfs:label ?label . " +
                " } " +
                " ORDER BY ASC(?label) " +
                " LIMIT " + pageSize +
                " OFFSET " + offset;
        return StudyObjectCollection.findManyByQuery(query);
    }        

    public static int findTotalStudyObjectCollectionsByStudy(String studyuri) {
        if (studyuri == null || studyuri.isEmpty()) {
            return 0;
        }
        String query = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                " SELECT (count(?uri) as ?tot)  " +
                " WHERE {  ?uri hasco:isMemberOf <" + studyuri + "> .  " +
                " }";
        return GenericFind.findTotalByQuery(query);
    }        

    public static List<StudyObjectCollection> findStudyObjectCollectionsByStudy(String studyUri) {
        if (studyUri == null) {
            return null;
        }
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                "SELECT ?uri WHERE { \n" + 
                "   ?socType rdfs:subClassOf* hasco:StudyObjectCollection . \n" +
                "   ?uri a ?socType . \n" +
                "   ?uri hasco:isMemberOf <" + studyUri + "> . \n" +
                " } ";
        return findManyByQuery(queryString);
    }

    private static List<String> findStudyObjectCollectionUrisByStudy(String study_uri) {
        //System.out.println("findStudyObjectCollectionUris() is called");
        //System.out.println("study_uri: " + study_uri);
        List<String> socList = new ArrayList<String>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                "SELECT ?soc_uri  WHERE {  " +
                "      ?soc_uri hasco:isMemberOf " + study_uri + " . " +
                " } ";
        try {
            ResultSetRewindable resultsrw = SPARQLUtils.select(
                    CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

            while (resultsrw.hasNext()) {
                QuerySolution soln = resultsrw.next();
                if (soln.contains("soc_uri")) {
                    socList.add(soln.get("soc_uri").toString());
                    //System.out.println("STUDY: [" + study_uri + "]   OC: [" + soln.get("oc_uri").toString() + "]");
                }
            }
        } catch (QueryExceptionHTTP e) {
            e.printStackTrace();
        }
        return socList;
    }

    private static List<String> findDataAcquisitionUris(String study_uri) {
        List<String> daList = new ArrayList<String>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                "SELECT ?da_uri  WHERE {  " +
                "      ?da_uri hasco:isDataAcquisitionOf " + study_uri + " . " +
                " } ";
        try {
            ResultSetRewindable resultsrw = SPARQLUtils.select(
                    CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

            while (resultsrw.hasNext()) {
                QuerySolution soln = resultsrw.next();
                if (soln.contains("da_uri")) {
                    daList.add(soln.get("da_uri").toString());
                }
            }
        } catch (QueryExceptionHTTP e) {
            e.printStackTrace();
        }
        return daList;
    }

    public static List<StudyObjectCollection> findManyByQuery(String requestedQuery) {
        List<StudyObjectCollection> socs = new ArrayList<StudyObjectCollection>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + requestedQuery;

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            String uri = soln.getResource("uri").getURI();
            StudyObjectCollection soc = StudyObjectCollection.find(uri);
            socs.add(soc);
        }

        if (socs != null && !socs.contains(null) &&  socs.size() > 0) {
            java.util.Collections.sort((List<StudyObjectCollection>) socs);
        }
        return socs;
    }

    public static Map<String, String> labelsByStudyUri(String studyUri) {
        if (studyUri == null) {
            return null;
        }
        Map<String, String> labelsMap = new HashMap<String, String>();
        List<StudyObjectCollection> socList = findStudyObjectCollectionsByStudy(studyUri);

        for (StudyObjectCollection soc : socList) {
            if (soc.getGroundingLabel() != null && !soc.getGroundingLabel().equals("")) {
                labelsMap.put(soc.getSOCReference(), soc.getGroundingLabel());
            } else {
            }
        }

        return labelsMap;
    }

    public static String findByStudyUriJSON(String studyUri) {
        if (studyUri == null) {
            return null;
        }
        
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                "SELECT ?uri ?label WHERE { \n" + 
                "   ?ocType rdfs:subClassOf+ hasco:StudyObjectCollection . \n" +
                "   ?uri a ?ocType . \n" +
                "   ?uri hasco:isMemberOf <" + studyUri + "> . \n" +
                "   OPTIONAL { ?uri rdfs:label ?label } . \n" +
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

    private void saveObjectUris(String ssocUri) {
        if (objectUris == null || objectUris.size() == 0) {
            return;
        }

        String insert = "";

        insert += NameSpaces.getInstance().printSparqlNameSpaceList();
        insert += INSERT_LINE1;
        for (String uri : objectUris) {
            if (uri != null && !uri.equals("")) {
                if (uri.startsWith("http")) {
                    insert += "  <" + uri + "> hasco:isMemberOf  " + ssocUri + " . ";
                } else {
                    insert += "  " + uri + " hasco:isMemberOf  " + ssocUri + " . ";
                }
            }
        }
        insert += LINE_LAST;
        UpdateRequest request = UpdateFactory.create(insert);
        UpdateProcessor processor = UpdateExecutionFactory.createRemote(
                request, CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_UPDATE));
        processor.execute();
    }

    @Override
    public void save() {
        saveToTripleStore();
        return;
    }

    @Override
    public boolean saveToTripleStore() {
        String insert = "";

        String socUri = "";
        if (this.getUri().startsWith("<")) {
            socUri = this.getUri();
        } else {
            socUri = "<" + this.getUri() + ">";
        }

        insert += NameSpaces.getInstance().printSparqlNameSpaceList();
        insert += INSERT_LINE1;

        if (!getNamedGraph().isEmpty()) {
            insert += " GRAPH <" + getNamedGraph() + "> { ";
        } else {
            insert += " GRAPH <" + Constants.DEFAULT_REPOSITORY + "> { ";
        }

        insert += socUri + " a <" + typeUri + "> . ";
        insert += socUri + " hasco:hascoType <" + hascoTypeUri + "> . ";
        insert += socUri + " rdfs:label  \"" + this.getLabel() + "\" . ";
        if (this.getIsMemberOfUri().startsWith("http")) {
            insert += socUri + " hasco:isMemberOf  <" + this.getIsMemberOfUri() + "> . ";
        } else {
            insert += socUri + " hasco:isMemberOf  " + this.getIsMemberOfUri() + " . ";
        }
        if (this.getComment() != null && !this.getComment().equals("")) {
            insert += socUri + " rdfs:comment  \"" + this.getComment() + "\" . ";
        }
        if (this.getHasScopeUri() != null && !this.getHasScopeUri().equals("")) {
            if (this.getHasScopeUri().startsWith("http")) {
                insert += socUri + " hasco:hasScope  <" + this.getHasScopeUri() + "> . ";
            } else {
                insert += socUri + " hasco:hasScope  " + this.getHasScopeUri() + " . ";
            }
        }
        if (this.getVirtualColumnUri() != null && !this.getVirtualColumnUri().equals("")) {
            insert += socUri + " hasco:hasVirtualColumn  <" + this.getVirtualColumnUri() + "> . ";
        }
        if (this.getRoleUri() != null && !this.getRoleUri().equals("")) {
            insert += socUri + " hasco:hasRole  <" + this.getRoleUri() + "> . ";
        }
        if (this.getHasSIRManagerEmail() != null && !this.getHasSIRManagerEmail().equals("")) {
            insert += socUri + " vstoi:hasSIRManagerEmail  \"" + this.getHasSIRManagerEmail() + "\" . ";
        }
        insert += socUri + " hasco:hasLastCounter  \"" + this.hasLastCounter + "\" . ";
        if (this.getSpaceScopeUris() != null && this.getSpaceScopeUris().size() > 0) {
            for (String spaceScope : this.getSpaceScopeUris()) {
                if (spaceScope != null && !spaceScope.isEmpty()){
                    if (spaceScope.startsWith("http")) {
                        insert += socUri + " hasco:hasSpaceScope  <" + spaceScope + "> . ";
                    } else {
                        insert += socUri + " hasco:hasSpaceScope  " + spaceScope + " . ";
                    }
                }
            }
        }
        if (this.getTimeScopeUris() != null && this.getTimeScopeUris().size() > 0) {
            for (String timeScope : this.getTimeScopeUris()) {
                if (timeScope != null && !timeScope.isEmpty()){
                    if (timeScope.startsWith("http")) {
                        insert += socUri + " hasco:hasTimeScope  <" + timeScope + "> . ";
                    } else {
                        insert += socUri + " hasco:hasTimeScope  " + timeScope + " . ";
                        //System.out.println(socUri + " hasco:hasTimeScope  " + timeScope + " . ");
                    }
                }
            }
        }
        if (this.getGroupUris() != null && this.getGroupUris().size() > 0) {
            for (String group : this.getGroupUris()) {
                if (group.length() > 0){
                    if (group.startsWith("http")) {
                        insert += socUri + " hasco:hasGroup  <" + group + "> . ";
                    } else {
                        insert += socUri + " hasco:hasGroup  " + group + " . ";
                        //System.out.println(socUri + " hasco:hasGroup  " + group + " . ");
                    }
                }
            }
        }

        // NAMEDGRAPH CLOSING
        insert += " } ";

        insert += LINE_LAST;

        //System.out.println("save to TS: insert = " + insert);

        try {
            UpdateRequest request = UpdateFactory.create(insert);
            UpdateProcessor processor = UpdateExecutionFactory.createRemote(
                    request, CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_UPDATE));
            processor.execute();
        } catch (QueryParseException e) {
            System.out.println("QueryParseException due to update query: " + insert);
            throw e;
        }

        //saveObjectUris(socUri);

        return true;
    }
 
    public void saveRoleLabel(String label) {
        if (uri == null || uri.isEmpty()) {
            return;
        }
        if (hasRoleUri == null || hasRoleUri.isEmpty()) {
            return;
        }

        StudyRole role = StudyRole.find(hasRoleUri);
        role.setLabel(label);
        if(role.getNamedGraph().isEmpty()){
            role.setNamedGraph(Constants.DEFAULT_REPOSITORY);
        } 
        role.save();
    }

    @Override
    public void delete() {
        deleteFromTripleStore();
    }

    @Override
    public void deleteFromTripleStore() {
        if(getNamedGraph().isEmpty()){
            setNamedGraph(Constants.DEFAULT_REPOSITORY);
        } 
        List<StudyObject> listObj = getObjects();
        int totObj = listObj.size();
        if (listObj.size() > 0) {
        	for (StudyObject so : listObj) {
        		so.deleteFromTripleStore();
        	}
        }
    	super.deleteFromTripleStore();
    	System.out.println("StudyObjectCollection: deleted SOC " + this.getLabel() + " and its " + totObj + " objects.");
    }

    public static String computeRouteLabel (StudyObjectCollection soc, List<StudyObjectCollection> studySOCs) {
        if (soc.getGroundingLabel() != null && !soc.getGroundingLabel().equals("")) {
            return soc.getGroundingLabel();
        } else {
            List<StudyObjectCollection> socList = new ArrayList<StudyObjectCollection>();
            List<StudyObjectCollection> allList = new ArrayList<StudyObjectCollection>();
            List<StudyObjectCollection> inspectedList = new ArrayList<StudyObjectCollection>();
            socList.add(soc);
            for (StudyObjectCollection receivedSOC : studySOCs) {
                if (!receivedSOC.equals(soc)) {
                    allList.add(receivedSOC);
                    inspectedList.add(receivedSOC);
                }
            } 
            return traverseRouteLabel(socList, allList, inspectedList);
        }
    }

    private static String traverseRouteLabel(List<StudyObjectCollection> path, List<StudyObjectCollection> inspectedList, List<StudyObjectCollection> allList) {
        //System.out.println("Path " + path);
        //System.out.println("StudyOCs " + inspectedList);
        for (StudyObjectCollection soc : inspectedList) {
            //System.out.println("    - soc " + soc.getUri());
            //System.out.println("    - current.domain " + path.get(path.size() - 1).getHasScopeUri());
            //System.out.println("    - current.time " + path.get(path.size() - 1).getTimeScopes());
            //System.out.println("    - soc.domain " + soc.getHasScopeUri());
            //System.out.println("    - soc.time " + soc.getTimeScopes());
            if (path.get(path.size() - 1).isConnected(soc)) {
                //System.out.println(soc.getUri() + " is connected to " + path.get(path.size() - 1).getUri());
                if (soc.getGroundingLabel() != null && !soc.getGroundingLabel().equals("")) {
                    String finalLabel = soc.getGroundingLabel();
                    for (int i = path.size() - 1; i >= 0; i--) {
                        finalLabel = finalLabel + " " + path.get(i).getLabel();
                    }
                    //System.out.println(" final label ==> <" + finalLabel + ">");
                    return finalLabel;
                } else {
                    path.add(soc);
                    List<StudyObjectCollection> newList = new ArrayList<StudyObjectCollection>();
                    for (StudyObjectCollection socFromAllList : allList) {
                        if (!path.contains(socFromAllList)) {
                            newList.add(socFromAllList);
                        }
                    }
                    return traverseRouteLabel(path, newList, allList);
                }
            } else {
                //System.out.println("next iteration of traverseRouteLabel");
                inspectedList.remove(soc);
                return traverseRouteLabel(path, inspectedList, allList);
            }
        }
        System.out.println("Could not find path for " + path.get(0).getSOCReference());
        return null;
    }

    public String toString() {
        return this.getUri();
    } 

}
