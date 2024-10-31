package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import org.hascoapi.Constants;
import org.hascoapi.entity.pojo.INS;
import org.hascoapi.transform.Renderings;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.HAScOMapper;
import org.hascoapi.vocabularies.SIO;
import org.hascoapi.vocabularies.HASCO;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class INSAPI extends Controller {

    private Result createINSResult(INS ins) {
        ins.save();
        return ok(ApiUtil.createResponse("INS <" + ins.getUri() + "> has been CREATED.", true));
    }

    public Result createINS(String json) {
        if (json == null || json.equals("")) {
            return ok(ApiUtil.createResponse("No json content has been provided.", false));
        }
        //System.out.println("(UnitAPI) Value of json in createUnit: [" + json + "]");
        ObjectMapper objectMapper = new ObjectMapper();
        INS newINS;
        try {
            //convert json string to Unit unitance
            newINS  = objectMapper.readValue(json, INS.class);
        } catch (Exception e) {
            //System.out.println("(UnitAPI) Failed to parse json for [" + json + "]");
            return ok(ApiUtil.createResponse("Failed to parse json.", false));
        }
        return createINSResult(newINS);
    }

    public static Result getINSs(List<INS> results){
        if (results == null) {
            return ok(ApiUtil.createResponse("No INS has been found", false));
        } else {
            //for (INS ins: results) {
            //    System.out.println(ins.getLabel() + "  [" + ins.getHasDataFile() + "]");
            //}
            ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,HASCO.INS);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }

}
