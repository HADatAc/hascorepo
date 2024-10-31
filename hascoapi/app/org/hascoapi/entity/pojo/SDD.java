package org.hascoapi.entity.pojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.net.URL;

import com.fasterxml.jackson.annotation.JsonFilter;
import org.apache.commons.io.FileUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
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
import org.hascoapi.annotations.PropertyField;
import org.hascoapi.entity.pojo.SDDAttribute;
import org.hascoapi.entity.pojo.SDDObject;
import org.hascoapi.ingestion.Record;
import org.hascoapi.ingestion.RecordFile;
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.FirstLabel;
import org.hascoapi.utils.IngestionLogger;
import org.hascoapi.utils.NameSpaces;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.utils.SPARQLUtils;
import org.hascoapi.utils.Templates;
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.vocabularies.RDF;
import org.hascoapi.vocabularies.RDFS;
import org.hascoapi.vocabularies.VSTOI;

@JsonFilter("sddFilter")
public class SDD extends MetadataTemplate {

    /********************************* 
     * 
     *        CLASS VARIABLES
     * 
     *********************************/

    //private Map<String, String> mapCatalog = new HashMap<String, String>();
    private Map<String, String> codeMappings = new HashMap<String, String>();
    private Map<String, String> mapAttrObj = new HashMap<String, String>();
    private Map<String, Map<String, String>> codebook = new HashMap<String, Map<String, String>>();
    private Map<String, Map<String, String>> timeline = new HashMap<String, Map<String, String>>();
    private static Map<String, SDD> SDDCache;
    private List<SDDAttribute> attributesCache = new ArrayList<SDDAttribute>();
    private List<SDDObject> objectsCache = new ArrayList<SDDObject>();
    private Map<String, Map<String, String>> possibleValuesCache = new HashMap<String, Map<String, String>>();
    private DataFile sddfile = null;
    private IngestionLogger logger = null;
    private Templates templates = null;

    //@PropertyField(uri = "vstoi:hasStatus")    
    //private String hasStatus;

    //@PropertyField(uri = "vstoi:hasVersion")
    //private String hasVersion;

    //@PropertyField(uri = "hasco:hasDataFile")
    //private String hasDataFileUri;

    //@PropertyField(uri = "vstoi:hasSIRManagerEmail")
    //private String hasSIRManagerEmail;

    @PropertyField(uri = "hasco:TimeStamp")
    private String timestampLabel = "";

    @PropertyField(uri = "sio:SIO_000418")
    private String timeInstantLabel = "";

    @PropertyField(uri = "hasco:namedTime")
    private String namedTimeLabel = "";

    @PropertyField(uri = "hasco:uriId")
    private String idLabel = "";

    @PropertyField(uri = "hasco:originalID")
    private String originalIdLabel = "";

    @PropertyField(uri = "hasco:hasElevation")
    private String elevationLabel = "";

    @PropertyField(uri = "hasco:hasMetaEntity")
    private String entityLabel = "";

    @PropertyField(uri = "hasco:hasMetaUnit")
    private String unitLabel = "";

    @PropertyField(uri = "sio:SIO_000668")
    private String inRelationToLabel = "";

    @PropertyField(uri = "hasco:hasLOD")
    private String lodLabel = "";

    @PropertyField(uri = "hasco:isGroupMember")
    private String groupLabel = "";

    @PropertyField(uri = "hasco:matchesWith")
    private String matchingLabel = "";
    
    public static List<String> METASDDA = Arrays.asList(
            "hasco:TimeStamp", 
            "sio:SIO_000418", 
            "hasco:namedTime", 
            "hasco:originalID", 
            "hasco:uriId", 
            "hasco:hasMetaEntity", 
            "hasco:hasMetaEntityURI", 
            "hasco:hasMetaAttribute", 
            "hasco:hasMetaAttributeURI", 
            "hasco:hasMetaUnit", 
            "hasco:hasMetaUnitURI", 
            "sio:SIO_000668",
            "hasco:hasLOD",
            "hasco:hasCalibration",
            "hasco:hasElevation",
            "hasco:hasLocation",
            "hasco:isGroupMember",
            "hasco:matchesWith");

    /** 
    private static List<String> metaAttributes;
    private static List<String> getMetaAttributes() {
        if (metaAttributes == null) {
            metaAttributes = new ArrayList<String>() {{
                add("hasco:TimeStamp");
                add("sio:SIO_000418");
                add("hasco:namedTime");
                add("hasco:originalID");
                add("hasco:uriId");
                add("hasco:hasMetaEntity");
                add("hasco:hasMetaEntityURI");
                add("hasco:hasMetaAttribute");
                add("hasco:hasMetaAttributeURI");
                add("hasco:hasMetaUnit");
                add("hasco:hasMetaUnitURI");
                add("sio:SIO_000668");
                add("hasco:hasLOD");
                add("hasco:hasCalibration");
                add("hasco:hasElevation");
                add("hasco:hasLocation");
                add("hasco:AnalysisMode");
                add("hasco:LabHubAccession");
                add("hasco:LevelOfDetection");
                add("hasco:ReplicateNumber");
            }};
        }
        return metaAttributes;
    }
    */

