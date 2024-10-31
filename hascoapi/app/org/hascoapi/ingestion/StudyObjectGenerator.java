package org.hascoapi.ingestion;

import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hascoapi.utils.ConfigProp;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.utils.Utils;
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.entity.pojo.DataFile;
import org.hascoapi.entity.pojo.HADatAcThing;
import org.hascoapi.entity.pojo.StudyObjectCollection;
import org.hascoapi.entity.pojo.StudyObject;


public class StudyObjectGenerator extends BaseGenerator {

    String study_id;
    String file_name;
    String soc_uri;
    String soc_type;
    String soc_scope;
    String soc_timescope;
    private Map<String, StudyObjectCollection> socMap = new HashMap<String, StudyObjectCollection>();
    String role;
    final String kbPrefix = ConfigProp.getKbPrefix();
    private List<String> listCache = new ArrayList<String>();
    private Map<String, String> uriMap = new HashMap<String, String>();
    private Map<String, List<String>> mapContent = new HashMap<String, List<String>>();

    public StudyObjectGenerator(DataFile dataFile, List<String> listContent, 
            Map<String, List<String>> mapContent, String study_uri, String study_id) {
        super(dataFile);
        //this.study_id = study_id; 
        //file_name = file.getFile().getName();
        file_name = fileName;
        //System.out.println("We are in StudyObject Generator!");
        //System.out.println("Study URI: " + study_uri);
        
        //study_id = file.getFile().getName().replaceAll("SSD-", "").replaceAll(".xlsx", "");
        //study_id = file_name.replaceAll("SSD-", "").replaceAll(".xlsx", "");
        this.study_id = study_id;
        
        setStudyUri(study_uri);       
        this.listCache = listContent;
        //System.out.println(listContent);
        this.mapContent = mapContent;
        this.soc_uri = Utils.uriPlainGen("studyobjectcollection", listContent.get(0));
        //System.out.println("oc_uri : " + oc_uri);
        this.soc_type = listContent.get(1);
        //System.out.println("oc_type : " + oc_type);
        this.soc_scope = listContent.get(2);
        //System.out.println("oc_scope : " + oc_scope);
        this.soc_timescope = listContent.get(3);
        //System.out.println("oc_timescope : " + oc_timescope);
        this.role = listContent.get(4);
        //System.out.println("role : " + role);
        uriMap.put("hasco:SubjectGroup", "SBJ-");
        uriMap.put("hasco:SampleCollection", "SPL-");
        uriMap.put("hasco:TimeCollection", "TIME-");
        uriMap.put("hasco:LocationCollection", "LOC-");
        uriMap.put("hasco:ObjectCollection", "OBJ-");
    }

    @Override
    public void initMapping() {
        mapCol.clear();
        mapCol.put("originalID", "originalID");
        mapCol.put("rdf:type", "rdf:type");
        mapCol.put("scopeID", "scopeID");
        mapCol.put("timeScopeID", "timeScopeID");
    }

    private String getUri() {
        //String originalID = rec.getValueByColumnName(mapCol.get("originalID"));
        // System.out.println("StudyObjectGenerator: " + originalID);
        //if (URIUtils.isValidURI(originalID)) {
            //System.out.println("StudyObjectGenerator: VALID URI");
        //    return URIUtils.replaceNameSpaceEx(originalID);
        //}

        // System.out.println("StudyObjectGenerator: " + kbPrefix + uriMap.get(oc_type) + originalID + "-" + study_id);
        //if ( uriMap.get(soc_type).contains("SBJ-") && UNIQUE_IDENTIFIER_ON) {
        //    return kbPrefix + uriMap.get(soc_type) + originalID;
        //} else return kbPrefix + uriMap.get(soc_type) + originalID + "-" + study_id;
        return Utils.uriGen("studyobject");
    }

    private String getType(Record rec) {
        return rec.getValueByColumnName(mapCol.get("rdf:type"));
    }

    private String getLabel(Record rec) {
        String originalID = rec.getValueByColumnName(mapCol.get("originalID"));
        if (URIUtils.isValidURI(originalID)) {
            return URIUtils.getBaseName(originalID);
        }
        
        if (getSoc() != null && getSoc().getRoleLabel() != null && !getSoc().getRoleLabel().equals("")) {
    		return getSoc().getRoleLabel() + " " + originalID;
    	}
        
        String auxstr = uriMap.get(soc_type);
        if (auxstr == null) {
            auxstr = "";
        } else {
            auxstr = auxstr.replaceAll("-","");
        }

        if (auxstr.contains("SBJ")) {
            return auxstr + " " + originalID;
        }
        return auxstr + " " + originalID + " - " + study_id;
    }

    private String getOriginalID(Record rec) {
        String auxstr = rec.getValueByColumnName(mapCol.get("originalID"));
        //System.out.println("StudyObjectGenerator: getOriginalID(1) = [" + auxstr + "]");
        if (auxstr == null) {
            return "";
        } 
        if (URIUtils.isValidURI(auxstr)) {
            return "";
        }
        auxstr = auxstr.replaceAll("\\s+","");
        //System.out.println("StudyObjectGenerator: getOriginalID(2) = [" + auxstr + "]");
        
        //auxstr = auxstr.replaceAll("(?<=^\\d+)\\.0*$", "");
        //System.out.println("StudyObjectGenerator: getOriginalID(3) = [" + auxstr + "]");
        return auxstr;
    }

