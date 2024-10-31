package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import org.hascoapi.Constants;
import org.hascoapi.entity.pojo.GenericFind;
import org.hascoapi.entity.pojo.StudyObject;
import org.hascoapi.entity.pojo.StudyObjectCollection;
import org.hascoapi.entity.pojo.VirtualColumn;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.HAScOMapper;
import org.hascoapi.vocabularies.HASCO;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class StudyObjectCollectionAPI extends Controller {

    public static Result getStudyObjectCollections(List<StudyObjectCollection> results){
        if (results == null) {
            return ok(ApiUtil.createResponse("No Study Object Collection has been found", false));
        } else {
            ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,HASCO.STUDY_OBJECT_COLLECTION);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }

    /**
     *   GET ELEMENTS BY MANAGER EMAIL AND SOC WITH PAGE
     */
    public Result getElementsByManagerEmailBySOC(String studyobjectcollectionuri, String elementtype, String manageremail, int pagesize, int offset) {
        if (manageremail == null || manageremail.isEmpty()) {
            return ok(ApiUtil.createResponse("No Manager Email has been provided", false));
        }
        if (elementtype.equals("studyobject")) {
            GenericFind<StudyObject> query = new GenericFind<StudyObject>();
            List<StudyObject> results = query.findByManagerEmailWithPagesBySOC(StudyObject.class, studyobjectcollectionuri, manageremail, pagesize, offset);
            return StudyObjectAPI.getStudyObjects(results);
        }  
        return ok("[getElementsByManagerEmailByStudy] No valid element type.");
    }

    public Result getTotalElementsByManagerEmailBySOC(String studyobjectcollectionuri, String elementtype, String manageremail){
        //System.out.println("SIRElementAPI: getTotalElementsByManagerEmailByStudy");
        if (elementtype == null || elementtype.isEmpty()) {
            return ok(ApiUtil.createResponse("No elementtype has been provided", false));
        }
        Class clazz = GenericFind.getElementClass(elementtype);
        if (clazz == null) {        
            return ok(ApiUtil.createResponse("[" + elementtype + "] is not a valid elementtype", false));
        }
        int totalElements = totalElements = GenericFind.findTotalByManagerEmailBySOC(clazz, studyobjectcollectionuri, manageremail);
        if (totalElements >= 0) {
            String totalElementsJSON = "{\"total\":" + totalElements + "}";
            return ok(ApiUtil.createResponse(totalElementsJSON, true));
        }
        return ok(ApiUtil.createResponse("query method getTotalElementsByManagerEmailBySOC() failed to retrieve total number of element", false));
    }

    public Result getSOCsByStudy(String studyUri){
        List<StudyObjectCollection> results = StudyObjectCollection.findStudyObjectCollectionsByStudy(studyUri);
        return getStudyObjectCollections(results);
    }

    public Result findTotalSOCsByStudy(String studyuri) {
        int totalElements = StudyObjectCollection.findTotalStudyObjectCollectionsByStudy(studyuri);
        if (totalElements >= 0) {
            String totalElementsJSON = "{\"total\":" + totalElements + "}";
            return ok(ApiUtil.createResponse(totalElementsJSON, true));
        }     
        return ok(ApiUtil.createResponse("Query method findTotalSOCsByStudy() failed to retrieve total number of SOCs by study", false));   
    }

}