    private List<String> attributes = new ArrayList<String>();
    private List<String> objects = new ArrayList<String>();
    private List<String> events = new ArrayList<String>();
    private boolean isRefreshed = false;

    /********************************* 
     * 
     *        CONSTRUCTS
     * 
     *********************************/

    public SDD() {
        SDD.getCache();
    }

    public SDD(DataFile dataFile, String templateFile) {
        this.uri = dataFile.getUri().replace("DF","SD");
        this.label = "";
        this.setTemplates(templateFile);
        isRefreshed = false;
        //SDD.getCache();
        //getAttributes();
        //getObjects();
    }

    public SDD(String uri, String label) {
        this.uri = uri;
        this.label = label;
        isRefreshed = false;
        //SDD.getCache();
        //getAttributes();
        //getObjects();
    }

    /*************************************** 
     * 
     *        METHODS  SETS/GETS
     * 
     ***************************************/

    private static Map<String, SDD> getCache() {
        if (SDDCache == null) {
            SDDCache = new HashMap<String, SDD>(); 
        } 
        return SDDCache;
    }

    //public Map<String, String> getCatalog() {
    //    return mapCatalog;
    //}

    public Map<String, String> getCodeMapping() {
        return codeMappings;
    }

    public Map<String, String> getMapAttrObj() {
        return mapAttrObj;
    }

    public Map<String, Map<String, String>> getCodebook() {
        return codebook;
    }

    public Map<String, Map<String, String>> getTimeLine() {
        return timeline;
    }

    //public String getUri() {
    //    return uri;
    //}
    public String getUriNamespace() {
        return URIUtils.replaceNameSpaceEx(uri);
    }
    //public void setUri(String uri) {
    //    this.uri = uri;
    //}

    //public String getLabel() {
    //    return label;
    //}
    //public void setLabel(String label) {
    //    this.label = label;
    //}

    //public String getHasVersion() {
    //    return hasVersion;
    //}
    //public void setHasVersion(String hasVersion) {
    //    this.hasVersion = hasVersion;
    //}

    //public String getHasDataFile() {
    //    return hasDataFileUri;
    //}
    //public void setHasDataFile(String hasDataFileUri) {
    //    this.hasDataFileUri = hasDataFileUri;
    //}
    //public DataFile getDataFile() {
        //System.out.println("Inside SDD.getDataFile(). hasDataFileuri is " + this.hasDataFileUri);
    //    DataFile dataFile = DataFile.find(this.hasDataFileUri);
    //    return dataFile;
    //}

    //public String getHasStatus() {
    //    return hasStatus;
    //}
    //public void setHasStatus(String hasStatus) {
    //    this.hasStatus = hasStatus;
    //}

    public String getTimestampLabel() {
        return timestampLabel;
    }
    public void setTimestampLabel(String timestampLabel) {
        this.timestampLabel = timestampLabel;
    }

    public String getTimeInstantLabel() {
        return timeInstantLabel;
    }
    public void setTimeInstantLabel(String timeInstantLabel) {
        this.timeInstantLabel = timeInstantLabel;
    }

    public String getNamedTimeLabel() {
        return namedTimeLabel;
    }
    public void setNamedTimeLabel(String namedTimeLabel) {
        this.namedTimeLabel = namedTimeLabel;
    }

    public String getIdLabel() {
        return idLabel;
    }
    public void setIdLabel(String idLabel) {
        this.idLabel = idLabel;
    }

    public String getOriginalIdLabel() {
        return originalIdLabel;
    }
    public void setOriginalIdLabel(String originalIdLabel) {
        this.originalIdLabel = originalIdLabel;
    }

    public String getLODLabel() {
        return lodLabel;
    }
    public void setLODLabel(String lodLabel) {
        this.lodLabel = lodLabel;
    }

    public String getGroupLabel() {
        return groupLabel;
    }
    public void setGroupLabel(String groupLabel) {
        this.groupLabel = groupLabel;
    }

    public String getMatchingLabel() {
        return matchingLabel;
    }
    public void setMatchingLabel(String matchingLabel) {
        this.matchingLabel = matchingLabel;
    }

