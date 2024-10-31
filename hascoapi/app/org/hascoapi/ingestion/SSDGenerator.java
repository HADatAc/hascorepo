package org.hascoapi.ingestion;

import java.lang.String;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.hascoapi.entity.pojo.DataFile;
import org.hascoapi.entity.pojo.HADatAcThing;
import org.hascoapi.entity.pojo.StudyObjectCollection;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.utils.Utils;
import org.hascoapi.vocabularies.HASCO;

public class SSDGenerator extends BaseGenerator {

    String SDDName = ""; //used for reference column uri

    public SSDGenerator(DataFile dataFile) {
        super(dataFile);
        //this.SDDName = dataFile.getBaseName().replaceAll("SSD-", "");
        
        if (records.get(0) != null) {
            studyUri = URIUtils.replacePrefixEx(getUri(records.get(0)));
        } else {
            studyUri = "";
        }
    }

    @Override
    public void initMapping() {
        mapCol.clear();
        mapCol.put("sheet", "sheet");
        mapCol.put("uri", "hasURI");
        mapCol.put("typeUri", "type");
        mapCol.put("hasSOCReference", "hasSOCReference");
        mapCol.put("hasRoleLabel", "hasRoleLabel");
        mapCol.put("label", "label");
        mapCol.put("hasScopeUri", "hasScope");
        //mapCol.put("groundingLabel", "groundingLabel");
        mapCol.put("spaceScopeUris", "hasSpaceScope");
        mapCol.put("timeScopeUris", "hasTimeScope");
        mapCol.put("groupUris", "hasGroup");
    }

    private String getUri(Record rec) {
        return Utils.uriPlainGen("studyobjectcollection", rec.getValueByColumnName(mapCol.get("uri")));
    }

    private String getTypeUri(Record rec) {
        return rec.getValueByColumnName(mapCol.get("typeUri"));
    }

    private String getLabel(Record rec) {
        return rec.getValueByColumnName(mapCol.get("label"));
    }

    private String getVirtualColumnUri(Record rec) {
        String vcUri= 
            studyUri.replace("ST", "VC") + "-" + 
            getSOCReference(rec).replace("??", "");
        return vcUri;
    }
    
    private String getSOCReference(Record rec) {
        String ref = rec.getValueByColumnName(mapCol.get("hasSOCReference"));
        return ref.trim().replace(" ", "").replace("_", "-");
    }

    private String getRoleLabel(Record rec) {
        if (mapCol.get("hasRoleLabel") == null) {
            return "";
        }
        String ref = rec.getValueByColumnName(mapCol.get("hasRoleLabel"));
        if (ref == null) {
            return "";
        }
        return ref.trim().replace(" ", "").replace("_", "-");
    }

    private String getHasScopeUri(Record rec) {
        if (rec.getValueByColumnName(mapCol.get("hasScopeUri")) == null ||
            rec.getValueByColumnName(mapCol.get("hasScopeUri")).isEmpty()) {
            return null;
        }
        return Utils.uriPlainGen("studyobjectcollection", rec.getValueByColumnName(mapCol.get("hasScopeUri")));
    }

    /*private String getGroundingLabel(Record rec) {
        return rec.getValueByColumnName(mapCol.get("groundingLabel"));
    }*/

    private List<String> getSpaceScopeUris(Record rec) {
        if (mapCol.get("spaceScopeUris") == null || rec.getValueByColumnName(mapCol.get("spaceScopeUris")) == null) {
            return new ArrayList<String>();
        }
        List<String> ans = Arrays.asList(rec.getValueByColumnName(mapCol.get("spaceScopeUris")).split(","))
                .stream()
                .map(s -> URIUtils.replacePrefixEx(s))
                .collect(Collectors.toList());
        List<String> uris = new ArrayList<String>();
        for (String item : ans) {
            if (item == null || item.isEmpty()) {
                uris.add(null);
            } else {
                uris.add(Utils.uriPlainGen("studyobjectcollection", item));
            }
        }
        return uris;
    }

