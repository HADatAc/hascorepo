package org.hascoapi.entity.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFilter;
import org.apache.jena.query.QueryParseException;
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
import org.hascoapi.utils.SPARQLUtils;
import org.hascoapi.annotations.PropertyField;
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.NameSpaces;
import org.hascoapi.utils.State;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.vocabularies.PROV;
import org.hascoapi.vocabularies.RDF;
import org.hascoapi.vocabularies.RDFS;
import org.hascoapi.vocabularies.VSTOI;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

@JsonFilter("deploymentFilter")
public class Deployment extends HADatAcThing {

    @PropertyField(uri="vstoi:designedAtTime")
    private String designedAt;

    @PropertyField(uri="prov:startedAtTime")
    private String startedAt;

    @PropertyField(uri="prov:endedAtTime")
    private String endedAt;

    @PropertyField(uri="vstoi:isLegacy")
    private String isLegacy;

    @PropertyField(uri="vstoi:hasInstrumentInstance")
    private String instrumentInstanceUri;

    @PropertyField(uri="vstoi:hasPlatformInstance")
    private String platformInstanceUri;

    @PropertyField(uri="vstoi:hasDetectorInstance")
    private List<String> detectorInstanceUri;

    @PropertyField(uri = "vstoi:hasVersion")
    private String hasVersion;

    @PropertyField(uri = "hasco:canView")
    private List<String> canView;

    @PropertyField(uri = "hasco:canUpdate")
    private List<String> canUpdate;

    @PropertyField(uri = "vstoi:hasSIRManagerEmail")
    private String hasSIRManagerEmail;

    //
    //  CACHE
    //

    private static Map<String, Deployment> DPLCache;

    private static Map<String, Deployment> getCache() {
        if (DPLCache == null) {
            DPLCache = new HashMap<String, Deployment>(); 
        }
        return DPLCache;
    }

    public static void resetCache() {
        DPLCache = null;
    }

    //
    //  CONSTRUCT
    //

    public Deployment() {
        designedAt = null;
        startedAt = null;
        endedAt = null;
        instrumentInstanceUri = null;
        platformInstanceUri = null;
        isLegacy = "F";
        detectorInstanceUri = new ArrayList<String>();
        canUpdate = new ArrayList<String>();
        canView = new ArrayList<String>();
        Deployment.getCache();
    }

    //
    //  GETS/SETS
    //

    public String getIsLegacy() {
        return this.isLegacy;
    }
    public void setIsLegacy(String isLegacy) {
        if (isLegacy == null && 
            (!isLegacy.equals("T") || !isLegacy.equals("F"))) {
            System.out.println("[ERROR] Deployment.java: isLegacy needs to be 'T' or 'F'.");
        }
        this.isLegacy = isLegacy;
    }

    public String getDesignedAt() {
        return designedAt;
    }
    public void setDesignedAt(String designedAtString) {
        DateTimeFormatter formatterWithZone = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        DateTimeFormatter formatterWithoutZone = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        DateTimeFormatter formatterISO = ISODateTimeFormat.dateTime();
        DateTime designedAtRaw;
        try {
            designedAtRaw = DateTime.parse(designedAtString, formatterWithZone);
        } catch (IllegalArgumentException e) {
            try {
                designedAtRaw = DateTime.parse(designedAtString, formatterWithoutZone);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Date-time string is not in a valid format: " + designedAtString, ex);
            }
        }
        //System.out.println("setDesignedAtXsdWithMillis: " +  designedAtRaw.toString(formatterISO));
        this.designedAt = designedAtRaw.toString(formatterISO);
    }
    public void setDesignedAtXsd(DateTime designedAtRaw) {
        DateTimeFormatter formatterNoMillis = ISODateTimeFormat.dateTimeNoMillis();
        this.designedAt = designedAtRaw.toString(formatterNoMillis);
    }

    public String getStartedAt() {
        return startedAt;
    }
    public void setStartedAt(String startedAtString) {
        DateTimeFormatter formatterWithZone = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        DateTimeFormatter formatterWithoutZone = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        DateTimeFormatter formatterISO = ISODateTimeFormat.dateTime();
        DateTime startedAtRaw;
        try {
            startedAtRaw = DateTime.parse(startedAtString, formatterWithZone);
        } catch (IllegalArgumentException e) {
            try {
                startedAtRaw = DateTime.parse(startedAtString, formatterWithoutZone);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Date-time string is not in a valid format: " + startedAtString, ex);
            }
        }
        this.startedAt = startedAtRaw.toString(formatterISO);
    }
    public void setStartedAtXsd(DateTime startedAtRaw) {
        DateTimeFormatter formatterNoMillis = ISODateTimeFormat.dateTimeNoMillis();
        this.startedAt = startedAtRaw.toString(formatterNoMillis);
    }

