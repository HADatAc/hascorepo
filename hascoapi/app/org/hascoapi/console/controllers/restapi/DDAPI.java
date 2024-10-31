package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import org.hascoapi.Constants;
import org.hascoapi.entity.pojo.DD;
import org.hascoapi.transform.Renderings;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.HAScOMapper;
import org.hascoapi.vocabularies.SIO;
import org.hascoapi.vocabularies.HASCO;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.hascoapi.Constants.TEST_UNIT_URI;

public class DDAPI extends Controller {

    private Result createDDResult(DD dd) {
        dd.save();
        return ok(ApiUtil.createResponse("DD <" + dd.getUri() + "> has been CREATED.", true));
    }

    public Result createDD(String json) {
        if (json == null || json.equals("")) {
            return ok(ApiUtil.createResponse("No json content has been provided.", false));
        }
        //System.out.println("(UnitAPI) Value of json in createUnit: [" + json + "]");
        ObjectMapper objectMapper = new ObjectMapper();
        DD newDD;
        try {
            //convert json string to Unit unitance
            newDD  = objectMapper.readValue(json, DD.class);
        } catch (Exception e) {
            //System.out.println("(UnitAPI) Failed to parse json for [" + json + "]");
            return ok(ApiUtil.createResponse("Failed to parse json.", false));
        }
        return createDDResult(newDD);
    }

    public static Result getDDs(List<DD> results){
        if (results == null) {
            return ok(ApiUtil.createResponse("No DD has been found", false));
        } else {
            //for (DD dd: results) {
            //    System.out.println(dd.getLabel() + "  [" + dd.getHasDataFile() + "]");
            //}
            ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,HASCO.DD);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            //System.out.println(org.hascoapi.console.controllers.restapi.URIPage.prettyPrintJsonString(jsonObject));
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }



}