    private List<String> getTimeScopeUris(Record rec) {
        System.out.println("getTimeScopeUris:  timeScopeUris is [" + mapCol.get("timeScopeUris") + "]");
        if (mapCol.get("timeScopeUris") == null || rec.getValueByColumnName(mapCol.get("timeScopeUris")) == null) {
            return new ArrayList<String>();
        }
        System.out.println("getTimeScopeUris: getValueByColumnName: [" + rec.getValueByColumnName(mapCol.get("timeScopeUris")) + "]");
        List<String> ans = Arrays.asList(rec.getValueByColumnName(mapCol.get("timeScopeUris")).split(","))
                .stream()
                .map(s -> URIUtils.replacePrefixEx(s))
                .collect(Collectors.toList());
        List<String> uris = new ArrayList<String>();
        for (String item : ans) {
            if (item == null || item.isEmpty()) {
                uris.add(null);
            } else {
                uris.add(Utils.uriPlainGen("studyobjectcollection", item));
            }
        }
        return uris;
    }

    private List<String> getGroupUris(Record rec) {
        if (mapCol.get("groupUris") == null || rec.getValueByColumnName(mapCol.get("groupUris")) == null) {
            return new ArrayList<String>();
        }
        List<String> ans = Arrays.asList(rec.getValueByColumnName(mapCol.get("groupUris")).split(","))
                .stream()
                .map(s -> URIUtils.replacePrefixEx(s))
                .collect(Collectors.toList());
        return ans;
    }

    public StudyObjectCollection createObjectCollection(Record record) throws Exception {

        String uri = this.getUri(record);
    	String typeUri = this.getTypeUri(record);

        // Skip the study row in the SSD sheet
    	//if (typeUri.equals("hasco:Study")) {
        //    return null;
        //}
        
        // Skip the SOC generator for columns with just VirtualColumn info (i.e., blank type and filled out SOC reference
        String SOCReference = getSOCReference(record);

        System.out.println("SSDGenerator: typeUri=[" + typeUri + "] SOCReference=[" + SOCReference + "]");

        if (typeUri.isEmpty() && SOCReference != null && !SOCReference.isEmpty()) {
            return null;
        }
        
        if (typeUri.isEmpty() && SOCReference.isEmpty()) {
        	return null;
        }
        
        if (this.studyUri == null || this.studyUri.isEmpty()) {
            logger.printExceptionByIdWithArgs("SSD_00001", typeUri);
            return null;
        }
            
        if (SOCReference == null || SOCReference.isEmpty()) {
            logger.printExceptionById("SSD_00002");
            return null;
        }

        String scopeUri = getHasScopeUri(record);
        if (scopeUri == null) {
            scopeUri = "";
        } else {
            scopeUri = URIUtils.replacePrefixEx(scopeUri);
        }

        StudyObjectCollection soc = new StudyObjectCollection(
                getUri(record),
                URIUtils.replacePrefixEx(typeUri),
                URIUtils.replacePrefixEx(HASCO.STUDY_OBJECT_COLLECTION),
                getLabel(record),
                getLabel(record),
                getStudyUri(),
                getVirtualColumnUri(record),
                getRoleLabel(record),
                this.dataFile.getHasSIRManagerEmail(),
                scopeUri,
                getSpaceScopeUris(record),
                getTimeScopeUris(record),
                getGroupUris(record),
                "0");
        
        System.out.println("New SOC: uri=[" + getUri(record) +
                "] label=[" + getLabel(record) + "]");

        return soc;
    }   
        
    @Override
    public void preprocess() throws Exception {}

    @Override
    public HADatAcThing createObject(Record rec, int rowNumber, String selector) throws Exception {
            if (!URIUtils.replacePrefixEx(getUri(rec)).equals(studyUri)) {
                HADatAcThing obj = createObjectCollection(rec);
                return obj;
            }
        return null;
    }

    @Override
    public String getErrorMsg(Exception e) {
        return "Error in SSDGenerator: " + e.getMessage();
    }

    @Override
    public String getTableName() {
        // TODO Auto-generated method stub
        return null;
    }
}