    //public String getHasSIRManagerEmail() {
    //    return hasSIRManagerEmail;
    //}
    //public void setHasSIRManagerEmail(String hasSIRManagerEmail) {
    //    this.hasSIRManagerEmail = hasSIRManagerEmail;
    //}

    public String getElevationLabel() {
        return elevationLabel;
    }
    public void setElevationLabel(String elevationLabel) {
        this.elevationLabel = elevationLabel;
    }

    public String getEntityLabel() {
        return entityLabel;
    }
    public void setEntityLabel(String entityLabel) {
        this.entityLabel = entityLabel;
    }

    public String getUnitLabel() {
        return unitLabel;
    }
    public void setUnitLabel(String unitLabel) {
        this.unitLabel = unitLabel;
    }

    public String getInRelationToLabel() {
        return inRelationToLabel;
    }
    public void setInRelationToLabel(String inRelationToLabel) {
        this.inRelationToLabel = inRelationToLabel;
    }

    public Templates getTemplates() {
        return this.templates;
    }
    public void setTemplates(String templateFile) {
        this.templates = new Templates(templateFile);
    }

    public int getTotalSDDA() {
        if (attributes == null) {
            return -1;
        }
        return attributes.size();
    }

    public int getTotalSDDE() {
        if (events == null) {
            return -1;
        }
        return events.size();
    }

    public int getTotalSDDO() {
        if (objects == null) {
            return -1;
        }
        return objects.size();
    }

    /*************************************** 
     * 
     *        METHODS  DIVERSE
     * 
     ***************************************/

    public static void resetCache() {
        SDDAttribute.resetCache();
        SDDObject.resetCache();
        SDDCache = null;
    }

    public void resetAttributesCache() {
        attributesCache = null;
    }

    public void resetObjectsCache() {
        objectsCache = null;
    }

