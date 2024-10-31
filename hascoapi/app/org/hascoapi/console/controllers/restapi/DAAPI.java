package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import org.hascoapi.Constants;
import org.hascoapi.entity.pojo.DA;
import org.hascoapi.transform.Renderings;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.HAScOMapper;
import org.hascoapi.vocabularies.SIO;
import org.hascoapi.vocabularies.HASCO;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class DAAPI extends Controller {

    private Result createDAResult(DA da) {
        da.save();
        return ok(ApiUtil.createResponse("DA <" + da.getUri() + "> has been CREATED.", true));
    }

    public Result createDA(String json) {
        if (json == null || json.equals("")) {
            return ok(ApiUtil.createResponse("No json content has been provided.", false));
        }
        //System.out.println("(UnitAPI) Value of json in createUnit: [" + json + "]");
        ObjectMapper objectMapper = new ObjectMapper();
        DA newDA;
        try {
            //convert json string to Unit unitance
            newDA  = objectMapper.readValue(json, DA.class);
        } catch (Exception e) {
            //System.out.println("(UnitAPI) Failed to parse json for [" + json + "]");
            return ok(ApiUtil.createResponse("Failed to parse json.", false));
        }
        return createDAResult(newDA);
    }

    public static Result getDAs(List<DA> results){
        if (results == null) {
            return ok(ApiUtil.createResponse("No DA has been found", false));
        } else {
            //for (DD dd: results) {
            //    System.out.println(dd.getLabel() + "  [" + dd.getHasDataFile() + "]");
            //}
            ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,HASCO.DATA_ACQUISITION);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }

}
