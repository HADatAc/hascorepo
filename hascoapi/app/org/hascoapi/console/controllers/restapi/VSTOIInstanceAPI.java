package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import org.hascoapi.Constants;
import org.hascoapi.entity.pojo.DetectorInstance;
import org.hascoapi.entity.pojo.GenericFind;
import org.hascoapi.entity.pojo.INS;
import org.hascoapi.entity.pojo.InstrumentInstance;
import org.hascoapi.entity.pojo.PlatformInstance;
import org.hascoapi.entity.pojo.StudyObject;
import org.hascoapi.transform.Renderings;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.HAScOMapper;
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.vocabularies.VSTOI;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class VSTOIInstanceAPI extends Controller {

    public static Result getInstrumentInstances(List<InstrumentInstance> results){
        if (results == null) {
            return ok(ApiUtil.createResponse("No InstrumentInstance has been found", false));
        } else {
            ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,VSTOI.INSTRUMENT_INSTANCE);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }

    public static Result getDetectorInstances(List<DetectorInstance> results){
        if (results == null) {
            return ok(ApiUtil.createResponse("No DetectorInstance has been found", false));
        } else {
            ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,VSTOI.DETECTOR_INSTANCE);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }

    public static Result getPlatformInstances(List<PlatformInstance> results){
        if (results == null) {
            return ok(ApiUtil.createResponse("No PlatformInstance has been found", false));
        } else {
            ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,VSTOI.PLATFORM_INSTANCE);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }

    public Result findPlatformInstancesByPlatformWithPage(String platformUri, int pagesize, int offset) {
        if (platformUri == null || platformUri.isEmpty()) {
            return ok(ApiUtil.createResponse("No platform uri has been provided", false));
        }
        System.out.println(platformUri);
        List<PlatformInstance> results = PlatformInstance.findByPlaformWithPage(platformUri, pagesize, offset);
        return this.getPlatformInstances(results);
    }

    public Result findTotalPlatformInstancesByPlatform(String platformUri){
        if (platformUri == null || platformUri.isEmpty()) {
            return ok(ApiUtil.createResponse("No platform uri has been provided", false));
        }
        int totalElements = totalElements = PlatformInstance.findTotalByPlatform(platformUri);
        if (totalElements >= 0) {
            String totalElementsJSON = "{\"total\":" + totalElements + "}";
            return ok(ApiUtil.createResponse(totalElementsJSON, true));
        }
        return ok(ApiUtil.createResponse("query method findTotalPlatformInstancesByPlatform() failed to retrieve total number of element", false));
    }

}
