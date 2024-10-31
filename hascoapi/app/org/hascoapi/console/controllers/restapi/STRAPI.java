package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import org.hascoapi.Constants;
import org.hascoapi.entity.pojo.STR;
import org.hascoapi.transform.Renderings;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.HAScOMapper;
import org.hascoapi.vocabularies.SIO;
import org.hascoapi.vocabularies.HASCO;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class STRAPI extends Controller {

    private Result createSTRResult(STR str) {
        str.save();
        return ok(ApiUtil.createResponse("STR <" + str.getUri() + "> has been CREATED.", true));
    }

    public Result createSTR(String json) {
        if (json == null || json.equals("")) {
            return ok(ApiUtil.createResponse("No json content has been provided.", false));
        }
        //System.out.println("(UnitAPI) Value of json in createUnit: [" + json + "]");
        ObjectMapper objectMapper = new ObjectMapper();
        STR newSTR;
        try {
            //convert json string to Unit unitance
            newSTR  = objectMapper.readValue(json, STR.class);
        } catch (Exception e) {
            //System.out.println("(UnitAPI) Failed to parse json for [" + json + "]");
            return ok(ApiUtil.createResponse("Failed to parse json.", false));
        }
        return createSTRResult(newSTR);
    }

    public static Result getSTRs(List<STR> results){
        if (results == null) {
            return ok(ApiUtil.createResponse("No STR has been found", false));
        } else {
            //for (STR str: results) {
            //    System.out.println(str.getLabel() + "  [" + str.getHasDataFile() + "]");
            //}
            ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,HASCO.STR);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }

}
