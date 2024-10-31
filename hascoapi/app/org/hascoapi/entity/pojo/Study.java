package org.hascoapi.entity.pojo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.hascoapi.annotations.PropertyField;
import org.hascoapi.annotations.PropertyValueType;
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.ConfigProp;
import org.hascoapi.utils.NameSpaces;
import org.hascoapi.utils.SPARQLUtils;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.vocabularies.RDF;
import org.hascoapi.vocabularies.RDFS;
import org.hascoapi.vocabularies.VSTOI;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonFilter("studyFilter")
public class Study extends HADatAcThing {

    private static final Logger log = LoggerFactory.getLogger(Study.class);

    private static String className = "hasco:Study";

    private static final String kbPrefix = ConfigProp.getKbPrefix();

    public static String INSERT_LINE1 = "INSERT DATA {  ";
    public static String DELETE_LINE1 = "DELETE WHERE {  ";
    public static String DELETE_LINE3 = " ?p ?o . ";
    public static String LINE_LAST = "}  ";
    public static String PREFIX = "DSG-";

    @PropertyField(uri="hasco:hasId")
    private String id;

    @PropertyField(uri = "vstoi:hasStatus")
    private String hasStatus;

    @PropertyField(uri = "hasco:hasDataFile")
    private String hasDataFileUri;

    @PropertyField(uri="hasco:hasImage")
    private String image;

    @PropertyField(uri="hasco:hasTitle")
    private String title;

    @PropertyField(uri="hasco:hasProject")
    private String project;

    @PropertyField(uri="hasco:hasExternalSource")
    private String externalSource;

    @PropertyField(uri="hasco:hasInstitution", valueType=PropertyValueType.URI)
    private String institutionUri;

    @PropertyField(uri="hasco:hasPI", valueType=PropertyValueType.URI)
    private String piUri;

    //@PropertyField(uri="hasco:hasLastId")
    //private String lastId;

    @PropertyField(uri="vstoi:hasSIRManagerEmail")
    private String hasSIRManagerEmail;

    private DateTime startedAt;

    private DateTime endedAt;

    private List<String> dataAcquisitionUris;

    private List<String> objectCollectionUris;

    //private Person pi;

    //private Organization institution;

    public Study(String id,
                 String uri,
                 String studyType,
                 String label,
                 String title,
                 String project,
                 String comment,
                 String externalSource,
                 String institutionUri,
                 String piUri,
                 String startDateTime,
                 String endDateTime) {
        this.id = id;
        this.uri = uri;
        this.typeUri = studyType;
        this.label = label;
        this.title = title;
        this.project = project;
        this.comment = comment;
        this.externalSource = externalSource;
        this.institutionUri = institutionUri;
        this.piUri = piUri;
        this.setStartedAt(startDateTime);
        this.setEndedAt(endDateTime);
        this.dataAcquisitionUris = new ArrayList<String>();
        this.objectCollectionUris = new ArrayList<String>();
        //this.lastId= "0";
    }

