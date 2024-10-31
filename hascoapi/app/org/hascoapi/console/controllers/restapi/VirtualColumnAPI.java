package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import org.hascoapi.Constants;
import org.hascoapi.entity.pojo.Place;
import org.hascoapi.entity.pojo.VirtualColumn;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.HAScOMapper;
import org.hascoapi.vocabularies.HASCO;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class VirtualColumnAPI extends Controller {

    public static Result getVirtualColumns(List<VirtualColumn> results){
        if (results == null) {
            return ok(ApiUtil.createResponse("No VirtualColumn has been found", false));
        } else {
            ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,HASCO.VIRTUAL_COLUMN);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            //System.out.println("VirtualColumnAPI: json=[" + ApiUtil.createResponse(jsonObject, true) + "]");
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }

    public Result getVCsByStudy(String studyUri){
        List<VirtualColumn> results = VirtualColumn.findVCsByStudy(studyUri);
        return getVirtualColumns(results);
    }

    public Result findTotalVCsByStudy(String studyuri) {
        int totalElements = VirtualColumn.findTotalVCsByStudy(studyuri);
        if (totalElements >= 0) {
            String totalElementsJSON = "{\"total\":" + totalElements + "}";
            return ok(ApiUtil.createResponse(totalElementsJSON, true));
        }     
        return ok(ApiUtil.createResponse("Query method findTotalVCsByStudy() failed to retrieve total number of VCs by study", false));   
    }

}
