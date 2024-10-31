package org.hascoapi.entity.pojo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.text.WordUtils;
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
import org.hascoapi.utils.IngestionLogger;
import org.hascoapi.utils.NameSpaces;
import org.hascoapi.utils.SPARQLUtils;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.vocabularies.PROV;
import org.hascoapi.vocabularies.RDF;
import org.hascoapi.vocabularies.RDFS;
import org.hascoapi.vocabularies.SCHEMA;
import org.hascoapi.vocabularies.VSTOI;
import org.hascoapi.annotations.PropertyField;
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.State;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonFilter("streamFilter")
public class Stream extends HADatAcThing implements Comparable<Stream> {
    private static final String className = "hasco:Stream";

    /*
     *   GENERIC STREAM PROPERTIES
     */
    @PropertyField(uri="hasco:hasStudy")
    private String studyUri;
    @PropertyField(uri="hasco:hasSDD")
    private String sddUri;
    @PropertyField(uri="hasco:hasDeployment")
    private String deploymentUri;
    @PropertyField(uri="hasco:hasMethod")
    private String method;   // Possible values: 'message' or 'file'
    @PropertyField(uri="vstoi:designedAtTime")
    private String designedAt;
    @PropertyField(uri="prov:startedAtTime")
    private String startedAt;
    @PropertyField(uri="prov:endedAtTime")
    private String endedAt;
    @PropertyField(uri="vstoi:hasVersion")
    private String hasVersion;
    @PropertyField(uri="hasco:hasPermissionUri")
    private String permissionUri;
    @PropertyField(uri="hasco:hasParameter")
    private String parameter;
    @PropertyField(uri="hasco:hasTriggeringEvent")
    private int triggeringEvent;
    @PropertyField(uri="hasco:hasNumberDataPoints")
    private long numberDataPoints;

    /*
     *   FILE-SUPPORTING PROPERTIES
     */
    @PropertyField(uri="hasco:hasDatasetPattern")
    private String datasetPattern;
    @PropertyField(uri="hasco:hasDatasetURIs")
    private List<String> datasetURIs;
    @PropertyField(uri="hasco:hasCellScopeUri")
    private List<String> cellScopeUri;
    @PropertyField(uri="hasco:hasCellScopeName")
    private List<String> cellScopeName;


    /* 
     *   MESSAGE-SUPPORTING PROPERTIES
     */
    @PropertyField(uri="hasco:hasTotalMessages")
    private long totalMessages;
    @PropertyField(uri="hasco:hasIngestedMessages")
    private long ingestedMessages;
    @PropertyField(uri="hasco:hasMessageProtocol")
    private String messageProtocol;
    @PropertyField(uri="hasco:hasMessageIP")
    private String messageIP;
    @PropertyField(uri="hasco:hasMessagePort")
    private String messagePort;
    @PropertyField(uri="hasco:hasMessageHeader")
    private String messageHeaders;
    @PropertyField(uri="hasco:hasMessageArchiveID")
    private String messageArchiveId;


    /*
     * Possible values for message status:
     * ACTIVE:     It is not closed and it is collecting data
     * SUSPENDED:  It is not closed but it is not collecting data
     * CLOSED:     It is not collecting data. It is no longer available
     *             for data collection
     */
    @PropertyField(uri="hasco:hasMessageStatus")
    private String messageStatus;

    /*
     * 0 - DataAcquisition is a new one, its details on the preamble It should
     * not exist inside the KB Preamble must contain deployment link and
     * deployment must exists on the KB 1 - DataAcquisition already exists, only
     * a reference present on the preamble It should exist inside the KB as not
     * finished yet 2 - DataAcquisition already exists, the preamble states its
     * termination with endedAtTime information It should exist inside the KB as
     * not finished yet
     *
     * 9999 - Stream Specification is complete (anything else diferent
     * than 9999 is considered incomplete
     *
     */
    @PropertyField(uri="hasco:hasStatus")
    private int status;

    @PropertyField(uri = "hasco:canView")
    private List<String> canView;

    @PropertyField(uri = "hasco:canUpdate")
    private List<String> canUpdate;

	@PropertyField(uri="vstoi:hasSIRManagerEmail")
	private String hasSIRManagerEmail;

