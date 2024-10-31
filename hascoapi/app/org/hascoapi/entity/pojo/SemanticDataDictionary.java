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

@JsonFilter("semanticDataDictionaryFilter")
public class SemanticDataDictionary extends HADatAcThing {

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
    private static Map<String, SemanticDataDictionary> SemanticDataDictionaryCache;
    private List<SDDAttribute> attributesCache = new ArrayList<SDDAttribute>();
    private List<SDDObject> objectsCache = new ArrayList<SDDObject>();
    private List<PossibleValue> possibleValuesCache = new ArrayList<PossibleValue>();
    //private Map<String, Map<String, String>> possibleValuesCache = new HashMap<String, Map<String, String>>();
    //private DataFile sddfile = null;
    private IngestionLogger logger = null;
    //private Templates templates = null;

    @PropertyField(uri = "vstoi:hasStatus")    
    private String hasStatus;

    @PropertyField(uri = "vstoi:hasVersion")
    private String hasVersion;

    //@PropertyField(uri = "hasco:hasDataFile")
    //private String hasDataFileUri;

    @PropertyField(uri = "vstoi:hasSIRManagerEmail")
    private String hasSIRManagerEmail;

    /*
    @PropertyField(uri = "hasco:TimeStamp")
    private String timestampLabel = "";
    */

    /* 
    @PropertyField(uri = "sio:SIO_000418")
    private String timeInstantLabel = "";
    */

    /* 
    @PropertyField(uri = "hasco:namedTime")
    private String namedTimeLabel = "";
    */

    /* 
    @PropertyField(uri = "hasco:uriId")
    private String idLabel = "";
    */

    /* 
    @PropertyField(uri = "hasco:originalID")
    private String originalIdLabel = "";
    */

    /* 
    @PropertyField(uri = "hasco:hasElevation")
    private String elevationLabel = "";
    */

    /* 
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
    */
 
    /* 
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
        */

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
    private List<String> possibleValues = new ArrayList<String>();
    private boolean isRefreshed = false;

    /********************************* 
     * 
     *        CONSTRUCTS
     * 
     *********************************/

    public SemanticDataDictionary() {
        SemanticDataDictionary.getCache();
        getAttributes();
        getObjects();
        getPossibleValues();
    }

    public SemanticDataDictionary(DataFile dataFile) {
        this.uri = dataFile.getUri().replace("DF","SD");
        this.label = "";
        isRefreshed = false;
        SemanticDataDictionary.getCache();
        getAttributes();
        getObjects();
        getPossibleValues();
    }

    public SemanticDataDictionary(String uri, String label) {
        this.uri = uri;
        this.label = label;
        isRefreshed = false;
        SemanticDataDictionary.getCache();
        getAttributes();
        getObjects();
        getPossibleValues();
    }

    /*************************************** 
     * 
     *        METHODS  SETS/GETS
     * 
     ***************************************/