    private String getSocUri() {
        return soc_uri;
    }

    
    private StudyObjectCollection getSoc() {
    	if (soc_uri == null || soc_uri.equals("")) {
    		return null;
    	}
    	if (socMap.containsKey(soc_uri)) {
    		return socMap.get(soc_uri);
    	}
    	StudyObjectCollection soc = StudyObjectCollection.find(Utils.uriPlainGen("studyobjectcollection", soc_uri));
    	socMap.put(soc_uri, soc);
    	return soc;
    }
    
    private String getScopeUri(Record rec) {
        if (soc_scope != null && !soc_scope.isEmpty()){
        	if (mapContent.get(soc_scope) != null) {
        		String scopeSOCtype = mapContent.get(soc_scope).get(1);
        		if (scopeSOCtype.toLowerCase().contains("SubjectGroup".toLowerCase())) {
                    return kbPrefix + uriMap.get(scopeSOCtype) + rec.getValueByColumnName(mapCol.get("scopeID")).replaceAll("(?<=^\\d+)\\.0*$", "");
                } else return kbPrefix + uriMap.get(scopeSOCtype) + rec.getValueByColumnName(mapCol.get("scopeID")).replaceAll("(?<=^\\d+)\\.0*$", "") + "-" + study_id;
            } else {
        		System.out.println("[ERROR] StudyObjectGenerator: no mapping for [" + soc_scope + "] in getScopeUri()");
        		return "";
        	}
        } else {
        	return "";
        }
    }

    private String getTimeScopeUri(Record rec) {
        if (soc_timescope != null && soc_timescope.length() > 0){
        	if (mapContent.get(soc_timescope) != null) {
        		String timeScopeSOCtype = mapContent.get(soc_timescope).get(1);
        		String returnedValue = rec.getValueByColumnName(mapCol.get("timeScopeID"));
        		// the value returned by getValueByColumnName may be an URI or an original.
        		if (URIUtils.isValidURI(returnedValue)) {
        			// if returned value is an URI, this function returns the URI with expanded namespace 
        			return URIUtils.replacePrefixEx(returnedValue);
        		} else {
        			// if returned value is not an URI, this function composes an URI according to SDD convention 
        			return kbPrefix + uriMap.get(timeScopeSOCtype) + returnedValue.replaceAll("(?<=^\\d+)\\.0*$", "") + "-" + study_id;
        		}
        	} else {
        		System.out.println("[ERROR] StudyObjectGenerator: no mapContent for [" + soc_timescope + "] in getTimeScopeUri(). Record is " + rec);
        		return "";
        	}
        } else {
            return "";
        }
    }
    
    public StudyObject createStudyObject(Record record) throws Exception {
    	if (getOriginalID(record) == null || getOriginalID(record).isEmpty()) {
    		return null;
    	}

    	StudyObject obj = new StudyObject(
            getUri(), 
            getType(record), 
            URIUtils.replacePrefixEx(HASCO.STUDY_OBJECT),
			getOriginalID(record), 
            getLabel(record), 
			getSocUri(), 
            getLabel(record),
            this.dataFile.getHasSIRManagerEmail());  // hasSIRManagerEmail
        obj.setRoleUri(URIUtils.replacePrefixEx(role));
        obj.addScopeUri(getScopeUri(record));
        //System.out.println("StudyObjectGenerator: createStudyObject calling getTimeScopeUri. original ID [" + getOriginalID(record) + "]");
        //System.out.println("StudyObjectGenerator: value of time scope [" + record.getValueByColumnName(mapCol.get("timeScopeID")).replaceAll("(?<=^\\d+)\\.0*$", "") + "]");
        //System.out.println("StudyObjectGenerator: value of oc_timescope [" + oc_timescope + "]");
        //System.out.println("StudyObjectGenerator: value of mapContent.get(oc_timescope) [" + mapContent.get(oc_timescope) + "]");
        List<String> l = new ArrayList<String>(mapContent.keySet());
        //for (String str : l) {
        //	System.out.println("StudyObjectGenerator: mapContent's key [" + str + "]");
        //}
        obj.addTimeScopeUri(getTimeScopeUri(record));
        
        return obj;
    }

    @Override
    public HADatAcThing createObject(Record rec, int rowNumber, String selector) throws Exception {
        return createStudyObject(rec);
    }

    @Override
    public Map<String, Object> createRow(Record rec, int rowNumber) throws Exception {
        if (getOriginalID(rec).length() > 0) {
            Map<String, Object> row = new HashMap<String, Object>();
            row.put("hasURI", getUri());
            return row;
        }
        
        return null;
    }

    @Override
    public void preprocess() throws Exception {}

    @Override
    public String getTableName() {
        return "StudyObject";
    }

    @Override
    public String getErrorMsg(Exception e) {
        return "Error in StudyObjectGenerator: " + e.getMessage();
    }
}