    public String getEndedAt() {
        return endedAt;
    }
    public void setEndedAt(String endedAtString) {
        DateTimeFormatter formatterWithZone = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        DateTimeFormatter formatterWithoutZone = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        DateTimeFormatter formatterISO = ISODateTimeFormat.dateTime();
        DateTime endedAtRaw;
        try {
            endedAtRaw = DateTime.parse(endedAtString, formatterWithZone);
        } catch (IllegalArgumentException e) {
            try {
                endedAtRaw = DateTime.parse(endedAtString, formatterWithoutZone);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Date-time string is not in a valid format: " + endedAtString, ex);
            }
        }
        this.startedAt = endedAtRaw.toString(formatterISO);
    }
    public void setEndedAtXsd(DateTime endedAtRaw) {
        DateTimeFormatter formatterNoMillis = ISODateTimeFormat.dateTimeNoMillis();
        this.endedAt = endedAtRaw.toString(formatterNoMillis);
    }

    public String getInstrumentInstanceUri() {
        return instrumentInstanceUri;
    }
    public InstrumentInstance getInstrumentInstance() {
        if (instrumentInstanceUri == null || instrumentInstanceUri.isEmpty()) {
            return null;
        }
        return InstrumentInstance.find(instrumentInstanceUri);
    }
    public void setInstrumentInstanceUri(String instrumentInstanceUri) {
        this.instrumentInstanceUri = instrumentInstanceUri;
    }

    public String getPlatformInstanceUri() {
        return platformInstanceUri;
    }
    public PlatformInstance getPlatformInstance() {
        if (platformInstanceUri == null || platformInstanceUri.isEmpty()) {
            return null;
        }
        return PlatformInstance.find(platformInstanceUri);
    }
    public void setPlatformInstanceUri(String platformInstanceUri) {
        this.platformInstanceUri = platformInstanceUri;
    }

    public List<String> getDetectorInstanceUri() {
        return detectorInstanceUri;
    }
    public List<DetectorInstance> getDetectorInstance() {
        List<DetectorInstance> detectorInstances = new ArrayList<DetectorInstance>();
        if (detectorInstanceUri != null && detectorInstanceUri.size() > 0) {
            for (String detInstanceUri : detectorInstanceUri) {
                if (detInstanceUri != null) {
                    DetectorInstance detInstance = DetectorInstance.find(detInstanceUri);
                    if (detInstance != null) {
                        detectorInstances.add(detInstance);
                    }
                }
            }
        }
        return detectorInstances;
    }
    public void addDetectorInstanceUri(String detectorInstanceUri) {
        if (detectorInstanceUri != null) {
            if (!detectorInstanceUri.contains(detectorInstanceUri)) {
                this.detectorInstanceUri.add(detectorInstanceUri);
            }
        }
    }

    public List<String> getCanUpdate() {
        return canUpdate;
    }
    public void setCanUpdate(List<String> canUpdate) {
        this.canUpdate = canUpdate;
    }
    public void addCanUpdate(String canUpdateEmail) {
        if (canUpdate != null) {
            if (!canUpdate.contains(canUpdateEmail)) {
                this.canUpdate.add(canUpdateEmail);
            }
        }
    }

    public List<String> getCanView() {
        return canView;
    }
    public void setCanView(List<String> canView) {
        this.canView = canView;
    }
    public void addCanView(String canViewEmail) {
        if (canView != null) {
            if (!canView.contains(canViewEmail)) {
                this.canView.add(canViewEmail);
            }
        }
    }