    private static Map<String, SemanticDataDictionary> getCache() {
        if (SemanticDataDictionaryCache == null) {
            SemanticDataDictionaryCache = new HashMap<String, SemanticDataDictionary>(); 
        } 
        return SemanticDataDictionaryCache;
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

    public String getHasVersion() {
        return hasVersion;
    }
    public void setHasVersion(String hasVersion) {
        this.hasVersion = hasVersion;
    }

    //public String getHasDataFile() {
    //    return hasDataFileUri;
    //}
    //public void setHasDataFile(String hasDataFileUri) {
    //    this.hasDataFileUri = hasDataFileUri;
    //}
    //public DataFile getDataFile() {
        //System.out.println("Inside SemanticDataDictionary.getDataFile(). hasDataFileuri is " + this.hasDataFileUri);
    //    DataFile dataFile = DataFile.find(this.hasDataFileUri);
    //    return dataFile;
    //}

    public String getHasStatus() {
        return hasStatus;
    }
    public void setHasStatus(String hasStatus) {
        this.hasStatus = hasStatus;
    }

    /* 
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
    */

    public String getHasSIRManagerEmail() {
        return hasSIRManagerEmail;
    }
    public void setHasSIRManagerEmail(String hasSIRManagerEmail) {
        this.hasSIRManagerEmail = hasSIRManagerEmail;
    }

    /* 
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
    */

    //public void setTemplates(String templateFile) {
    //    this.templates = new Templates(templateFile);
    //}

    public int getTotalVariables() {
        if (attributes == null) {
            return -1;
        }
        return attributes.size();
    }

    public int getTotalEvents() {
        if (events == null) {
            return -1;
        }
        return events.size();
    }

    public int getTotalObjects() {
        if (objects == null) {
            return -1;
        }
        return objects.size();
    }

    public int getTotalCodes() {
        if (possibleValues == null) {
            return -1;
        }
        return possibleValues.size();
    }

    public List<SDDAttribute> getAttributes() {
        if (attributesCache == null || attributesCache.isEmpty()) {
            attributesCache = SDDAttribute.findBySchema(getUri());
        }
        return attributesCache;
    }
    public void setAttributes(List<String> attributes) {
        if (attributes == null) {
            //System.out.println("[WARNING] No SDDObject for " + uri + " is defined in the knowledge base. ");
        } else {
            this.attributes = attributes;
            /* 
            if (!isRefreshed) {
                refreshAttributes();
            }
            */
        }
    }

    public List<SDDObject> getObjects() {
        if (objectsCache == null || objectsCache.isEmpty()) {
            objectsCache = SDDObject.findBySchema(getUri());
        }
        return objectsCache;
    }
    public void setObjects(List<String> objects) {
        if (objects == null) {
            //System.out.println("[WARNING] No SDDObject for " + uri + " is defined in the knowledge base. ");
        } else {
            this.objects = objects;
        }
    }

    public List<PossibleValue> getPossibleValues() {
        if (possibleValuesCache == null || possibleValuesCache.isEmpty()) {
            possibleValuesCache = PossibleValue.findBySchema(getUri());
        }
        return possibleValuesCache;
    }
    public void setPossibleValues(List<String> possibleValues) {
        if (possibleValues == null) {
            //System.out.println("[WARNING] No SDDObject for " + uri + " is defined in the knowledge base. ");
        } else {
            this.possibleValues = possibleValues;
            /* 
            if (!isRefreshed) {
                refreshAttributes();
            }
            */
        }
    }

    public SDDObject getObject(String sddoUri) {
        for (String sddo : objects) {
            if (sddo.equals(sddoUri)) {
                return SDDObject.find(sddo);
            }
        }
        return null;
    }

    public SDDObject getEvent(String sddeUri) {
        return SDDObject.find(sddeUri);
    }

    /*************************************** 
     * 
     *        METHODS  DIVERSE
     * 
     ***************************************/

    public static void resetCache() {
        SDDAttribute.resetCache();
        SDDObject.resetCache();
        PossibleValue.resetCache();
        SemanticDataDictionaryCache = null;
    }

    public void resetAttributesCache() {
        attributesCache = null;
    }

    public void resetObjectsCache() {
        objectsCache = null;
    }

    /* 
    public void refreshAttributes() {
        List<SDDAttribute> attributeList = SDDAttribute.findBySchema(this.getUri());
        if (attributes == null) {
            System.out.println("[ERROR] No SDDAttribute for " + uri + " is defined in the knowledge base. ");
        } else {
            for (SDDAttribute sdda : attributeList) {
                sdda.setSemanticDataDictionary(this);

                if (sdda.getAttributes().contains(URIUtils.replacePrefixEx("hasco:TimeStamp"))) {
                    setTimestampLabel(sdda.getLabel());
                    //System.out.println("[OK] SemanticDataDictionary TimeStampLabel: " + sdda.getLabel());
                }
                if (sdda.getAttributes().contains(URIUtils.replacePrefixEx("sio:SIO_000418"))) {
                    setTimeInstantLabel(sdda.getLabel());
                    //System.out.println("[OK] SemanticDataDictionary TimeInstantLabel: " + sdda.getLabel());
                }
                if (sdda.getAttributes().contains(URIUtils.replacePrefixEx("hasco:namedTime"))) {
                    setNamedTimeLabel(sdda.getLabel());
                    //System.out.println("[OK] SemanticDataDictionary NamedTimeLabel: " + sdda.getLabel());
                }
                if (sdda.getAttributes().contains(URIUtils.replacePrefixEx("hasco:uriId"))) {
                    setIdLabel(sdda.getLabel());
                    //System.out.println("[OK] SemanticDataDictionary IdLabel: " + sdda.getLabel());
                }
                if (sdda.getAttributes().contains(URIUtils.replacePrefixEx("hasco:LevelOfDetection"))) {
                    setLODLabel(sdda.getLabel());
                    //System.out.println("[OK] SemanticDataDictionary LODLabel: " + sdda.getLabel());
                }
                if (sdda.getAttributes().contains(URIUtils.replacePrefixEx("hasco:isGroupMember"))) {
                    setGroupLabel(sdda.getLabel());
                    //System.out.println("[OK] SemanticDataDictionary GroupLabel: " + sdda.getLabel());
                }
                if (sdda.getAttributes().contains(URIUtils.replacePrefixEx("hasco:matchesWith"))) {
                    setMatchingLabel(sdda.getLabel());
                    //System.out.println("[OK] SemanticDataDictionary MatchingLabel: " + sdda.getLabel());
                }
                if (sdda.getAttributes().contains(URIUtils.replacePrefixEx("hasco:originalID")) 
                        || sdda.getAttributes().equals(URIUtils.replacePrefixEx("sio:SIO_000115")) 
                        || Entity.getSubclasses(URIUtils.replacePrefixEx("hasco:originalID")).contains(sdda.getAttributes())) { 
                    setOriginalIdLabel(sdda.getLabel());
                    //System.out.println("[OK] SemanticDataDictionary IdLabel: " + sdda.getLabel());
                }
                if (sdda.getAttributes().contains(URIUtils.replacePrefixEx("hasco:hasEntity"))) {
                    setEntityLabel(sdda.getLabel());
                    //System.out.println("[OK] SemanticDataDictionary EntityLabel: " + sdda.getLabel());
                }
                if (!sdda.getInRelationToUri(URIUtils.replacePrefixEx("sio:SIO_000221")).isEmpty()) {
                    String uri = sdda.getInRelationToUri(URIUtils.replacePrefixEx("sio:SIO_000221"));
                    SDDObject sddoUnit = SDDObject.find(uri);
                    if (sddoUnit != null) {
                        setUnitLabel(sddoUnit.getLabel());
                    } else {
                        SDDAttribute sddaUnit = SDDAttribute.find(uri);
                        if (sddaUnit != null) {
                            setUnitLabel(sddaUnit.getLabel());
                        }
                    }
                }
                if (sdda.getAttributes().contains(URIUtils.replacePrefixEx("sio:SIO_000668"))) {
                    setInRelationToLabel(sdda.getLabel());
                }
            }
        }
    }
    */

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

    /*********************************************************** 
     * 
     *        METHODS  FIND (RETRIEVE SemanticDataDictionary)
     * 
     ***********************************************************/

    private static SemanticDataDictionary findCoreProperties(String uri) {
        Statement statement;
        RDFNode object;

        String queryString = "DESCRIBE <" + uri + ">";
        Model model = SPARQLUtils.describe(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.SPARQL_QUERY), queryString);

        StmtIterator stmtIterator = model.listStatements();

        if (!stmtIterator.hasNext()) {
            return null;
        }

        SemanticDataDictionary sdd = new SemanticDataDictionary();

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

    public static SemanticDataDictionary find(String sddUri) {
        //System.out.println("SemanticDataDictionary.find() with URI: " + sddUri);

        if (SemanticDataDictionary.getCache().get(sddUri) != null) {
        	
            SemanticDataDictionary sdd = SemanticDataDictionary.getCache().get(sddUri);
            sdd.getAttributes();
            return sdd;
        }

        //System.out.println("Looking for data acquisition sdd " + sddUri);

        if (sddUri == null || sddUri.equals("")) {
            System.out.println("[ERROR] at SemanticDataDictionary.java. URI blank or null.");
            return null;
        }

        SemanticDataDictionary sdd = SemanticDataDictionary.findCoreProperties(sddUri);

        if (sdd == null) {
            System.out.println("[ERROR] at SemanticDataDictionary.java. Could not find sdd for uri: <" + sddUri + ">");
        	return null;
        }

        sdd.setAttributes(SDDAttribute.findUriBySchema(sddUri));
        sdd.setObjects(SDDObject.findUriBySchema(sddUri));
        sdd.setPossibleValues(PossibleValue.findUriBySchema(sddUri));

        sdd.getAttributes();
        sdd.getObjects();
        sdd.getPossibleValues();
        SemanticDataDictionary.getCache().put(sddUri,sdd);
        return sdd;
    }

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

    /*************************************** 
     * 
     *        METHODS  SAVE/DELETE
     * 
     ***************************************/

    public static SemanticDataDictionary create(String uri) {
        SemanticDataDictionary sdd = new SemanticDataDictionary();
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
        List<SDDAttribute> attributes = SDDAttribute.findBySchema(uri);
        for (SDDAttribute sdda : attributes) {
            sdda.delete();
        }
        List<SDDObject> objects = SDDObject.findBySchema(uri);
        for (SDDObject sddo : objects) {
            sddo.delete();
        }
        List<PossibleValue> possibleValues = PossibleValue.findBySchema(uri);
        for (PossibleValue possibleValue : possibleValues) {
            possibleValue.delete();
        }
        super.deleteFromTripleStore();
        SemanticDataDictionary.resetCache();
    }

}
