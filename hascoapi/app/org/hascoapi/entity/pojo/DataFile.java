package org.hascoapi.entity.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.io.FilenameUtils;
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
import org.hascoapi.annotations.PropertyField;
import org.hascoapi.ingestion.CSVRecordFile;
import org.hascoapi.ingestion.RecordFile;
import org.hascoapi.ingestion.SpreadsheetRecordFile;
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.ConfigProp;
import org.hascoapi.utils.Feedback;
import org.hascoapi.utils.IngestionLogger;
import org.hascoapi.utils.NameSpaces;
import org.hascoapi.utils.SPARQLUtils;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.vocabularies.RDF;
import org.hascoapi.vocabularies.RDFS;
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.vocabularies.VSTOI;
import org.hascoapi.vocabularies.PROV;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DataFile extends HADatAcThing implements Cloneable {

    // Process status for auto-annotator
    public static final String UNPROCESSED = "UNPROCESSED";
    public static final String PROCESSED = "PROCESSED";
    public static final String PROCESSED_STD = "PROCESSED_STD";
    public static final String WORKING = "WORKING";
    public static final String WORKING_STD = "WORKING_STD";

    // Process status for downloader
    public static final String CREATING = "CREATING";
    public static final String CREATED 	= "CREATED";
    public static final String DELETED  = "DELETED";

    // constant used for dataset generation
    public static final String DS_GENERATION = "download";

    @PropertyField(uri = "hasco:hasFileId")
    private String id;

    @PropertyField(uri = "hasco:hasFileViewableId")
    private String viewableId = "";

    @PropertyField(uri = "hasco:hasFileEditableId")
    private String editableId = "";

    @PropertyField(uri = "hasco:hasFilename")
    private String filename = "";

    @PropertyField(uri = "hasco:hasFileStatus")
    private String fileStatus;

    @PropertyField(uri = "vstoi:hasSIRManagerEmail")
    private String hasSIRManagerEmail = "";

    @PropertyField(uri = "hasco:hasViewerEmail")
    private List<String> viewerEmails;

    @PropertyField(uri = "hasco:hasEditorEmail")
    private List<String> editorEmails;

    @PropertyField(uri = "hasco:hasStudy")
    private String studyUri = "";

    @PropertyField(uri = "hasco:hasStream")
    private String streamUri = "";

    @PropertyField(uri = "hasco:hasDataset")
    private String datasetUri = "";

    @PropertyField(uri = "hasco:hasCompletionPercentage")
    private int completionPercentage = 0;

    @PropertyField(uri = "hasco:hasSubmissionTime")
    private String submissionTime = "";

    @PropertyField(uri = "hasco:hasCompletionTime")
    private String completionTime = "";

    @PropertyField(uri = "hasco:hasLastProcessTime")
    private String lastProcessTime = "";

    @PropertyField(uri = "prov:wasDerivedFrom")
    private List<String> wasDerivedFrom;

    @PropertyField(uri = "hasco:hasLog")
    private String log = "";

    private IngestionLogger logger = null;
    private RecordFile recordFile = null;
    private File file = null;

    // Permissible actions depending on user
    private boolean allowViewing = false;
    private boolean allowEditing = false;
    private boolean allowRenaming = false;
    private boolean allowMoving = false;
    private boolean allowDeleting = false;
    private boolean allowSharing = false;
    private boolean allowDownloading = false;
    private boolean allowIngesting = false;
    private boolean allowVerifying = false;

    public DataFile() {
        logger = new IngestionLogger(this);
    }

    public DataFile(String id, String filename) {
        this.id = id;
        this.filename = filename;
        logger = new IngestionLogger(this);
    }

    public Object clone()throws CloneNotSupportedException {
        return (DataFile)super.clone();
    }

    @Override
    public int hashCode() {
        return filename.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DataFile) {
            return filename.equals(((DataFile) o).filename);
        }
        return false;
    }

    public boolean getAllowViewing() {
        return allowViewing;
    }
    public void setAllowViewing(boolean allowViewing) {
        this.allowViewing = allowViewing;
    }

    public boolean getAllowEditing() {
        return allowEditing;
    }
    public void setAllowEditing(boolean allowEditing) {
        this.allowEditing = allowEditing;
    }

    public boolean getAllowRenaming() {
        return allowRenaming;
    }
    public void setAllowRenaming(boolean allowRenaming) {
        this.allowRenaming = allowRenaming;
    }

    public boolean getAllowMoving() {
        return allowMoving;
    }
    public void setAllowMoving(boolean allowMoving) {
        this.allowMoving = allowMoving;
    }

    public boolean getAllowDeleting() {
        return allowDeleting;
    }
    public void setAllowDeleting(boolean allowDeleting) {
        this.allowDeleting = allowDeleting;
    }

    public boolean getAllowSharing() {
        return allowSharing;
    }
    public void setAllowSharing(boolean allowSharing) {
        this.allowSharing = allowSharing;
    }

    public boolean getAllowDownloading() {
        return allowDownloading;
    }
    public void setAllowDownloading(boolean allowDownloading) {
        this.allowDownloading = allowDownloading;
    }

    public boolean getAllowIngesting() {
        return allowIngesting;
    }
    public void setAllowIngesting(boolean allowIngesting) {
        this.allowIngesting = allowIngesting;
    }

    public boolean getAllowVerifying() {
        return allowVerifying;
    }
    public void setAllowVerifying(boolean allowVerifying) {
        this.allowVerifying = allowVerifying;
    }

    public static void updatePermission(List<DataFile> dataFiles, String userEmail) {
        for (DataFile dataFile : dataFiles) {
            dataFile.updatePermissionByUserEmail(userEmail);
        }
    }

    public void updatePermissionByUserEmail(String userEmail) {
        if (getHasSIRManagerEmail().equals(userEmail)) {
            setAllowViewing(true);
            setAllowEditing(true);
            setAllowRenaming(true);
            setAllowMoving(true);
            setAllowDeleting(true);
            setAllowSharing(true);
            setAllowDownloading(true);
            setAllowIngesting(true);
            setAllowVerifying(true);
        } else if (getEditorEmails().contains(userEmail)) {
            setAllowViewing(true);
            setAllowEditing(true);
            setAllowDownloading(true);
        } else if (getViewerEmails().contains(userEmail)) {
            setAllowViewing(true);
            setAllowDownloading(true);
        }
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getViewableId() {
        return viewableId;
    }
    public void setViewableId(String viewableId) {
        this.viewableId = viewableId;
    }

    public String getEditableId() {
        return editableId;
    }
    public void setEditableId(String editableId) {
        this.editableId = editableId;
    }

    public IngestionLogger getLogger() {
        return logger;
    }
    public void setLogger(IngestionLogger logger) {
        this.logger = logger;
    }

    public RecordFile getRecordFile() {
        return recordFile;
    }
    public void setRecordFile(RecordFile recordFile) {
        this.recordFile = recordFile;
        this.file = recordFile.getFile();
    }

    public File getFile() {
        return file;
    }

    public String getBaseName() {
        return FilenameUtils.getBaseName(filename);
    }

    public String getFileExtention() {
        return FilenameUtils.getExtension(filename);
    }

    public String getHasSIRManagerEmail() {
        return hasSIRManagerEmail;
    }
    public void setHasSIRManagerEmail(String hasSIRManagerEmail) {
        this.hasSIRManagerEmail = hasSIRManagerEmail;
    }

    public List<String> getViewerEmails() {
        if (viewerEmails == null) {
            viewerEmails = new ArrayList<String>();
        }
        return viewerEmails;
    }
    public void setViewerEmails(List<String> viewerEmails) {
        this.viewerEmails = viewerEmails;
    }
    public void addViewerEmail(String viewerEmail) {
        if (!viewerEmails.contains(viewerEmail)) {
            viewerEmails.add(viewerEmail);
        }
    }
    public void removeViewerEmail(String viewerEmail) {
        if (viewerEmails.contains(viewerEmail)) {
            viewerEmails.remove(viewerEmail);
        }
    }

    public List<String> getEditorEmails() {
        if (editorEmails == null) {
            editorEmails = new ArrayList<String>();
        }
        return editorEmails;
    }
    public void setEditorEmails(List<String> editorEmails) {
        this.editorEmails = editorEmails;
    }
    public void addEditorEmail(String editorEmail) {
        if (!editorEmails.contains(editorEmail)) {
            editorEmails.add(editorEmail);
        }
    }
    public void removeEditorEmail(String editorEmail) {
        if (editorEmails.contains(editorEmail)) {
            editorEmails.remove(editorEmail);
        }
    }

    public String getStudyUri() {
        return studyUri;
    }
    public void setStudyUri(String studyUri) {
        this.studyUri = studyUri;
    }

    public String getStreamUri() {
        return streamUri;
    }
    public void setStreamUri(String streamUri) {
        this.streamUri = streamUri;
    }

    public String getDatasetUri() {
        return datasetUri;
    }
    public void setDatasetUri(String datasetUri) {
        this.datasetUri = datasetUri;
    }

    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileStatus() {
        return fileStatus;
    }
    public void setFileStatus(String fileStatus) {
        this.fileStatus = fileStatus;
    }

    public int getCompletionPercentage() {
        return completionPercentage;
    }
    public void setCompletionPercentage(int completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public String getSubmissionTime() {
        return submissionTime;
    }
    public void setSubmissionTime(String submissionTime) {
        this.submissionTime = submissionTime;
    }

    public String getCompletionTime() {
        return completionTime;
    }
    public void setCompletionTime(String completionTime) {
        this.completionTime = completionTime;
    }

    public String getLastProcessTime() {
        return lastProcessTime;
    }
    public void setLastProcessTime(String lastProcessTime) {
        this.lastProcessTime = lastProcessTime;
    }

    public List<String> getWasDerivedFrom() {
        return wasDerivedFrom;
    }
    public void setWasDerivedFrom(List<String> wasDerivedFromList) {
        if (this.wasDerivedFrom == null) {
            this.wasDerivedFrom = new ArrayList<String>();
        }
        this.wasDerivedFrom = wasDerivedFromList;
    }
    public void addWasDerivedFrom(String wasDerivedFromInd) {
        if (!wasDerivedFrom.contains(wasDerivedFromInd)) {
            wasDerivedFrom.add(wasDerivedFromInd);
        }
    }
    public void removeWasDerivedFrom(String wasDerivedFromInd) {
        if (wasDerivedFrom.contains(wasDerivedFromInd)) {
            wasDerivedFrom.remove(wasDerivedFromInd);
        }
    }

    public String getLog() {
        return getLogger().getLog();
    }
    public void setLog(String log) {
        getLogger().setLog(log);
        this.log = log;
    }
 
    @Override
    public void save() {
        this.log = getLog();
        if (uri.isEmpty() && getNamedGraph().isEmpty()) {
            System.out.println("[ERROR] (DataFile.java) Trying to save Datafile outside named graph.");
            return;
        }
        if (!uri.isEmpty()) {
            System.out.println("[WARNING] (DataFile.java) Using Datafile's URI as named graph's name.");
            this.setNamedGraph(uri);
        }
        saveToTripleStore();
    }

    @Override
    public void delete() {
        getLogger().resetLog();
        //deleteFromTripleStore();

        // In HASCOAPI, the DataFile is a graph named with the DataFile's URI. 
        // The entire named graph should be deleted when the DataFile is removed  
        String query = "DELETE WHERE { \n" +
            "    GRAPH "; 
        if (getUri().startsWith("http")) {
            query += "<" + this.getUri() + ">";
        } else {
            query += this.getUri();
        }
        query += " { ?s ?p ?o . } \n";
        query += " } ";

        //System.out.println("Delete named graph query: [" + query + "]");
        UpdateRequest request = UpdateFactory.create(query);
        UpdateProcessor processor = UpdateExecutionFactory.createRemote(
            request, CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_UPDATE));
        processor.execute();
    }

    public void resetForUnprocessed() {
        setFileStatus(DataFile.UNPROCESSED);
        getLogger().resetLog();
        setSubmissionTime(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
        setCompletionTime("");
    }

    /** 
    public static DataFile create(String id, String filename, String hasSIRManagerEmail, String status) {
        DataFile dataFile = new DataFile(id, filename);
        dataFile.setHasSIRManagerEmail(hasSIRManagerEmail);
        dataFile.setFileStatus(status);
        dataFile.setSubmissionTime(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));

        //if (filename.startsWith("DA-")) {
        //    String streamUri = STR.getProperStreamUri(filename);
        //    dataFile.setDataAcquisitionUri(streamUri == null ? "" : streamUri);
        //}

        dataFile.save();

        return dataFile;
    }
    */

    public static DataFile find(String uri) {
        DataFile dataFile = null;
        Statement statement;
        RDFNode object;

        String queryString = "DESCRIBE <" + uri + ">";
        Model model = SPARQLUtils.describe(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.SPARQL_QUERY), queryString);

        StmtIterator stmtIterator = model.listStatements();

        if (!stmtIterator.hasNext()) {
            return null;
        }

        dataFile = new DataFile();

        while (stmtIterator.hasNext()) {
            statement = stmtIterator.next();
            object = statement.getObject();
            String str = URIUtils.objectRDFToString(object);
            if (statement.getPredicate().getURI().equals(RDFS.LABEL)) {
                dataFile.setLabel(str);
            } else if (statement.getPredicate().getURI().equals(RDF.TYPE)) {
                dataFile.setTypeUri(str);
            } else if (statement.getPredicate().getURI().equals(RDFS.COMMENT)) {
                dataFile.setComment(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HASCO_TYPE)) {
                dataFile.setHascoTypeUri(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_FILE_ID)) {
                dataFile.setId(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_FILE_VISIBLE_ID)) {
                dataFile.setViewableId(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_FILE_EDITABLE_ID)) {
                dataFile.setEditableId(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_FILENAME)) {
                dataFile.setFilename(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_FILE_STATUS)) {
                dataFile.setFileStatus(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_SIR_MANAGER_EMAIL)) {
                dataFile.setHasSIRManagerEmail(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_VIEWER_EMAIL)) {
                List<String> viewerList = dataFile.getViewerEmails();
                if (!viewerList.contains(str)) {
                    viewerList.add(str);
                    dataFile.setViewerEmails(viewerList);
                }
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_EDITOR_EMAIL)) {
                List<String> editorList = dataFile.getEditorEmails();
                if (!editorList.contains(str)) {
                    editorList.add(str);
                    dataFile.setViewerEmails(editorList);
                }
                dataFile.setEditorEmails(editorList);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_STUDY)) {
                dataFile.setStudyUri(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_STREAM)) {
                dataFile.setStreamUri(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_DATASET)) {
                dataFile.setDatasetUri(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_COMPLETION_PERCENTAGE)) {
                dataFile.setCompletionPercentage(Integer. parseInt(str));
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_SUBMISSION_TIME)) {
                dataFile.setSubmissionTime(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_COMPLETION_TIME)) {
                dataFile.setCompletionTime(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_LAST_PROCESS_TIME)) {
                dataFile.setLastProcessTime(str);
            } else if (statement.getPredicate().getURI().equals(PROV.WAS_DERIVED_FROM)) {
                List<String> wasDerivedFromList = dataFile.getWasDerivedFrom();
                if (!wasDerivedFromList.contains(str)) {
                    wasDerivedFromList.add(str);
                    dataFile.setWasDerivedFrom(wasDerivedFromList);
                }
                dataFile.setWasDerivedFrom(wasDerivedFromList);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_LOG)) {
                dataFile.setLog(str);
            }

        }

        dataFile.setUri(uri);

        return dataFile;
    }

    private static List<DataFile> findByQuery(String queryString) {
        List<DataFile> dataFiles = new ArrayList<DataFile>();
        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);
        if (!resultsrw.hasNext()) {
            return null;
        }
        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            DataFile dataFile = find(soln.getResource("uri").getURI());
            dataFiles.add(dataFile);
        }
        dataFiles.sort(new Comparator<DataFile>() {
            @Override
            public int compare(DataFile o1, DataFile o2) {
                return o1.getSubmissionTime().compareTo(o2.getSubmissionTime());
            }
        });
        return dataFiles;
    }

    private static DataFile findOneByQuery(String queryString) {
        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);
        if (!resultsrw.hasNext()) {
            return null;
        }
        if (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            return find(soln.getResource("uri").getURI());
        }
        return null;
    }

    public static List<DataFile> find(String managerEmail, String status) {
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                "   ?type rdfs:subClassOf* hasco:Datafile . " +
                "   ?uri a ?type ." +
				"   ?uri hasco:hasManagerEmail ?managerEmail . " +
				"   ?uri hasco:hasCompletionTime ?completionTime . " +
				"   FILTER (?managerEmail = \"" + managerEmail + "\") " +
				"   ?uri hasco:hasFileStatus ?status . " +
				"   FILTER (?status = \"" + status + "\") " +
                "} " +
                "ORDER BY DESC(?completionTime) ";
        return findByQuery(queryString);
    }

    public static List<DataFile> findByStatus(String status) {
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                "   ?type rdfs:subClassOf* hasco:Datafile . " +
                "   ?uri a ?type ." +
                "} ";
        return findByQuery(queryString);
    }

    public static int totalByStatus(String status) {
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
		        " SELECT (count(?uri) as ?tot) WHERE { " +
                "   ?type rdfs:subClassOf* hasco:Datafile . " +
                "   ?uri a ?type ." +
                "} ";
        return GenericFind.findTotalByQuery(queryString);
    }

    public static List<DataFile> findByMultiStatus(List<String> status) {
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                "   ?type rdfs:subClassOf* hasco:Datafile . " +
                "   ?uri a ?type ." +
                "} ";
        return findByQuery(queryString);
    }

    public static List<DataFile> findByStream(String dataStremUri) {
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                "   ?type rdfs:subClassOf* hasco:Datafile . " +
                "   ?uri a ?type ." +
                "} ";
        return findByQuery(queryString);
    }

    public static int totalByMultiStatus(List<String> status) {
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
		        " SELECT (count(?uri) as ?tot) WHERE { " +
                "   ?type rdfs:subClassOf* hasco:Datafile . " +
                "   ?uri a ?type ." +
                "} ";
        return GenericFind.findTotalByQuery(queryString);
    }

    public static DataFile findByIdAndEmail(String id, String ownerEmail) {
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                "   ?type rdfs:subClassOf* hasco:Datafile . " +
                "   ?uri a ?type ." +
                "} ";
        return findOneByQuery(queryString);
    }

    public static DataFile findByIdAndStatus(String id, String status) {
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                "   ?type rdfs:subClassOf* hasco:Datafile . " +
                "   ?uri a ?type ." +
                "} ";
        return findOneByQuery(queryString);
    }

    public static DataFile findByNameAndStatus(String filename, String status) {
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                "   ?type rdfs:subClassOf* hasco:Datafile . " +
                "   ?uri a ?type ." +
                "} ";
        return findOneByQuery(queryString);
    }

    public static DataFile findByIdAndOwnerEmailAndStatus(String id, String ownerEmail, String status) {
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                "   ?type rdfs:subClassOf* hasco:Datafile . " +
                "   ?uri a ?type ." +
                "} ";
        return findOneByQuery(queryString);
    }

    public static DataFile findByViewableId(String id) {
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                "   ?type rdfs:subClassOf* hasco:Datafile . " +
                "   ?uri a ?type ." +
                "} ";
        return findOneByQuery(queryString);
    }

    public static DataFile findByEditableId(String id) {
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                "   ?type rdfs:subClassOf* hasco:Datafile . " +
                "   ?uri a ?type ." +
                "} ";
        return findOneByQuery(queryString);
    }

    public static DataFile findById(String id) {
        return findByIdAndEmail(id, null);
    }

    public static boolean hasValidExtension(String filename) {
        List<String> validExtensions = Arrays.asList(".csv", ".xlsx");
        for (String ext : validExtensions) {
            filename.endsWith(ext);
            return true;
        }

        return false;
    }


}