    public Study() {
        this.id = "";
        this.uri = "";
        this.label = "";
        this.title = "";
        this.project = "";
        this.comment = "";
        this.externalSource = "";
        this.institutionUri = "";
        this.piUri = "";
        this.setStartedAt("");
        this.setEndedAt("");
        this.dataAcquisitionUris = new ArrayList<String>();
        this.objectCollectionUris = new ArrayList<String>();
        //this.lastId = "0";
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getHasStatus() {
        return hasStatus;
    }
    public void setHasStatus(String hasStatus) {
        this.hasStatus = hasStatus;
    }

    public String getHasDataFileUri() {
        return hasDataFileUri;
    }
    public void setHasDataFileUri(String hasDataFileUri) {
        this.hasDataFileUri = hasDataFileUri;
    }
    public DataFile getHasDataFile() {
        if (this.hasDataFileUri == null) {
            return null;
        }
        return DataFile.find(this.hasDataFileUri);
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public String getUri() {
        return uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }

    public String getProject() {
        return project;
    }
    public void setProject(String project) {
        this.project = project;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getExternalSource() {
        return externalSource;
    }
    public void setExternalSource(String externalSource) {
        this.externalSource = externalSource;
    }

    public String getInstitutionUri() {
        return institutionUri;
    }
    public Organization getInstitution() {
        if (institutionUri == null || institutionUri.equals("")) {
            return null;
        }
        //if (institution != null && institution.getUri().equals(institutionUri)) {
        //    return institution;
        //}
        return Organization.find(institutionUri);
    }
    public void setInstitutionUri(String institutionUri) {
        this.institutionUri = institutionUri;
    }

    public String getPiUri() {
        return piUri;
    }
    public Person getPi() {
        if (piUri == null || piUri.equals("")) {
            return null;
        }
        //if (pi != null && pi.getUri().equals(piUri)) {
        //    return pi;
        //}
        return Person.find(piUri);
    }
    public void setPiUri(String piUri) {
        this.piUri = piUri;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getHasSIRManagerEmail() {
        return hasSIRManagerEmail;
    }
    public void setHasSIRManagerEmail(String hasSIRManagerEmail) {
        this.hasSIRManagerEmail = hasSIRManagerEmail;
    }

    public static int getNumberStudies() {
        String query = "";
        query += NameSpaces.getInstance().printSparqlNameSpaceList();
        query += " select (count(?study) as ?tot) where { " +
                " ?studyType rdfs:subClassOf* hasco:Study . " +
                " ?study a ?studyType . " +
                " }";

        //select ?obj ?collection ?objType where { ?obj hasco:isMemberOf ?collection . ?obj a ?objType . FILTER NOT EXISTS { ?objType rdfs:subClassOf* hasco:StudyObjectCollection . } }
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

    // get Start Time Methods
    public String getStartedAt() {
        DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
        return formatter.withZone(DateTimeZone.UTC).print(startedAt);
    }
    public String getStartedAtXsd() {
        DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis();
        return formatter.withZone(DateTimeZone.UTC).print(startedAt);
    }

    // get End Time Methods
    public String getEndedAt() {
        DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
        return formatter.withZone(DateTimeZone.UTC).print(endedAt);
    }
    public String getEndedAtXsd() {
        DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis();
        return formatter.withZone(DateTimeZone.UTC).print(endedAt);
    }

    // set Start Time Methods
    public void setStartedAt(String startedAt) {
        if (startedAt == null || startedAt.equals("")) {
            this.startedAt = null;
        } else {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss zzz yyyy");
            this.startedAt = formatter.parseDateTime(startedAt);
        }
    }

    public void setStartedAtXsd(String startedAt) {
        DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis();
        this.startedAt = formatter.parseDateTime(startedAt);
    }

    public void setStartedAtXsdWithMillis(String startedAt) {
        DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
        this.startedAt = formatter.parseDateTime(startedAt);
    }

    // set End Time Methods
    public void setEndedAt(String endedAt) {
        if (startedAt == null || startedAt.equals("")) {
            this.startedAt = null;
        } else {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss zzz yyyy");
            this.endedAt = formatter.parseDateTime(endedAt);
        }
    }

    public void setEndedAtXsd(String endedAt) {
        DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis();
        this.endedAt = formatter.parseDateTime(endedAt);
    }

    public void setEndedAtXsdWithMillis(String endedAt) {
        DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
        this.endedAt = formatter.parseDateTime(endedAt);
    }

    public void setDataAcquisitionUris(List<String> dataAcquisitionUris) {
        this.dataAcquisitionUris = dataAcquisitionUris;
    }

    public List<String> getDataAcquisitionUris() {
        return this.dataAcquisitionUris;
    }

    public void addDataAcquisitionUri(String da_uri) {
        this.dataAcquisitionUris.add(da_uri);
    }

    public void setStudyObjectCollectionUris(List<String> objectCollectionUris) {
        this.objectCollectionUris = objectCollectionUris;
    }

    public List<String>  getStudyObjectCollectionUris() {
        return this.objectCollectionUris;
    }

    public void addStudyObjectCollectionUri(String oc_uri) {
        this.objectCollectionUris.add(oc_uri);
    }

    @Override
    public boolean equals(Object o) {
        if((o instanceof Study) && (((Study)o).getUri().equals(this.getUri()))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getUri().hashCode();
    }

	public static Study find(String uri) {
        
        if (uri == null || uri.isEmpty()) {
            System.out.println("[ERROR] No valid URI provided to retrieve Study object: " + uri);
            return null;
        }

		//System.out.println("Study.java : in find(): uri = [" + uri + "]");
	    Study study = null;
	    Statement statement;
	    RDFNode object;
	    
	    String queryString = "DESCRIBE <" + uri + ">";
	    Model model = SPARQLUtils.describe(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.SPARQL_QUERY), queryString);
		
		StmtIterator stmtIterator = model.listStatements();

		if (!stmtIterator.hasNext()) {
			return null;
		} else {
			study = new Study();
		}
		
		while (stmtIterator.hasNext()) {
		    statement = stmtIterator.next();
		    object = statement.getObject();
			String str = URIUtils.objectRDFToString(object);
			if (uri != null && !uri.isEmpty()) {
				if (statement.getPredicate().getURI().equals(RDFS.LABEL)) {
					study.setLabel(str);
				} else if (statement.getPredicate().getURI().equals(HASCO.HAS_TITLE)) {
					study.setTitle(str); 
				} else if (statement.getPredicate().getURI().equals(RDF.TYPE)) {
					study.setTypeUri(str); 
				} else if (statement.getPredicate().getURI().equals(HASCO.HASCO_TYPE)) {
					study.setHascoTypeUri(str);
				} else if (statement.getPredicate().getURI().equals(VSTOI.HAS_STATUS)) {
					study.setHasStatus(str);
                } else if (statement.getPredicate().getURI().equals(HASCO.HAS_DATAFILE)) {
                    study.setHasDataFileUri(str);
				} else if (statement.getPredicate().getURI().equals(HASCO.HAS_IMAGE)) {
					study.setImage(str);
				} else if (statement.getPredicate().getURI().equals(RDFS.COMMENT)) {
					study.setComment(str);
				} else if (statement.getPredicate().getURI().equals(HASCO.HAS_PROJECT)) {
					study.setProject(str);
				} else if (statement.getPredicate().getURI().equals(HASCO.HAS_EXTERNAL_SOURCE)) {
					study.setExternalSource(str);
				} else if (statement.getPredicate().getURI().equals(HASCO.HAS_PI)) {
					study.setPiUri(str);
				} else if (statement.getPredicate().getURI().equals(HASCO.HAS_INSTITUTION)) {
					study.setInstitutionUri(str);
//				} else if (statement.getPredicate().getURI().equals(HASCO.HAS_LAST_ID)) {
//					study.setLastId(str);
				} else if (statement.getPredicate().getURI().equals(VSTOI.HAS_SIR_MANAGER_EMAIL)) {
					study.setHasSIRManagerEmail(str);
				}
			}
		}

		study.setUri(uri);
		
		return study;
	}

    /* 
    public Map<String, StudyObject> getObjectsMap() {
        Map<String, StudyObject> resp = new HashMap<String, StudyObject>();
        String queryString = "";
        queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                "SELECT ?objUri " +
                " WHERE {  ?objUri hasco:isMemberOf ?socUri . " +
                "          ?socUri hasco:isMemberOf <" + getUri() + "> . " +
                " }";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        StudyObject obj = null;
        while (resultsrw.hasNext()){
            QuerySolution soln = resultsrw.next();
            if (soln != null && soln.getResource("objUri").getURI()!= null) {
                obj = StudyObject.find(soln.get("objUri").toString());
                //System.out.println("StudyObject URI: " + soln.get("objUri").toString());
                if (obj != null) {
                    resp.put(obj.getUri(), obj);
                }
            }
        }

        return resp;
    }
    */

    /* 
    public Map<String, StudyObject> getObjectsMapInBatch() {
        Map<String, StudyObject> results = new HashMap<String, StudyObject>();

        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                "DESCRIBE ?objUri WHERE { \n" +
                "  ?objUri hasco:isMemberOf ?socUri . \n" +
                "  ?socUri hasco:isMemberOf <" + getUri() + "> . \n" +
                "}";

        ResultSetRewindable resultsrw = SPARQLUtils.describeAsRs(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);
        if (resultsrw.size() <1){
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                    "DESCRIBE ?objUri WHERE { \n" +
                    "  ?objUri hasco:isMemberOf ?socUri . \n" +
                    "  ?socUri hasco:isMemberOf <" + getUri() + "> . \n" +
                    "}";
            resultsrw = SPARQLUtils.describeAsRs(
                    CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);
        }

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            String uri = soln.get("subject").toString();
            if (soln != null && uri!= null) {
                if (!results.containsKey(uri)) {
                    StudyObject tmpObj = new StudyObject();
                    tmpObj.setUri(uri);
                    results.put(uri, tmpObj);
                }

                results.get(uri).fromQuerySolution(soln);
            }
        }

        //for (Map.Entry<String, StudyObject> entry : results.entrySet()) {
        //     System.out.println("getObjectsMapInBatch(): Key = " + entry.getKey() + ", Value = " + ((StudyObject)entry.getValue()).getUri());
        //}

        return results;
    }
    */

    public static List<StudyObjectCollection> findStudyObjectCollections(String uri, int pageSize, int offset) {
        if (uri == null || uri.isEmpty()) {
            return new ArrayList<StudyObjectCollection>();
        }
        String query = 
                "SELECT ?uri " +
                " WHERE {  ?uri hasco:isMemberOf  <" + uri + "> .  " +
				"          ?uri rdfs:label ?label . " +
                " } " +
                " ORDER BY ASC(?label) " +
                " LIMIT " + pageSize +
                " OFFSET " + offset;
        return StudyObjectCollection.findManyByQuery(query);
    }        

    public static int findTotalStudyObjectCollections(String uri) {
        if (uri == null || uri.isEmpty()) {
            return 0;
        }
        String query = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                " SELECT (count(?uri) as ?tot)  " +
                " WHERE {  ?uri hasco:isMemberOf <" + uri + "> .  " +
				"          ?uri hasco:hascoType <" + HASCO.STUDY_OBJECT_COLLECTION + "> . " +
                " }";
        return GenericFind.findTotalByQuery(query);
    }        
    
    public static int findTotalStudyDAs(String uri) {
        if (uri == null || uri.isEmpty()) {
            return 0;
        }
        String query = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                " SELECT (count(?uri) as ?tot)  " +
                " WHERE {  ?uri hasco:isMemberOf <" + uri + "> .  " +
				"          ?uri hasco:hascoType <" + HASCO.DATA_ACQUISITION + "> . " +
                " }";
        return GenericFind.findTotalByQuery(query);
    }        
    
    public static int findTotalStudyRoles(String uri) {
        if (uri == null || uri.isEmpty()) {
            return 0;
        }
        String query = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                " SELECT (count(?uri) as ?tot)  " +
                " WHERE {  ?uri hasco:isMemberOf <" + uri + "> .  " +
				"          ?uri hasco:hascoType <" + HASCO.STUDY_ROLE + "> . " +
                " }";
        return GenericFind.findTotalByQuery(query);
    }        
    
    private static List<String> findStudyObjectCollectionUris(String study_uri) {
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

    public static Study findById(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        String queryString = "";
        queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                "SELECT ?uri " +
                " WHERE {  ?subUri rdfs:subClassOf* hasco:Study . " +
                "          ?uri a ?subUri . " +
                "          ?uri hasco:hasId ?id .  " +
                "        FILTER (?id=\"" + id + "\"^^xsd:string)  . " +
                " }";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        String uri = null;
        Study study = null;
        if (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            if (soln != null && soln.get("uri") != null) {
                uri = soln.get("uri").toString();
            }
            study = Study.find(uri);
        }

        return study;
    }

    public static Study findByName(String studyName){
        if (studyName == null || studyName.equals("")) {
            System.out.println("[ERROR] No valid StudyName provided to retrieve Study object: " + studyName);
            return null;
        }
        Study returnStudy = new Study();
        String queryUri = URIUtils.replacePrefixEx(kbPrefix + "DSG-" + studyName);

        return find(queryUri);
    }

    public static List<String> findIds() {
        List<String> studies = new ArrayList<String>();
        String queryString = "";
        queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                "SELECT ?uri ?id " +
                " WHERE {  ?subUri rdfs:subClassOf* hasco:Study . " +
                "          ?uri a ?subUri . " +
                "          ?uri hasco:hasId ?id . " +
                " }";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        String studyId = null;
        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            if (soln != null && soln.get("id") != null) {
                studyId = soln.get("id").toString();
                //System.out.println("Study URI: " + soln.get("uri").toString());
            }
            studies.add(studyId);
        }

        return studies;
    }

    public int getTotalStudyDAs() {
        return findTotal(HASCO.DATA_ACQUISITION);
    }

    public int getTotalStudyRoles() {
        return findTotal(HASCO.STUDY_ROLE);
    }

    public int getTotalVirtualColumns() {
        return findTotal(HASCO.VIRTUAL_COLUMN);
    }

    public int getTotalStudyObjectCollections() {
        return findTotal(HASCO.STUDY_OBJECT_COLLECTION);
    }

    private int findTotal(String conceptUri) {
		String queryString = NameSpaces.getInstance().printSparqlNameSpaceList();
		queryString += " SELECT (count(?uri) as ?tot) WHERE { " +
				" ?uri hasco:hascoType <" + conceptUri + "> . " +
				" ?uri hasco:isMemberOf <" + uri + "> . " +
				"}";
        return GenericFind.findTotalByQuery(queryString);
	}

    @Override
    public void save() {
        saveToTripleStore();
    }

    @Override
    public void delete() {

        // Delete associated DAs and their measurements
        deleteMeasurements();
        deleteDataAcquisitions();

        // Delete associated SOCs
        for (String soc_uri : this.objectCollectionUris) {
            StudyObjectCollection soc = StudyObjectCollection.find(soc_uri);
            if (soc != null) {
                soc.deleteFromTripleStore();
            }
        }

        // Delete study itself
        deleteFromTripleStore();
    }

    @Override
    public void deleteFromTripleStore() {
        super.deleteFromTripleStore();
    }

    public int deleteDataAcquisitions() {
        // TO BE IMPLEMENTED - WAS SOLR BASED
        return -1;
    }

    public int deleteMeasurements() {
        // TO BE IMPLEMENTED - WAS SOLR BASED
        return -1;
    }

}