    public String getHasVersion() {
        return hasVersion;
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

    public void close(String endedAt) {
        setEndedAt(endedAt);
        State activeState = new State(State.ACTIVE);
        List<Stream> list = Stream.findByStateDeployment(activeState,this.getUri());
        if (!list.isEmpty()) {
            for (Stream stream: list) {
                stream.close(endedAt);
            }
        }
        //saveEndedAtTime();
        save();
    }

    public static Deployment create(String uri) {
        Deployment deployment = new Deployment();
        deployment.setUri(uri);
        
        return deployment;
    }

    public static Deployment createLegacy(String uri) {
        Deployment deployment = new Deployment();
        deployment.setUri(uri);
        deployment.setIsLegacy("T");

        return deployment;
    }

    public static Deployment find(String deployment_uri) {
        if (Deployment.getCache().get(deployment_uri) != null) {
            return Deployment.getCache().get(deployment_uri);
        }

        //System.out.println("Current URI for FIND DEPLOYMENT: " + deployment_uri);

        Deployment deployment = null;
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList();
        if (deployment_uri.startsWith("http")) {
            queryString += "DESCRIBE <" + deployment_uri + ">";
        } else {
            queryString += "DESCRIBE " + deployment_uri;
        }
        // System.out.println("FIND DEPLOYMENT (queryString): " + queryString);
        
        Model model = SPARQLUtils.describe(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.SPARQL_QUERY), queryString);

        StmtIterator stmtIterator = model.listStatements();
        if (!model.isEmpty()) {
            deployment = new Deployment();
            deployment.setUri(deployment_uri);
        }

        while (stmtIterator.hasNext()) {
            Statement statement = stmtIterator.next();
            RDFNode object = statement.getObject();
            String str = URIUtils.objectRDFToString(object);
            //System.out.println("Str [" + str + "] Predicate [" + statement.getPredicate().getURI() + "]");
            if (statement.getPredicate().getURI().equals(RDFS.LABEL)) {
                deployment.setLabel(str);
            } else if (statement.getPredicate().getURI().equals(RDF.TYPE)) {
                deployment.setTypeUri(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HASCO_TYPE)) {
                deployment.setHascoTypeUri(str);
            } else if (statement.getPredicate().getURI().equals(RDFS.COMMENT)) {
                deployment.setComment(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_INSTRUMENT_INSTANCE)) {
                deployment.setInstrumentInstanceUri(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_PLATFORM_INSTANCE)) {
                deployment.setPlatformInstanceUri(str);;
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_DETECTOR_INSTANCE)) {
                deployment.addDetectorInstanceUri(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.DESIGNED_AT_TIME)) {
                deployment.setDesignedAt(str);
            } else if (statement.getPredicate().getURI().equals(PROV.STARTED_AT_TIME)) {
                deployment.setStartedAt(str);
            } else if (statement.getPredicate().getURI().equals(PROV.ENDED_AT_TIME)) {
                deployment.setEndedAt(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_VERSION)) {
                deployment.setHasVersion(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.CAN_UPDATE)) {
                deployment.addCanUpdate(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.CAN_VIEW)) {
                deployment.addCanView(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_SIR_MANAGER_EMAIL)) {
                deployment.setHasSIRManagerEmail(str);
            }
        }
        
