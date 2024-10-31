package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import org.hascoapi.Constants;
import org.hascoapi.entity.pojo.DP2;
import org.hascoapi.transform.Renderings;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.HAScOMapper;
import org.hascoapi.vocabularies.SIO;
import org.hascoapi.vocabularies.HASCO;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class DP2API extends Controller {

    private Result createDP2Result(DP2 dp2) {
        dp2.save();
        return ok(ApiUtil.createResponse("DP2 <" + dp2.getUri() + "> has been CREATED.", true));
    }

    public Result createDP2(String json) {
        if (json == null || json.equals("")) {
            return ok(ApiUtil.createResponse("No json content has been provided.", false));
        }
        //System.out.println("(DP2API) Value of json in createUnit: [" + json + "]");
        ObjectMapper objectMapper = new ObjectMapper();
        DP2 newDP2;
        try {
            //convert json string to Unit unitance
            newDP2  = objectMapper.readValue(json, DP2.class);
        } catch (Exception e) {
            //System.out.println("(DP2API) Failed to parse json for [" + json + "]");
            return ok(ApiUtil.createResponse("Failed to parse json.", false));
        }
        return createDP2Result(newDP2);
    }

    public static Result getDP2s(List<DP2> results){
        if (results == null) {
            return ok(ApiUtil.createResponse("No DP2 has been found", false));
        } else {
            //for (DP2 dp2: results) {
            //    System.out.println(dp2.getLabel() + "  [" + dp2.getHasDataFile() + "]");
            //}
            ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,HASCO.DP2);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }

}