    private boolean isComplete;
    private String localName;
    private Map<String,MessageTopic> topicsMap;
    private List<String> headers;

    private DataFile archive = null;
    private String log;
    private IngestionLogger logger = null;

    public static final String ACTIVE = "ACTIVE";
    public static final String SUSPENDED = "SUSPENDED";
    public static final String CLOSED = "CLOSED";

    public static final String MQTT = "mqtt";
    public static final String HTTP = "http";

    public Stream() {
        startedAt = null;
        endedAt = null;
        numberDataPoints = 0;
        isComplete = false;
        datasetURIs = new ArrayList<String>();
        totalMessages = 0;
        ingestedMessages = 0;
        messageProtocol = null;
        messageIP = null;
        messagePort = null;
        topicsMap = null;
        headers = new ArrayList<String>();
        cellScopeUri = new ArrayList<String>();
        cellScopeName = new ArrayList<String>();
        canView = new ArrayList<String>();
        canUpdate = new ArrayList<String>();
        headers = new ArrayList<String>();
    }

    @Override
    public boolean equals(Object o) {
        if ((o instanceof Stream) && (((Stream) o).getUri().equals(this.getUri()))) {
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
    public int compareTo(Stream another) {
        if (this.getLabel() != null && another.getLabel() != null) {
            return this.getLabel().compareTo(another.getLabel());
        }
        return this.getUri().compareTo(another.getUri());
    }

    /*
    public String getUsedUri() {
        return used_uri;
    }

    public void setUsedUri(String used_uri) {
        this.used_uri = used_uri;
    }
	*/

    public String getDeploymentUri() {
        return deploymentUri;
    }
    public Deployment getDeployment() {
        if (deploymentUri == null || deploymentUri.equals("")) {
            return null;
        }
        return Deployment.find(deploymentUri);
    }

    public void setDeploymentUri(String deploymentUri) {
        this.deploymentUri = deploymentUri;
    }

    public String getStudyUri() {
        return studyUri;
    }
    public Study getStudy() {
        if (studyUri == null || studyUri.equals(""))
            return null;
        return Study.find(studyUri);
    }
    public void setStudyUri(String study_uri) {
        this.studyUri = study_uri;
    }

    public String getSDDUri() {
        return sddUri;
    }
    public SemanticDataDictionary getSDD() {
        if (sddUri == null || sddUri.equals("")) {
            return null;
        }
        SemanticDataDictionary sdd = SemanticDataDictionary.find(sddUri);
        headers = new ArrayList<String>();
        if (sdd != null && sdd.getAttributes() != null) {
            for (SDDAttribute attr : sdd.getAttributes()) {
                headers.add(attr.getLabel());
            }
        }
        setHeaders(headers.toString());
        return sdd;
    }
    public void setSDDUri(String sddUri) {
        this.sddUri = sddUri;
    }

    public String getHasVersion() {
        return hasVersion;
    }
    public void setHasVersion(String hasVersion) {
        this.hasVersion = hasVersion;
    }

    public String getPermissionUri() {
        return permissionUri;
    }

    public void setPermissionUri(String permissionUri) {
        this.permissionUri = permissionUri;
    }

    public boolean getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public int getTriggeringEvent() {
        return triggeringEvent;
    }

    public void setTriggeringEvent(int triggeringEvent) {
        this.triggeringEvent = triggeringEvent;
    }

    public String getTriggeringEventName() {
        switch (triggeringEvent) {
            case TriggeringEvent.INITIAL_DEPLOYMENT:
                return TriggeringEvent.INITIAL_DEPLOYMENT_NAME;
            case TriggeringEvent.LEGACY_DEPLOYMENT:
                return TriggeringEvent.LEGACY_DEPLOYMENT_NAME;
            case TriggeringEvent.CHANGED_CONFIGURATION:
                return TriggeringEvent.CHANGED_CONFIGURATION_NAME;
            case TriggeringEvent.CHANGED_OWNERSHIP:
                return TriggeringEvent.CHANGED_OWNERSHIP_NAME;
            case TriggeringEvent.AUTO_CALIBRATION:
                return TriggeringEvent.AUTO_CALIBRATION_NAME;
            case TriggeringEvent.SUSPEND_DATA_ACQUISITION:
                return TriggeringEvent.SUSPEND_DATA_ACQUISITION_NAME;
            case TriggeringEvent.RESUME_DATA_ACQUISITION:
                return TriggeringEvent.RESUME_DATA_ACQUISITION_NAME;
        }
        return "";
    }

    public int getTriggeringEventByName(String name) {
        switch (name) {
            case TriggeringEvent.INITIAL_DEPLOYMENT_NAME:
                return TriggeringEvent.INITIAL_DEPLOYMENT;
            case TriggeringEvent.LEGACY_DEPLOYMENT_NAME:
                return TriggeringEvent.LEGACY_DEPLOYMENT;
            case TriggeringEvent.CHANGED_CONFIGURATION_NAME:
                return TriggeringEvent.CHANGED_CONFIGURATION;
            case TriggeringEvent.CHANGED_OWNERSHIP_NAME:
                return TriggeringEvent.CHANGED_OWNERSHIP;
            case TriggeringEvent.AUTO_CALIBRATION_NAME:
                return TriggeringEvent.AUTO_CALIBRATION;
            case TriggeringEvent.SUSPEND_DATA_ACQUISITION_NAME:
                return TriggeringEvent.SUSPEND_DATA_ACQUISITION;
            case TriggeringEvent.RESUME_DATA_ACQUISITION_NAME:
                return TriggeringEvent.RESUME_DATA_ACQUISITION;
        }

        return -1;
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

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public long getNumberDataPoints() {
        return numberDataPoints;
    }

    public void setNumberDataPoints(long numberDataPoints) {
        this.numberDataPoints = numberDataPoints;
    }

    public long getTotalMessages() {
        return totalMessages;
    }
    public void setTotalMessages(long totalMessages) {
        this.totalMessages = totalMessages;
    }

    public long getIngestedMessages() {
        return totalMessages;
    }
    public void setIngestedMessages(long totalMessages) {
        this.totalMessages = totalMessages;
    }

    public String getMessageProtocol() {
        return messageProtocol;
    }
    public void setMessageProtocol(String messageProtocol) {
        this.messageProtocol = messageProtocol;
    }

    public String getMessageIP() {
        return messageIP;
    }
    public void setMessageIP(String messageIP) {
        this.messageIP = messageIP;
    }

    public String getMessagePort() {
        return messagePort;
    }
    public void setMessagePort(String messagePort) {
        this.messagePort = messagePort;
    }

    public String getMessageArchiveId() {
        return messageArchiveId;
    }
    public void setMessageArchiveId(String messageArchiveId) {
        this.messageArchiveId = messageArchiveId;
    }

    public String getMessageName() {
        if (label == null && label.isEmpty()) {
            return "";
        }
        if (messagePort == null || messagePort.isEmpty()) {
            return label + "_at_" + messageIP;
        }
        return label + "_at_" + messageIP + "_" + messagePort;
    }

    public String getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(String messageStatus) {
        this.messageStatus = messageStatus;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isComplete() {
        return status == 9999;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @JsonIgnore
    public String getLog() {
        return getMessageLogger().getLog();
    }
    public void setLog(String log) {
        getMessageLogger().setLog(log);
        this.log = log;
    }

    @JsonIgnore
    public IngestionLogger getMessageLogger() {
        return logger;
    }
    public void setMessageLogger(IngestionLogger logger) {
        this.logger = logger;
    }
    
    private void loadTopicsMap() {
        List<MessageTopic> topics = MessageTopic.findByStream(uri);
        if (topics != null) {
            topicsMap = new HashMap<String, MessageTopic>();
            for (MessageTopic topic : topics) {
                topic.cacheTopic();
                topicsMap.put(topic.getLabel(), topic);
            }
        }
    }

    public Map<String,MessageTopic> getTopicsMap() {
        if (topicsMap != null) {
            return topicsMap;
        }
        loadTopicsMap();
        return topicsMap;
    };

    public List<MessageTopic> getTopicsList() {
        if (topicsMap != null) {
            return new ArrayList<MessageTopic>(topicsMap.values());
        }
        loadTopicsMap();
        if (topicsMap != null) {
            return new ArrayList<MessageTopic>(topicsMap.values());
        }
        return new ArrayList<MessageTopic>();
    }

    public void resetTopicsMap() {
        topicsMap = null;
    }

    public List<String> getHeaders() {
        if (headers != null) {
            return headers;
        }
        List<String> headers = new ArrayList<String>();
        if (messageHeaders == null || messageHeaders.isEmpty()) {
            return headers;
        }
        String auxstr = messageHeaders.replace("[","").replace("]","");
        StringTokenizer str = new StringTokenizer(auxstr,",");
        while (str.hasMoreTokens()) {
            headers.add(str.nextToken().trim());
        }
        return headers;
    }

    private void setHeaders(String headersStr) {
        this.messageHeaders = headersStr;
        getHeaders();
    }

    public boolean hasScope() {
        return (hasCellScope());
    }

    public boolean hasCellScope() {
        if (cellScopeUri != null && cellScopeUri.size() > 0) {
            for (String tmpUri : cellScopeUri) {
                if (tmpUri != null && !tmpUri.equals("")) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<String> getCellScopeUri() {
        return cellScopeUri;
    }

    public void setCellScopeUri(List<String> cellScopeUri) {
        this.cellScopeUri = cellScopeUri;
        if (cellScopeUri == null || cellScopeUri.size() == 0) {
            return;
        }
        cellScopeName = new ArrayList<String>();
        for (String objUri : cellScopeUri) {
            StudyObject obj = StudyObject.find(objUri);
            if (obj != null && obj.getUri().equals(objUri)) {
                cellScopeName.add(obj.getLabel());
            } else {
                cellScopeName.add("");
            }
        }
    }

    public void addCellScopeUri(String cellScopeUri) {
        this.cellScopeUri.add(cellScopeUri);
    }

    public List<String> getCellScopeName() {
        return cellScopeName;
    }

    public void setCellScopeName(List<String> cellScopeName) {
        this.cellScopeName = cellScopeName;
    }

    public void addCellScopeName(String cellScopeName) {
        this.cellScopeName.add(cellScopeName);
    }

    public List<String> getDatasetUri() {
        return datasetURIs;
    }

    public void setDatasetUri(List<String> datasetURIs) {
        this.datasetURIs = datasetURIs;
    }

    public void addDatasetUri(String dataset_uri) {
        if (!datasetURIs.contains(dataset_uri)) {
            datasetURIs.add(dataset_uri);
        }
    }

    public void deleteDatasetUri(String dataset_uri) {
        Iterator<String> iter = datasetURIs.iterator();
        while (iter.hasNext()) {
            if (iter.next().equals(dataset_uri)) {
                iter.remove();
            }
        }
    }

    public void deleteAllDatasetURIs() {
        datasetURIs.clear();
    }

    public boolean containsDataset(String uri) {
        return datasetURIs.contains(uri);
    }

    public void addNumberDataPoints(long number) {
        numberDataPoints += number;
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

    public String getHasSIRManagerEmail() {
        return this.hasSIRManagerEmail;
    }
    public void setHASSIRManagerEmail(String hasSIRManagerEmail) {
        this.hasSIRManagerEmail = hasSIRManagerEmail;
    }

    @JsonIgnore
    public List<String> getDOIs() {
    	List<String> resp = new ArrayList<String>();
    	List<DataFile> dfs = DataFile.findByStream(uri);
    	//System.out.println("STR da's uri is [" + uri + "]  and dfs's size is [" + dfs.size() + "]");
    	for (DataFile df : dfs) {
        	//System.out.println("STR df's wasDerivedFrom size is [" + df.getWasDerivedFrom().size() + "]");
    		for (String doi : df.getWasDerivedFrom()) {
    			resp.add(doi);
    		}
    	}
    	return resp;
    }

    /* 
    public boolean isFinished() {
        if (endedAt == null) {
            return false;
        } else {
            return endedAt.isBeforeNow();
        }
    }
    */

 
    /* 
    @Override
    public int deleteFromSolr() {
        try {
            deleteMeasurementData();

            SolrClient solr = new HttpSolrClient.Builder(
                    CollectionUtil.getCollectionPath(CollectionUtil.Collection.DATA_COLLECTION)).build();
            UpdateResponse response = solr.deleteById(this.uri);
            solr.commit();
            solr.close();
            return response.getStatus();
        } catch (SolrServerException e) {
            System.out.println("[ERROR] STR.delete() - SolrServerException message: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("[ERROR] STR.delete() - IOException message: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[ERROR] STR.delete() - Exception message: " + e.getMessage());
        }

        return -1;
    }
    */

    private static String retrieveHASCOTypeUri(String uri) {
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?type WHERE { " +
                " <" + uri + "> hasco:hascoType ?type ." +
                "} ";
        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);
        if (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            return soln.getResource("type").getURI();
        }
		return null;
    }

    public static Stream find(String uri) {
        System.out.println("inside Stream.find(uri): " + uri);
		Stream str;
		String hascoTypeUri = retrieveHASCOTypeUri(uri);
		if (hascoTypeUri.equals(HASCO.STREAM)) {
			str = new Stream();
		} else {
			return null;
		}

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
            System.out.println("Property valuee: [" + string + "]");
			if (uri != null && !uri.isEmpty()) {
				if (statement.getPredicate().getURI().equals(RDFS.LABEL)) {
					str.setLabel(string);
				} else if (statement.getPredicate().getURI().equals(RDF.TYPE)) {
					str.setTypeUri(string); 
				} else if (statement.getPredicate().getURI().equals(HASCO.HASCO_TYPE)) {
					str.setHascoTypeUri(string); 
				} else if (statement.getPredicate().getURI().equals(HASCO.HAS_DEPLOYMENT)) {
					str.setDeploymentUri(string);
				} else if (statement.getPredicate().getURI().equals(HASCO.HAS_STUDY)) {
					str.setStudyUri(uri);
				} else if (statement.getPredicate().getURI().equals(HASCO.HAS_SDD)) {
					str.setSDDUri(uri);
				} else if (statement.getPredicate().getURI().equals(HASCO.HAS_METHOD)) {
					str.setMethod(string);
				} else if (statement.getPredicate().getURI().equals(RDFS.COMMENT)) {
					str.setComment(string);
                } else if (statement.getPredicate().getURI().equals(VSTOI.DESIGNED_AT_TIME)) {
                    str.setDesignedAt(string);
                } else if (statement.getPredicate().getURI().equals(PROV.STARTED_AT_TIME)) {
                    str.setStartedAt(string);
                } else if (statement.getPredicate().getURI().equals(PROV.ENDED_AT_TIME)) {
                    str.setEndedAt(string);
                } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_VERSION)) {
                    str.setHasVersion(string);
                } else if (statement.getPredicate().getURI().equals(HASCO.HAS_MESSSAGE_ARCHIVE_ID)) {
                    str.setMessageArchiveId(string);
                } else if (statement.getPredicate().getURI().equals(HASCO.HAS_MESSSAGE_IP)) {
                    str.setMessageIP(string);
                } else if (statement.getPredicate().getURI().equals(HASCO.HAS_MESSSAGE_PORT)) {
                    str.setMessagePort(string);
                } else if (statement.getPredicate().getURI().equals(HASCO.HAS_MESSSAGE_PROTOCOL)) {
                    str.setMessageProtocol(string);
                } else if (statement.getPredicate().getURI().equals(HASCO.CAN_UPDATE)) {
                    str.addCanUpdate(string);
                } else if (statement.getPredicate().getURI().equals(HASCO.CAN_VIEW)) {
                    str.addCanView(string);				
                } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_SIR_MANAGER_EMAIL)) {
					str.setHASSIRManagerEmail(string);
				}
			}
		}

        /* 
        @PropertyField(uri="hasco:hasStartedAtDate")
        private String startedAtString;
        @PropertyField(uri="hasco:hasEndedAtDate")
        private String endedAtString;
        @PropertyField(uri="hasco:hasOwnerUri")
        private String ownerUri;
        @PropertyField(uri="hasco:hasVersion")
        private String version;
        @PropertyField(uri="hasco:hasPermissionUri")
        private String permissionUri;
        @PropertyField(uri="hasco:hasParameter")
        private String parameter;
        @PropertyField(uri="hasco:hasTriggeringEvent")
        private int triggeringEvent;
        @PropertyField(uri="hasco:hasNumberDataPoints")
        private long numberDataPoints;
        @PropertyField(uri="hasco:hasTotalMessages")
        private long totalMessages;
        @PropertyField(uri="hasco:hasIngestedMessages")
        private long ingestedMessages;
        @PropertyField(uri="hasco:hasMessageProtocol")
        private String messageProtocol;
        @PropertyField(uri="hasco:hasMessageIP")
        private String messageIP;
        @PropertyField(uri="hasco:hasMessagePort")
        private String messagePort;
        @PropertyField(uri="hasco:hasMessageHeader")
        private String messageHeaders;
        @PropertyField(uri="hasco:hasMessageArchiveID")
        private String messageArchiveId;    
        @PropertyField(uri="hasco:hasStudyUri")
        private String studyUri;
        @PropertyField(uri="hasco:hasMethodUri")
        private String methodUri;
        @PropertyField(uri="hasco:hasSchemaUri")
        private String schemaUri;
        @PropertyField(uri="hasco:hasDeploymentUri")
        private String deploymentUri;
        @PropertyField(uri="hasco:hasInstrumentModel")
        private String instrumentModel;
        @PropertyField(uri="hasco:hasInstrumentUri")
        private String instrumentUri;
        @PropertyField(uri="hasco:hasPlatformName")
        private String platformName;
        @PropertyField(uri="hasco:hasPlatformUri")
        private String platformUri;
        @PropertyField(uri="hasco:hasLocation")
        private String location;
        @PropertyField(uri="hasco:hasElevation")
        private String elevation;
        @PropertyField(uri="hasco:hasDatasetUri")
        private List<String> datasetURIs;
        @PropertyField(uri="hasco:hasCellScopeUri")
        private List<String> cellScopeUri;
        @PropertyField(uri="hasco:hasCellScopeName")
        private List<String> cellScopeName;
        @PropertyField(uri="hasco:hasMessageStatus")
        private String messageStatus;
        @PropertyField(uri="hasco:hasStatus")
        private int status;
        @PropertyField(uri="vstoi:hasSIRManagerEmail")
        private String hasSIRManagerEmail;
        */

		str.setUri(uri);
		
		return str;
	}

    public static List<Stream> findByStateDeployment(State state, String deploymentUri) {
        String queryString = "";
        if (state.getCurrent() == State.DESIGN) { 
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
            		"SELECT ?uri WHERE { " + 
                    "   ?uri a hasco:Stream . " + 
                    "   ?uri hasco:hasDeployment <" + deploymentUri + "> . " + 
                    //"   ?uri vstoi:designedAtTime ?datetime . " +
                    "   FILTER NOT EXISTS { ?uri prov:startedAtTime ?startdatetime . } " + 
                    "   FILTER NOT EXISTS { ?uri prov:endedAtTime ?enddatetime . } " + 
                    "} " + 
                    " ORDER BY DESC(?datetime) ";
        } else if (state.getCurrent() == State.ACTIVE) { 
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                    "SELECT ?uri WHERE { " + 
                    "   ?uri a hasco:Stream . " + 
                    "   ?uri hasco:hasDeployment <" + deploymentUri + "> . " + 
                    "   ?uri prov:startedAtTime ?startedattime . " + 
                    "   FILTER NOT EXISTS { ?uri prov:endedAtTime ?enddatetime . } " + 
                    "} " + 
                    " ORDER BY DESC(?startedattime)";
        } else if (state.getCurrent() == State.CLOSED) {
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                    "SELECT ?uri WHERE { " + 
                    "   ?uri a hasco:Stream . " + 
                    "   ?uri hasco:hasDeployment <" + deploymentUri + "> . " + 
                    "   ?uri prov:startedAtTime ?startedattime .  " + 
                    "   ?uri prov:endedAtTime ?enddatetime .  " + 
                    "} " +
                    " ORDER BY DESC(?startedattime)";
        } else if (state.getCurrent() == State.ALL) {
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                    "SELECT ?uri WHERE { " + 
                    "   ?uri a hasco:Stream . " + 
                    "   ?uri hasco:hasDeployment <" + deploymentUri + "> . " + 
                    "} " +
                    " ORDER BY DESC(?datetime)";
        } else {
            System.out.println("[ERROR] Stream.java: no valid state specified.");
            return null;
        }
        return findManyByQuery(queryString);
    }

    public static List<Stream> findCanUpdateByDeploymentWithPages(State state, String userEmail, String deploymentUri, int pageSize, int offset) {
        String queryString = "";
        if (state.getCurrent() == State.DESIGN) { 
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
            		"SELECT ?uri WHERE { " + 
                    "   ?uri a hasco:Stream . " + 
                    "   ?uri hasco:hasDeployment <" + deploymentUri + "> . " + 
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
                    "   ?uri a hasco:Stream . " + 
                    "   ?uri hasco:hasDeployment <" + deploymentUri + "> . " + 
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
                    "   ?uri a hasco:Stream . " + 
                    "   ?uri hasco:hasDeployment <" + deploymentUri + "> . " + 
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
                    "   ?uri a hasco:Stream . " + 
                    "   ?uri hasco:hasDeployment <" + deploymentUri + "> . " + 
                    "   ?uri hasco:canUpdate ?userEmail . " +
                    "   FILTER (?userEmail = \"" + userEmail + "\") " +
                    "} " +
                    " ORDER BY DESC(?datetime) " +
                    " LIMIT " + pageSize + 
                    " OFFSET " + offset;
        } else {
            System.out.println("[ERROR] Stream.java: no valid state specified.");
            return null;
        }
        return findManyByQuery(queryString);
    }

    public static int findTotalCanUpdateByDeploymentWithPages(State state, String userEmail, String deploymentUri) {
        String queryString = "";
        if (state.getCurrent() == State.DESIGN) { 
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                "SELECT (count(?uri) as ?tot) WHERE { " + 
                "   ?uri a hasco:Stream . " + 
                "   ?uri hasco:hasDeployment <" + deploymentUri + "> . " + 
                "   ?uri hasco:canUpdate ?userEmail . " +
                "   FILTER (?userEmail = \"" + userEmail + "\") " +
                "   FILTER NOT EXISTS { ?uri prov:startedAtTime ?startdatetime . } " + 
                "   FILTER NOT EXISTS { ?uri prov:endedAtTime ?enddatetime . } " + 
                "} ";
        } else if (state.getCurrent() == State.ACTIVE) { 
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                "SELECT (count(?uri) as ?tot) WHERE { " + 
                "   ?uri a hasco:Stream . " + 
                "   ?uri hasco:hasDeployment <" + deploymentUri + "> . " + 
                "   ?uri hasco:canUpdate ?userEmail . " +
                "   ?uri prov:startedAtTime ?startdatetime . " + 
                "   FILTER (?userEmail = \"" + userEmail + "\") " +
                "   FILTER NOT EXISTS { ?uri prov:endedAtTime ?enddatetime . } " + 
                "} "; 
        } else if (state.getCurrent() == State.CLOSED) {
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                "SELECT (count(?uri) as ?tot) WHERE { " + 
                "   ?uri a hasco:Stream . " + 
                "   ?uri hasco:hasDeployment <" + deploymentUri + "> . " + 
                "   ?uri hasco:canUpdate ?userEmail . " +
                "   ?uri prov:startedAtTime ?startdatetime .  " + 
                "   ?uri prov:endedAtTime ?enddatetime .  " + 
                "   FILTER (?userEmail = \"" + userEmail + "\") " +
                "} ";
        } else if (state.getCurrent() == State.ALL) {
            queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                "SELECT (count(?uri) as ?tot) WHERE { " + 
                "   ?uri a hasco:Stream . " + 
                "   ?uri hasco:hasDeployment <" + deploymentUri + "> . " + 
                "   ?uri hasco:canUpdate ?userEmail . " +
                "   FILTER (?userEmail = \"" + userEmail + "\") " +
                "} ";
        } else {
            System.out.println("[ERROR] Stream.java: no valid state specified.");
            return -1;
        }
        return findTotalByQuery(queryString);                
    }

    public static List<String> findAllAccessibleDataAcquisition(String user_email) {
        List<String> results = new ArrayList<String>();
        List<String> accessLevels = new ArrayList<String>();

        //User user = User.find(user_uri);
        //if (null != user) {
        //    user.getGroupNames(accessLevels);
        //}

        for (Stream str : find()) {
            if (str == null || str.getUri() == null) {
                continue;
            }
            if (str.getPermissionUri() == null) {
                System.out.println("[ERROR] PermissionUri for STR " + str.getUri() + "is missing");
                continue;
            }
            if (str.getHasSIRManagerEmail() == null) {
                System.out.println("[ERROR] OwnerUri for STR " + str.getUri() + "is missing");
                continue;
            }
            if (str.getPermissionUri().equals("Public") || str.getPermissionUri().equals(user_email)
                    || str.getHasSIRManagerEmail().equals(user_email)) {
                results.add(str.getUri());
                continue;
            }

            for (String level : accessLevels) {
                if (str.getPermissionUri().equals(level)) {
                    results.add(str.getUri());
                }
            }
        }

        return results;
    }

    public static List<Stream> find() {
        String query = NameSpaces.getInstance().printSparqlNameSpaceList() +
            "SELECT ?uri WHERE { " + 
            "   ?uri a hasco:Stream . " + 
            "} "; 
        return findManyByQuery(query);
    }

    /* Open streams are those with ended_at_date =  9999-12-31T23:59:59.999Z */
    public static List<Stream> findOpenStreams() {
        /*  
        SolrQuery query = new SolrQuery();
        query.set("q", "message_ip_str:* AND ended_at_date:\"9999-12-31T23:59:59.999Z\" AND -message_status_str:\"CLOSED\"");
        query.set("rows", "10000000");
        */
        String query = "";
        return findManyByQuery(query);
    }

    public static List<Stream> findManyByQuery(String query) {
        List<Stream> streams = new ArrayList<Stream>();
        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), query);
        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            String uri = soln.getResource("uri").getURI();
            Stream stream = Stream.find(uri);
            streams.add(stream);
        }
        return streams;
    }

    public static int findTotalByQuery(String query) {
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
    
    public void close(String endedAt) {
        setEndedAt(endedAt);
        saveToTripleStore();
    }


    public boolean deleteMeasurementData() {
        Iterator<String> iter = datasetURIs.iterator();
        //while (iter.hasNext()) {
        //    if (Measurement.deleteFromSolr(iter.next()) == 0) {
        //        iter.remove();
        //    }
        //}

        return datasetURIs.isEmpty();
    }

    public static String getProperDataAcquisitionUri(String fileName) {
        String base_name = FilenameUtils.getBaseName(fileName);
        List<Stream> da_list = find();

        // Use the longest match
        String daUri = "";
        int matchedQNameLength = 0;
        for (Stream da : da_list) {
            String abbrevUri = URIUtils.replaceNameSpaceEx(da.getUri());
            String qname = abbrevUri.split(":")[1];
            if (base_name.startsWith(qname)) {
                if (qname.length() > matchedQNameLength) {
                    matchedQNameLength = qname.length();
                    daUri = da.getUri();
                }
            }
        }

        if (!daUri.isEmpty()) {
            return daUri;
        }

        return null;
    }

    public void merge(Stream dataCollection) {
        Iterator<String> i;
        i = dataCollection.datasetURIs.iterator();
        while (i.hasNext()) {
            addDatasetUri(i.next());
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        Iterator<String> i;
        builder.append("localName: " + this.localName + "\n");
        builder.append("uri: " + this.getUri() + "\n");
        builder.append("started_at: " + this.getStartedAt() + "\n");
        builder.append("ended_at: " + this.getEndedAt() + "\n");
        builder.append("deployment_uri: " + this.deploymentUri + "\n");
        for (String cellUri : cellScopeUri) {
            builder.append("cellScopeUri: " + cellUri + "\n");
        }
        for (String cellName : cellScopeName) {
            builder.append("cellScopeName: " + cellName + "\n");
        }
        i = datasetURIs.iterator();
        while (i.hasNext()) {
            builder.append("dataset_uri: " + i.next() + "\n");
        }

        return builder.toString();
    }

    @Override
    public void save() {
        //System.out.println("Saving stream [" + uri + "]");
        //if (null == endedAt) {
        //    endedAt = "9999-12-31T23:59:59.999Z";
        //} else if (endedAt.toString().startsWith("9999")) {
        //    endedAt = "9999-12-31T23:59:59.999Z";
        //}
        saveToTripleStore();
    }

    @Override
    public void delete() {
        deleteFromTripleStore();
    }

}