        Deployment.getCache().put(deployment_uri, deployment);
        return deployment;
    }

    public static List<Deployment> find(State state) {
        String queryString = "";
        if (state.getCurrent() == State.DESIGN) { 
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
            		"SELECT ?uri WHERE { " + 
                    "   ?uri a vstoi:Deployment . " + 
                    "   FILTER NOT EXISTS { ?uri prov:startedAtTime ?startdatetime . } " + 
                    "   FILTER NOT EXISTS { ?uri prov:endedAtTime ?enddatetime . } " + 
                    "ORDER BY DESC(?datetime) ";
        } else if (state.getCurrent() == State.ACTIVE) {
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                    "SELECT ?uri WHERE { " + 
                    "   ?uri a vstoi:Deployment . " + 
                    "   ?uri prov:startedAtTime ?startdatetime .  " + 
                    "   FILTER NOT EXISTS { ?uri prov:endedAtTime ?enddatetime . } " + 
                    "} " +
                    "ORDER BY DESC(?datetime) ";
        } else if (state.getCurrent() == State.CLOSED) {
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                    "SELECT ?uri WHERE { " + 
                    "   ?uri a vstoi:Deployment . " + 
                    "   ?uri prov:startedAtTime ?startdatetime .  " + 
                    "   ?uri prov:endedAtTime ?enddatetime .  " + 
                    "} " +
                    "ORDER BY DESC(?datetime) ";
        } else if (state.getCurrent() == State.ALL) {
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                    "SELECT ?uri WHERE { " + 
                    "   ?uri a vstoi:Deployment . " + 
                    "} " +
                    "ORDER BY DESC(?datetime) ";
        } else {
            System.out.println("[ERROR] Deployment.java: no valid state specified.");
            return null;
        }
        return findManyByQuery(queryString);
    }

    public static List<Deployment> findWithPages(State state, int pageSize, int offset) {
        String queryString = "";
        if (state.getCurrent() == State.DESIGN) { 
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
            		"SELECT ?uri WHERE { " + 
                    "   ?uri a vstoi:Deployment . " + 
                    "   FILTER NOT EXISTS { ?uri prov:startedAtTime ?startdatetime . } " + 
                    "   FILTER NOT EXISTS { ?uri prov:endedAtTime ?enddatetime . } " + 
                    "} " + 
                    " ORDER BY DESC(?datetime) " +
            		" LIMIT " + pageSize + 
            		" OFFSET " + offset;
        } else if (state.getCurrent() == State.ACTIVE) { 
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                    "SELECT ?uri WHERE { " + 
                    "   ?uri a vstoi:Deployment . " + 
                    "   ?uri prov:startedAtTime ?startdatetime . " + 
                    "   FILTER NOT EXISTS { ?uri prov:endedAtTime ?enddatetime . } " + 
                    "} " + 
                    " ORDER BY DESC(?datetime) " +
                    " LIMIT " + pageSize + 
                    " OFFSET " + offset;
        } else if (state.getCurrent() == State.CLOSED) {
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                    "SELECT ?uri WHERE { " + 
                    "   ?uri a vstoi:Deployment . " + 
                    "   ?uri prov:startedAtTime ?startdatetime .  " + 
                    "   ?uri prov:endedAtTime ?enddatetime .  " + 
                    "} " +
                    " ORDER BY DESC(?datetime) " +
                    " LIMIT " + pageSize + 
                    " OFFSET " + offset;
        } else if (state.getCurrent() == State.ALL) {
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                    "SELECT ?uri WHERE { " + 
                    "   ?uri a vstoi:Deployment . " + 
                    "} " +
                    " ORDER BY DESC(?datetime) " +
                    " LIMIT " + pageSize + 
                    " OFFSET " + offset;
        } else {
            System.out.println("[ERROR] Deployment.java: no valid state specified.");
            return null;
        }
        return findManyByQuery(queryString);
    }

    public static List<Deployment> findCanUpdateWithPages(State state, String userEmail, int pageSize, int offset) {
        String queryString = "";
        if (state.getCurrent() == State.DESIGN) { 
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
            		"SELECT ?uri WHERE { " + 
                    "   ?uri a vstoi:Deployment . " + 
                    "   ?uri hasco:canUpdate ?userEmail . " +
                    //"   ?uri vstoi:designedAtTime ?datetime . " +
                    "   FILTER (?userEmail = \"" + userEmail + "\") " +
                    "   FILTER NOT EXISTS { ?uri prov:startedAtTime ?startdatetime . } " + 
                    "   FILTER NOT EXISTS { ?uri prov:endedAtTime ?enddatetime . } " + 
                    "} " + 
                    " ORDER BY DESC(?datetime) " +
            		" LIMIT " + pageSize + 
            		" OFFSET " + offset;
        } else if (state.getCurrent() == State.ACTIVE) { 
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                    "SELECT ?uri WHERE { " + 
                    "   ?uri a vstoi:Deployment . " + 
                    "   ?uri hasco:canUpdate ?userEmail . " +
                    "   ?uri prov:startedAtTime ?startedattime . " + 
                    "   FILTER (?userEmail = \"" + userEmail + "\") " +
                    "   FILTER NOT EXISTS { ?uri prov:endedAtTime ?enddatetime . } " + 
                    "} " + 
                    " ORDER BY DESC(?startedattime) " +
                    " LIMIT " + pageSize + 
                    " OFFSET " + offset;
        } else if (state.getCurrent() == State.CLOSED) {
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                    "SELECT ?uri WHERE { " + 
                    "   ?uri a vstoi:Deployment . " + 
                    "   ?uri hasco:canUpdate ?userEmail . " +
                    "   ?uri prov:startedAtTime ?startedattime .  " + 
                    "   ?uri prov:endedAtTime ?enddatetime .  " + 
                    "   FILTER (?userEmail = \"" + userEmail + "\") " +
                    "} " +
                    " ORDER BY DESC(?startedattime) " +
                    " LIMIT " + pageSize + 
                    " OFFSET " + offset;
        } else if (state.getCurrent() == State.ALL) {
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                    "SELECT ?uri WHERE { " + 
                    "   ?uri a vstoi:Deployment . " + 
                    "   ?uri hasco:canUpdate ?userEmail . " +
                    "   FILTER (?userEmail = \"" + userEmail + "\") " +
                    "} " +
                    " ORDER BY DESC(?datetime) " +
                    " LIMIT " + pageSize + 
                    " OFFSET " + offset;
        } else {
            System.out.println("[ERROR] Deployment.java: no valid state specified.");
            return null;
        }
        return findManyByQuery(queryString);
    }

    public static int findTotalCanUpdateWithPages(State state, String userEmail) {
        String queryString = "";
        if (state.getCurrent() == State.DESIGN) { 
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                "SELECT (count(?uri) as ?tot) WHERE { " + 
                "   ?uri a vstoi:Deployment . " + 
                "   ?uri hasco:canUpdate ?userEmail . " +
                "   FILTER (?userEmail = \"" + userEmail + "\") " +
                "   FILTER NOT EXISTS { ?uri prov:startedAtTime ?startdatetime . } " + 
                "   FILTER NOT EXISTS { ?uri prov:endedAtTime ?enddatetime . } " + 
                "} ";
        } else if (state.getCurrent() == State.ACTIVE) { 
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                "SELECT (count(?uri) as ?tot) WHERE { " + 
                "   ?uri a vstoi:Deployment . " + 
                "   ?uri hasco:canUpdate ?userEmail . " +
                "   ?uri prov:startedAtTime ?startdatetime . " + 
                "   FILTER (?userEmail = \"" + userEmail + "\") " +
                "   FILTER NOT EXISTS { ?uri prov:endedAtTime ?enddatetime . } " + 
                "} "; 
        } else if (state.getCurrent() == State.CLOSED) {
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                "SELECT (count(?uri) as ?tot) WHERE { " + 
                "   ?uri a vstoi:Deployment . " + 
                "   ?uri hasco:canUpdate ?userEmail . " +
                "   ?uri prov:startedAtTime ?startdatetime .  " + 
                "   ?uri prov:endedAtTime ?enddatetime .  " + 
                "   FILTER (?userEmail = \"" + userEmail + "\") " +
                "} ";
        } else if (state.getCurrent() == State.ALL) {
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                "SELECT (count(?uri) as ?tot) WHERE { " + 
                "   ?uri a vstoi:Deployment . " + 
                "   ?uri hasco:canUpdate ?userEmail . " +
                "   FILTER (?userEmail = \"" + userEmail + "\") " +
                "} ";
        } else {
            System.out.println("[ERROR] Deployment.java: no valid state specified.");
            return -1;
        }
        return GenericFind.findTotalByQuery(queryString);                
    }

    public static int getNumberDeployments(State state) {
        String query = "";
        if (state.getCurrent() == State.DESIGN) { 
            query = NameSpaces.getInstance().printSparqlNameSpaceList() +
            		"SELECT (count(?uri) as ?tot) WHERE { " + 
                    "   ?uri a vstoi:Deployment . " + 
                    "   FILTER NOT EXISTS { ?uri prov:startedAtTime ?startdatetime . } " + 
                    "   FILTER NOT EXISTS { ?uri prov:endedAtTime ?enddatetime . } " + 
                    "} "; 
        } else if (state.getCurrent() == State.ACTIVE) { 
            query = NameSpaces.getInstance().printSparqlNameSpaceList() +
            		"SELECT (count(?uri) as ?tot) WHERE { " + 
                    "   ?uri a vstoi:Deployment . " + 
                    "   ?uri prov:startedAtTime ?startdatetime . " + 
                    "   FILTER NOT EXISTS { ?uri prov:endedAtTime ?enddatetime . } " + 
                    "} "; 
        } else if (state.getCurrent() == State.CLOSED) {
            query = NameSpaces.getInstance().printSparqlNameSpaceList() +
                    "SELECT (count(?uri) as ?tot) WHERE { " + 
                    "   ?uri a vstoi:Deployment . " + 
                    "   ?uri prov:startedAtTime ?startdatetime .  " + 
                    "   ?uri prov:endedAtTime ?enddatetime .  " + 
                    "} ";
        } else if (state.getCurrent() == State.ALL) {
            query = NameSpaces.getInstance().printSparqlNameSpaceList() +
                    "SELECT (count(?uri) as ?tot) WHERE { " + 
                    "   ?uri a vstoi:Deployment . " + 
                    "} ";
        } else {
            System.out.println("[ERROR] Deployment.java: no valid state specified.");
            return -1;
        }
        return GenericFind.findTotalByQuery(query);
    }

    public static List<Deployment> findWithGeoReference() {
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                " ?platModel rdfs:subClassOf* vstoi:Platform . " + 
                " ?platInstance a ?platModel ." +
                " ?platInstance hasco:hasFirstCoordinate ?lat . " +
                " ?platInstance hasco:hasSecondCoordinate ?lon . " +
                " ?platInstance hasco:hasFirstCoordinateCharacteristic <" + Platform.LAT + "> . " +
                " ?platInstance hasco:hasSecondCoordinateCharacteristic <" + Platform.LONG + "> . " +
                " ?uri vstoi:hasPlatformInstance ?platInstance . " +
                "} ";

        return findManyByQuery(queryString);
    }
    
    public static List<Deployment> findByPlatformInstanceAndStatus(String plat_uri, State state) {
    	if (plat_uri == null) {
    		return null;
    	}
    	String p_uri = plat_uri;
    	if (plat_uri.startsWith("http")) {
    		p_uri = "<" + plat_uri + ">"; 
    	}
        String queryString = "";
        if (state.getCurrent() == State.DESIGN) { 
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
            		"SELECT ?uri WHERE { " + 
                    "   ?uri a vstoi:Deployment . " + 
                    "   ?uri vstoi:hasPlatformInstance " + p_uri + " . " + 
                    "   FILTER NOT EXISTS { ?uri prov:startedAtTime ?startdatetime . } " + 
                    "   FILTER NOT EXISTS { ?uri prov:endedAtTime ?enddatetime . } " + 
                    "} " + 
                    "ORDER BY DESC(?datetime) ";
        } else if (state.getCurrent() == State.ACTIVE) { 
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                    "SELECT ?uri WHERE { " + 
                    "   ?uri a vstoi:Deployment . " + 
                    "   ?uri vstoi:hasPlatforminstance " + p_uri + " . " + 
                    "   ?uri prov:startedAtTime ?startdatetime . " + 
                    "   FILTER NOT EXISTS { ?uri prov:endedAtTime ?enddatetime . } " + 
                    "} " + 
                    "ORDER BY DESC(?datetime) ";
        } else if (state.getCurrent() == State.CLOSED) {
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                    "SELECT ?uri WHERE { " + 
                    "   ?uri a vstoi:Deployment . " + 
                    "   ?uri vstoi:hasPlatforminstance " + p_uri + " . " + 
                    "   ?uri prov:startedAtTime ?startdatetime .  " + 
                    "   ?uri prov:endedAtTime ?enddatetime .  " + 
                    "} " +
                    "ORDER BY DESC(?datetime) ";
        } else if (state.getCurrent() == State.ALL) {
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                    "SELECT ?uri WHERE { " + 
                    "   ?uri a vstoi:Deployment . " + 
                    "   ?uri vstoi:hasPlatforminstance " + p_uri + " . " + 
                    "} " +
                    "ORDER BY DESC(?datetime) ";
        } else {
            System.out.println("[ERROR] Deployment.java: no valid state specified.");
            return null;
        }
        return findManyByQuery(queryString);
    }

    public static List<Deployment> findByReferenceLayoutAndStatus(String plat_uri, State state) {
    	if (plat_uri == null) {
    		return null;
    	}
    	String p_uri = plat_uri;
    	if (plat_uri.startsWith("http")) {
    		p_uri = "<" + plat_uri + ">"; 
    	}
        String queryString = "";
        if (state.getCurrent() == State.DESIGN) { 
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
            		"SELECT ?uri WHERE { " + 
                    "   ?uri a vstoi:Deployment . " + 
                    "   ?uri vstoi:hasPlatformInstance ?pltInstance . " + 
                    "   ?pltInstance hasco:hasReferenceLayout " + p_uri + "  . " + 
                    "   FILTER NOT EXISTS { ?uri prov:startedAtTime ?startdatetime . } " + 
                    "   FILTER NOT EXISTS { ?uri prov:endedAtTime ?enddatetime . } " + 
                    "} " + 
                    "ORDER BY DESC(?datetime) ";
        } else if (state.getCurrent() == State.ACTIVE) { 
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
            		"SELECT ?uri WHERE { " + 
                    "   ?uri a vstoi:Deployment . " + 
                    "   ?uri vstoi:hasPlatformInstance ?pltInstance . " + 
                    "   ?plt hasco:hasReferenceLayout " + p_uri + "  . " + 
                    "   ?uri prov:startedAtTime ?startdatetime . " + 
                    "   FILTER NOT EXISTS { ?uri prov:endedAtTime ?enddatetime . } " + 
                    "} " + 
                    "ORDER BY DESC(?datetime) ";
        } else if (state.getCurrent() == State.CLOSED) {
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                    "SELECT ?uri WHERE { " + 
                    "   ?uri a vstoi:Deployment . " + 
                    "   ?uri vstoi:hasPlatformInstance ?pltInstance . " + 
                    "   ?plt hasco:hasReferenceLayout " + p_uri + "  . " + 
                    "   ?uri prov:startedAtTime ?startdatetime .  " + 
                    "   ?uri prov:endedAtTime ?enddatetime .  " + 
                    "} " +
                    "ORDER BY DESC(?datetime) ";
        } else if (state.getCurrent() == State.ALL) {
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                    "SELECT ?uri WHERE { " + 
                    "   ?uri a vstoi:Deployment . " + 
                    "   ?uri vstoi:hasPlatformInstance ?pltInstance . " + 
                    "   ?plt hasco:hasReferenceLayout " + p_uri + "  . " + 
                    "} " +
                    "ORDER BY DESC(?datetime) ";
        } else {
            System.out.println("[ERROR] Deployment.java: no valid state specified.");
            return null;
        }
        return findManyByQuery(queryString);
    }

    public static List<Deployment> findByPlaformInstanceWithPage(String platforminstanceUri, int pageSize, int offset) {
        System.out.println("Deployment.findByPlatformInstanceWithPage: instanceUri=[" + platforminstanceUri + "]");
        if (platforminstanceUri == null || platforminstanceUri.isEmpty()) {
            return new ArrayList<Deployment>();
        }
        String query = NameSpaces.getInstance().printSparqlNameSpaceList() +
                "SELECT ?uri " +
                " WHERE {  ?uri vstoi:hasPlatformInstance <" + platforminstanceUri + "> .  " +
				"          ?uri hasco:hascoType vstoi:Deployment . " +
                " } " +
                " LIMIT " + pageSize +
                " OFFSET " + offset;
        return findManyByQuery(query);
    }        

    public static int findTotalByPlatformInstance(String platforminstanceUri) {
        if (platforminstanceUri == null || platforminstanceUri.isEmpty()) {
            return 0;
        }
        String query = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                " SELECT (count(?uri) as ?tot)  " +
                " WHERE {  ?uri vstoi:hasPlatformInstance <" + platforminstanceUri + "> .  " +
				"          ?uri hasco:hascoType vstoi:Deployment . " +
                " }";
        return GenericFind.findTotalByQuery(query);
    }     

    public static Deployment findOneByQuery(String query) {
        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), query);
        QuerySolution soln = resultsrw.next();
        String uri = soln.getResource("uri").getURI();
        return Deployment.find(uri);
    }

    public static List<Deployment> findManyByQuery(String query) {
        List<Deployment> deployments = new ArrayList<Deployment>();
        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), query);
        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            String uri = soln.getResource("uri").getURI();
            Deployment deployment = Deployment.find(uri);
            deployments.add(deployment);
        }
        //java.util.Collections.sort((List<Deployment>) deployments);
        return deployments;
    }

    //@Override
    //public int compareTo(Deployment another) {
    //    return this.getHasPosition().compareTo(another.getHasPosition());
    //}

    @Override
    public void save() {
        //System.out.println("Saving platform [" + uri + "]");
        saveToTripleStore();
        resetCache();
    }

    @Override
    public void delete() {
        deleteFromTripleStore();
        resetCache();
    }

}