    public File downloadFile(String fileURL, String filename) {
        //System.out.println("fileURL: " + fileURL);
        
        if (fileURL == null || fileURL.length() == 0) {
            return null;
        } else {
            try {
                URL url = new URL(fileURL);
                File file = new File(filename);
                FileUtils.copyURLToFile(url, file);
                return file;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /* 

    public List<String> defineTemporaryPositions(List<String> csvHeaders) {
        List<String> unknownHeaders = new ArrayList<String>(csvHeaders);
        List<SDDAttribute> listDasa = getAttributes();
        
        // Assign SDDA positions by label matching
        if (listDasa != null && listDasa.size() > 0) {
            // reset temporary positions
            for (SDDAttribute sdda : listDasa) {
                sdda.setTempPositionInt(-1);
            }

            for (int i = 0; i < csvHeaders.size(); i++) {
                for (SDDAttribute sdda : listDasa) {
                    if (csvHeaders.get(i).equalsIgnoreCase(sdda.getLabel())) {
                        sdda.setTempPositionInt(i);
                        unknownHeaders.remove(csvHeaders.get(i));
                    }
                }
            }
        }
        
        // Assign SDDO positions by label matching
        List<SDDObject> listDaso = getObjects();
        if (listDaso != null && listDaso.size() > 0) {
            // reset temporary positions
            for (SDDObject sddo : listDaso) {
                sddo.setTempPositionInt(-1);
            }

            for (int i = 0; i < csvHeaders.size(); i++) {
                for (SDDObject sddo : listDaso) {
                    if (csvHeaders.get(i).equalsIgnoreCase(sddo.getLabel())) {
                        sddo.setTempPositionInt(i);
                        unknownHeaders.remove(csvHeaders.get(i));
                    }
                }
            }
        }

        return unknownHeaders;
    }
    */

    /* 
    public int tempPositionOfLabel(String label) {
        if (label == null || label.equals("")) {
            return -1;
        }

        int position = -1;
        for (SDDAttribute sdda : getAttributes()) {
            if (sdda.getLabel().equalsIgnoreCase(label)) {
                position = sdda.getTempPositionInt();
                break;
            }
        }

        if (position != -1) {
            return position;
        }

        for (SDDObject sddo : getObjects()) {
            if (sddo.getLabel().equalsIgnoreCase(label)) {
                position = sddo.getTempPositionInt();
                break;
            }
        }

        return position;
    }
    */

    /************************************************ 
     * 
     *        METHODS  FIND (RETRIEVE SDDs)
     * 
     ************************************************/

    
    private static SDD findCoreProperties(String uri) {
        Statement statement;
        RDFNode object;

        String queryString = "DESCRIBE <" + uri + ">";
        Model model = SPARQLUtils.describe(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.SPARQL_QUERY), queryString);

        StmtIterator stmtIterator = model.listStatements();

        if (!stmtIterator.hasNext()) {
            return null;
        }

        SDD sdd = new SDD();

        while (stmtIterator.hasNext()) {
            statement = stmtIterator.next();
            object = statement.getObject();
            String str = URIUtils.objectRDFToString(object);
            //System.out.println(statement.getPredicate().getURI() + "  " + str);
            if (statement.getPredicate().getURI().equals(RDFS.LABEL)) {
                sdd.setLabel(str);
            } else if (statement.getPredicate().getURI().equals(RDF.TYPE)) {
                sdd.setTypeUri(str);
            } else if (statement.getPredicate().getURI().equals(RDFS.COMMENT)) {
                sdd.setComment(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HASCO_TYPE)) {
                sdd.setHascoTypeUri(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_DATAFILE)) {
                sdd.setHasDataFileUri(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_STATUS)) {
                sdd.setHasStatus(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_VERSION)) {
                sdd.setHasVersion(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_SIR_MANAGER_EMAIL)) {
                sdd.setHasSIRManagerEmail(str);
            }
        }

        sdd.setUri(uri);

        return sdd;
    }
    

    public static SDD find(String sddUri) {
        //System.out.println("SDD.find() with URI: " + sddUri);

        /* 
        if (SDD.getCache().get(sddUri) != null) {
        	
            SDD sdd = SDD.getCache().get(sddUri);
            sdd.getAttributes();
            return sdd;
        }
        */

        //System.out.println("Looking for data acquisition sdd " + sddUri);

        if (sddUri == null || sddUri.equals("")) {
            System.out.println("[ERROR] at SDD.java. URI blank or null.");
            return null;
        }

        SDD sdd = SDD.findCoreProperties(sddUri);

        if (sdd == null) {
            System.out.println("[ERROR] at SDD.java. Could not find sdd for uri: <" + sddUri + ">");
        	return null;
        }

        /* 
        sdd.setAttributes(SDDAttribute.findUriBySchema(sddUri));
        sdd.setObjects(SDDObject.findUriBySchema(sddUri));

        sdd.getAttributes();
        sdd.getObjects();
        SDD.getCache().put(sddUri,sdd);
        */
        return sdd;
    }
    

        /* 
    public static Map<String, String> findAllUrisByLabel(String sddUri) {
        Map<String, String> resp = new HashMap<String, String>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList()
                + " SELECT ?sddo_or_sdda ?label WHERE { "
                + " ?sddo_or_sdda rdfs:label ?label . "
                + " ?sddo_or_sdda hasco:partOfSchema <" + sddUri + "> . "
                + " }";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        String uriStr = "";
        String labelStr = "";
        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();

            if (soln.get("sddo_or_sdda") != null) {
                uriStr = soln.get("sddo_or_sdda").toString();
                if (soln.get("label") != null) {
                    labelStr = soln.get("label").toString();
                    if (uriStr != null && labelStr != null) {
                        resp.put(labelStr, uriStr);
                    }
                }
            }
        }
        return resp;
    }

    */

    /* 
    public static String findByLabel(String sddUri, String label) {
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList()
                + " SELECT ?sddo_or_sdda ?label WHERE { "
                + " ?sddo_or_sdda rdfs:label ?label . "
                + " ?sddo_or_sdda hasco:partOfSchema <" + sddUri + "> . "
                + " FILTER regex(str(?label), \"" + label + "\" ) "
                + " }";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        if (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            return soln.get("sddo_or_sdda").toString();
        }

        return "";
    }
    */

    /*************************************** 
     * 
     *        METHODS  SAVE/DELETE
     * 
     ***************************************/

    public static SDD create(String uri) {
        SDD sdd = new SDD();
        sdd.setUri(uri);
        return sdd;
    }

    @Override
    public void save() {
        saveToTripleStore();
    }

    @Override
    public void delete() {
        deleteFromTripleStore();
    }

    @Override
    public void deleteFromTripleStore() {
        super.deleteFromTripleStore();
        SDD.resetCache();
    }

    /************************************************ 
     * 
     *        METHODS  --   READ SDD COMPONENT
     * 
     ************************************************/


    public boolean readDataDictionary(RecordFile file, DataFile dataFile) {

        if (!file.isValid()) {
            return false;
        }


        //System.out.println("SDD: read Data Dictionary 1");

        System.out.println("header size: " + file.getHeaders().size());

        if (file.getHeaders().size() > 0) {
            dataFile.getLogger().println("The Dictionary Mapping has " + file.getHeaders().size() + " columns.");
        } else {
            dataFile.getLogger().printExceptionById("SDD_00007");
        }

        //System.out.println("SDD: read Data Dictionary 2");

        //Boolean uriResolvable = true;
        Boolean namespaceRegistered = true;
        Boolean hasLabel = true;
        Boolean isIndicator = true;

        List<String> checkUriNamespaceResults = new ArrayList<String>();
        List<String> checkUriLabelResults = new ArrayList<String>();
        List<String> checkCellValResults = new ArrayList<String>();
        //List<String> checkUriResolveResults = new ArrayList<String>();
        List<String> checkStudyIndicatorPathResults = new ArrayList<String>();
        List<String> dasaList = new ArrayList<String>();
        List<String> dasoList = new ArrayList<String>();
        Map<String, String> sa2so = new HashMap<String, String>();
        Map<String, String> so2so2 = new HashMap<String, String>();
        Map<String, String> so2type = new HashMap<String, String>();
        Map<String, String> so2role = new HashMap<String, String>();
        Map<String, String> so2df = new HashMap<String, String>();

        long rowNumber = 0;
        for (Record record : file.getRecords()) {
            rowNumber++;
            //System.out.println("processing row " + rowNumber);
            if (checkCellValue(record.getValueByColumnIndex(0))) {
                String attributeCell = record.getValueByColumnName("Attribute");
                String entityCell = record.getValueByColumnName("Entity");
                String roleCell = record.getValueByColumnName("Role");
                String relationCell = record.getValueByColumnName("Relation");
                String inRelationToCell = record.getValueByColumnName("inRelationTo");
                String attributeOfCell = record.getValueByColumnName("attributeOf");
                String dfCell = record.getValueByColumnName("wasDerivedFrom");

                if (attributeCell != null && !attributeCell.equals("")) {
                    if (!checkCellNamespace(attributeCell)) {
                        namespaceRegistered = false;
                        if (!checkUriNamespaceResults.contains(attributeCell)) {
                            checkUriNamespaceResults.add(attributeCell);
                        }
                    } else if (!checkCellLabel(attributeCell)) {
                        hasLabel = false;
                        if (!checkUriLabelResults.contains(attributeCell)) {
                            checkUriLabelResults.add(attributeCell);
                        }
                    } 
                }
                if (entityCell != null && !entityCell.equals("")) {
                    if (!checkCellNamespace(entityCell)) {
                        namespaceRegistered = false;
                        if (!checkUriNamespaceResults.contains(entityCell)) {
                            checkUriNamespaceResults.add(entityCell);
                        }
                    } else if (!checkCellLabel(entityCell)) {
                        hasLabel = false;
                        if (!checkUriLabelResults.contains(entityCell)) {
                            checkUriLabelResults.add(entityCell);
                        }
                    }
                } 
                if (roleCell != null && !roleCell.equals("")) {
                    if (!checkCellNamespace(roleCell)) {
                        namespaceRegistered = false;
                        if (!checkUriNamespaceResults.contains(roleCell)) {
                            checkUriNamespaceResults.add(roleCell);
                        }
                    } else if (!checkCellLabel(roleCell)) {
                        hasLabel = false;
                        if (!checkUriLabelResults.contains(roleCell)) {
                            checkUriLabelResults.add(roleCell);
                        }
                    } 
                }
                if (relationCell != null && !relationCell.equals("")) {
                    if (!checkCellNamespace(relationCell)) {
                        namespaceRegistered = false;
                        if (!checkUriNamespaceResults.contains(relationCell)) {
                            checkUriNamespaceResults.add(relationCell);
                        }
                    } else if (!checkCellLabel(relationCell)) {
                        hasLabel = false;
                        if (!checkUriLabelResults.contains(relationCell)) {
                            checkUriLabelResults.add(relationCell);
                        }
                    } 
                }

                /* 
                 *  Check if the values of attribute cells are subclasses of  
                 *  study indicators
                 */

                if (URIUtils.isValidURI(attributeCell)) {
                    isIndicator = checkIndicatorPath(attributeCell);
                    if (!isIndicator) {
                        //System.out.println("Adding " + attributeCell);
                        checkStudyIndicatorPathResults.add(attributeCell);
                    }
                } else {
                    if (entityCell == null || entityCell.length() == 0) {
                        isIndicator = false;
                        if (attributeCell != null && !attributeCell.isEmpty()) {
                            checkStudyIndicatorPathResults.add(attributeCell);
                        }
                    }
                }

                /* 
                 *  Check if the values of attributeOf cells references to   
                 *  objects defined in the SDD
                 */

                if (attributeCell != null && attributeCell.length() > 0) {
                    dasaList.add(record.getValueByColumnIndex(0));
                    if (attributeOfCell.length() > 0) {
                        sa2so.put(record.getValueByColumnIndex(0), attributeOfCell);
                    } else {
                        dataFile.getLogger().printExceptionByIdWithArgs("SDD_00008", record.getValueByColumnIndex(0));
                    }
                }

                if (entityCell != null && entityCell.length() > 0) {
                    if (URIUtils.isValidURI(entityCell)) {
                        dasoList.add(record.getValueByColumnIndex(0));
                        if (inRelationToCell.length() > 0) {
                            so2so2.put(record.getValueByColumnIndex(0), inRelationToCell);
                        } else {

                        }

                        so2type.put(record.getValueByColumnIndex(0), entityCell);

                        if (roleCell.length() > 0) {
                            so2role.put(record.getValueByColumnIndex(0), roleCell);
                        } else {

                        }

                        if (dfCell.length() > 0) {
                            so2df.put(record.getValueByColumnIndex(0), dfCell);
                        } else {

                        }
                    } else if (codeMappings.containsKey(entityCell)) {
                        if (URIUtils.isValidURI(codeMappings.get(entityCell))) {
                            dasoList.add(record.getValueByColumnIndex(0));
                            if (inRelationToCell.length() > 0) {
                                so2so2.put(record.getValueByColumnIndex(0), inRelationToCell);
                            } else {

                            }

                            so2type.put(record.getValueByColumnIndex(0), codeMappings.get(entityCell));

                            if (roleCell.length() > 0) {
                                so2role.put(record.getValueByColumnIndex(0), roleCell);
                            } else {

                            }

                            if (dfCell.length() > 0) {
                                so2df.put(record.getValueByColumnIndex(0), dfCell);
                            } else {

                            }
                        }
                    } else {
                        dataFile.getLogger().printExceptionByIdWithArgs("SDD_00009", entityCell);
                        return false;
                    }
                }

                if (checkCellValue(record.getValueByColumnName("attributeOf"))) {
                    mapAttrObj.put(record.getValueByColumnIndex(0), record.getValueByColumnName("attributeOf"));
                } else {
                    dataFile.getLogger().printExceptionByIdWithArgs("SDD_00010", record.getValueByColumnName("attributeOf"), rowNumber);
                    return false;
                }

            } else {
                dataFile.getLogger().printExceptionByIdWithArgs("SDD_00011", record.getValueByColumnName("Column"));
                return false;
            }

            try {
                mapAttrObj.put(record.getValueByColumnName(getTemplates().getLABEL()),
                        record.getValueByColumnName(getTemplates().getATTTRIBUTEOF()));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        //System.out.println("SDD: read Data Dictionary 3");

        if (checkUriNamespaceResults.size() > 0) {
            dataFile.getLogger().printExceptionByIdWithArgs("SDD_00012", String.join(", ", checkUriNamespaceResults));
            return false;
        }

        if (checkUriLabelResults.size() > 0) {
            dataFile.getLogger().printWarningByIdWithArgs("SDD_00013", String.join(", ", checkUriLabelResults));
        }

        if (checkStudyIndicatorPathResults.size() > 0) {
            dataFile.getLogger().printWarningByIdWithArgs("SDD_00014", String.join(", ", checkStudyIndicatorPathResults));
        }

        if (checkCellValResults.size() > 0) {
            dataFile.getLogger().printExceptionByIdWithArgs("SDD_00015", String.join(", ", checkCellValResults));
            return false;
        }

        //System.out.println("SDD: read Data Dictionary 4");

        if (namespaceRegistered == true) {
            dataFile.getLogger().println("The Dictionary Mapping's namespaces are all registered.");
        }
        if (hasLabel == true) {
            dataFile.getLogger().println("The Dictionary Mapping's terms have labels.");
        }
        if (isIndicator == true) {
            dataFile.getLogger().println("The Dictionary Mapping's attributes are all subclasses of hasco:StudyIndicator or hasco:SampleIndicator.");
        }

        dataFile.getLogger().println("The Dictionary Mapping has correct content under \"Column\" and \"attributeOf\" columns.");
        //System.out.println("[SDD] mapAttrObj: " + mapAttrObj);

        return true;
    }

    public boolean readCodeMapping(RecordFile file) {
        if (!file.isValid()) {
            return false;
        }

        for (Record record : file.getRecords()) {
            String code = record.getValueByColumnName("code");
            String uri = record.getValueByColumnName("uri");
            if (uri.startsWith("obo:UO_")) {
                uri = uri.replace("obo:UO_", "uo:");
            }
            //System.out.println("code mappings: code=[" + code + "] uri=[" + uri + "]");
            codeMappings.put(code, URIUtils.replacePrefixEx(uri));
        }

        if (codeMappings.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean readCodebook(RecordFile file) {
        if (!file.isValid()) {
            return false;
        }

        for (Record record : file.getRecords()) {
            //record.getValueByColumnName(getTemplates().getLABEL());

            if (!record.getValueByColumnName(getTemplates().getLABEL()).isEmpty()) {
                String colName = record.getValueByColumnName(getTemplates().getLABEL());
                Map<String, String> mapCodeClass = null;
                if (!codebook.containsKey(colName)) {
                    mapCodeClass = new HashMap<String, String>();
                    codebook.put(colName, mapCodeClass);
                } else {
                    mapCodeClass = codebook.get(colName);
                }
                String classUri = "";
                if (!record.getValueByColumnName(getTemplates().getCLASS()).isEmpty()) {
                    classUri = URIUtils.replacePrefixEx(record.getValueByColumnName(getTemplates().getCLASS()));
                }
                mapCodeClass.put(record.getValueByColumnName(getTemplates().getCODE()), classUri);
            }
        }

        if (codebook.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean readTimeline(RecordFile file) {
        if (!file.isValid()) {
            return false;
        }

        for (Record record : file.getRecords()) {
            if (record.getValueByColumnName("Name") != null && !record.getValueByColumnName("Name").isEmpty()) {
                String primaryKey = record.getValueByColumnName("Name");

                Map<String, String> timelineRow = new HashMap<String, String>();
                List<String> colNames = new ArrayList<String>();
                colNames.add("Label");
                colNames.add("Type");
                colNames.add("Start");
                colNames.add("End");
                colNames.add("Unit");
                colNames.add("inRelationTo");

                for (String col : colNames) {
                    if (!record.getValueByColumnName(col).isEmpty()) {
                        timelineRow.put(col, record.getValueByColumnName(col));
                    } else {
                        timelineRow.put(col, "null");
                    }
                }
                timeline.put(primaryKey, timelineRow);
            }
        }
        if (timeline.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public List<Map<String, List<String>>> readDDforEAmerge(RecordFile file) {

        //System.out.println("SDD: initiating readDDforEAmerge()");
        Map<String, List<String>> mapEAmerge = new HashMap<String, List<String>>();
        Map<String, List<String>> mapAAmerge = new HashMap<String, List<String>>();
        List<Map<String, List<String>>> response = new ArrayList<Map<String, List<String>>>(); 
        response.add(mapEAmerge);
        response.add(mapAAmerge);
        Map<String, List<String>> dasaContent = new HashMap<String, List<String>>();

        if (!file.isValid()) {
            return response;
        }

        for (Record record : file.getRecords()) {
            String columnCell = record.getValueByColumnIndex(0);
            if (checkCellValue(record.getValueByColumnIndex(0))) {
                if (record.getValueByColumnName("Attribute") != null
                        && record.getValueByColumnName("Attribute").length() > 0) {
                    if (!dasaContent.containsKey(record.getValueByColumnIndex(0))) {
                        List<String> dasa_entry = new ArrayList<String>();
                        dasa_entry.add(record.getValueByColumnName("Attribute"));
                        dasa_entry.add(record.getValueByColumnName("attributeOf"));
                        dasaContent.put(record.getValueByColumnIndex(0),dasa_entry);
                    }
                }
            }
        }

        for (Record record : file.getRecords()) {

            if (checkCellValue(record.getValueByColumnIndex(0))) {

                String columnCell = record.getValueByColumnIndex(0);
                String labelCell = record.getValueByColumnName("Label");
                String attrCell = record.getValueByColumnName("Attribute");
                String unitCell = record.getValueByColumnName("Unit");
                String timeCell = record.getValueByColumnName("Time");
                String entityCell = record.getValueByColumnName("Entity");
                String inRelationToCell = record.getValueByColumnName("inRelationTo");
                String attributeOfCell = record.getValueByColumnName("attributeOf");
                String dfCell = record.getValueByColumnName("wasDerivedFrom");

                if (dasaContent.containsKey(columnCell) && (attributeOfCell.startsWith("??"))) {

                    //System.out.println("listEAmergeTrigger: " + columnCell + " " + attributeOfCell);

                    List<String> listEAmerge = new ArrayList<String>();

                    if (dasaContent.containsKey(attributeOfCell)) {

                        // location [0] in the list
                        if (columnCell != null) {
                            listEAmerge.add(columnCell);
                        } else {
                            listEAmerge.add("");
                        }

                        // location [1] in the list
                        if (labelCell != null) {
                            listEAmerge.add(labelCell);
                        } else {
                            listEAmerge.add("");
                        }

                        // location [2] in the list
                        if (unitCell != null) {
                            listEAmerge.add(unitCell);
                        } else {
                            listEAmerge.add("");
                        }

                        // location [3] in the list
                        if (timeCell != null) {
                            listEAmerge.add(timeCell);
                        } else {
                            listEAmerge.add("");
                        }

                        // starting from location [4] in the list: attributes
                        if (attrCell != null) {
                            listEAmerge.add(attrCell);
                        } else {
                            listEAmerge.add("");
                        }

                        // continue to check/allow arbitrary level of attributeOf
                        String currentAttributeOfCell = attributeOfCell;
                        while ( dasaContent.containsKey(currentAttributeOfCell) ) {
                            if (dasaContent.get(currentAttributeOfCell).get(0) != null) {
                                listEAmerge.add(dasaContent.get(currentAttributeOfCell).get(0));
                            } else {
                                listEAmerge.add("");
                            }
                            currentAttributeOfCell = dasaContent.get(currentAttributeOfCell).get(1);
                        }

                        // done with the current one
                        mapEAmerge.put(attributeOfCell, listEAmerge);

                    }
                    //System.out.println("listEAmerge :" + listEAmerge);
                } else if (dasaContent.containsKey(columnCell)) {

                    List<String> listAAmerge = new ArrayList<String>();
                    String currentAttributeOfCell = attributeOfCell;

                    if (dasaContent.containsKey(currentAttributeOfCell)) {

                        listAAmerge.add(attrCell);  // add the starting attribute first

                        // allowing arbitrary levels
                        while ( dasaContent.containsKey(currentAttributeOfCell) ) {

                            /*if (attributeOfCell != null) {
                                listAAmerge.add(currentAttributeOfCell);
                            } else {
                                listAAmerge.add("");
                            }*/

                            if (dasaContent.get(currentAttributeOfCell).get(0) != null) {
                                listAAmerge.add(dasaContent.get(currentAttributeOfCell).get(0));
                            } else {
                                listAAmerge.add("");
                            }

                            /*if (dasaContent.get(currentAttributeOfCell).get(1) != null) {
                                listAAmerge.add(dasaContent.get(currentAttributeOfCell).get(1));
                            } else {
                                listAAmerge.add("");
                            }*/

                            currentAttributeOfCell = dasaContent.get(currentAttributeOfCell).get(1);
                        }
                        // add the final entity (after arbitrary levels) this SDDA applies to
                        listAAmerge.add(currentAttributeOfCell);

                        mapAAmerge.put(columnCell, listAAmerge);
                    }
                }
            }
        }

        //System.out.println("SDD: l_dasa :" + l_dasa);
        //System.out.println("SDD: mapEAmerge :" + mapEAmerge.keySet());
        return response;
    }

    /************************************************** 
     * 
     *        METHODS  --  CHECK SDD COMPONENT
     * 
     **************************************************/

    public boolean checkCellValue(String str) {
        if (str == null) {
            return false;
        }
        if (str.contains(",")) {
            return false;
        }
        if (str.trim().contains(" ")) {
            return false;
        }
        return true;
    }

    public boolean checkCellNamespace(String str) {
        if (str == null) {
            return true;
        }
        String prefixString = NameSpaces.getInstance().printSparqlNameSpaceList();
        if (str.contains(":")) {
            String[] split = str.split(":");
            String prefixname = split[0];
            if (!prefixString.contains(prefixname)) {
                return false;
            }
        } 
        return true;
    }

    public boolean checkCellLabel(String str) {
        if (str == null) {
            return true;
        }
        if (!str.contains(":")) {
            return true;
        }
        if (METASDDA.contains(str)) {
            return true;
        }

        String foundLabel = FirstLabel.getLabel(URIUtils.replacePrefixEx(str));
        return (foundLabel != null && !foundLabel.equals(""));
    }

    public boolean checkIndicatorPath(String str) {

        if (METASDDA.contains(str)) {
            return true;
        }

        String expanded = URIUtils.replacePrefixEx(str);
        String indvIndicatorQuery = "";
        String STUDY_INDICATOR = URIUtils.replacePrefixEx("hasco:StudyIndicator");
        String SAMPLE_INDICATOR = URIUtils.replacePrefixEx("hasco:SampleIndicator");

        indvIndicatorQuery += NameSpaces.getInstance().printSparqlNameSpaceList();
        indvIndicatorQuery += " SELECT * WHERE {  <" + expanded + "> rdfs:subClassOf*  ?super . }";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), indvIndicatorQuery);

        if (!resultsrw.hasNext()) {
            return false;
        }

        String superStr = "";

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            if (soln.get("super") != null) {
                superStr = soln.get("super").toString();
                // System.out.println("SDD:  Response for [" + expanded + "] is [" + superStr + "]");
                if (superStr.equals(STUDY_INDICATOR) || superStr.equals(SAMPLE_INDICATOR)) {
                    return true;
                }
            }

        }

        // System.out.println("SDD: [WARNING] " + expanded + " is not an indicator");
        return false;
    }

}
