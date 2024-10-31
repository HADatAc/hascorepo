package org.hascoapi.console.controllers.restapi;

import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import org.hascoapi.entity.pojo.Deployment;
import org.hascoapi.entity.pojo.HADatAcClass;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.HAScOMapper;
import org.hascoapi.utils.State;
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.vocabularies.VSTOI;
import play.mvc.Controller;
import play.mvc.Result;

public class HAScOClassAPI extends Controller {

    public static Result getClasses(List<HADatAcClass> results){

        //if (results == null) {
        //    return ok(ApiUtil.createResponse("No HADatAcClass has been found", false));
        //} else {
        if (results == null) {
            results = new ArrayList<HADatAcClass>();
        }
        ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,HASCO.HASCO_CLASS);
        JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
        return ok(ApiUtil.createResponse(jsonObject, true));
        //}
    }

    public Result getChildren(String superUri) {
        System.out.println("HAScOClassAPI.getChildren of [" + superUri + "]");
        if (superUri == null || superUri.isEmpty()) {
            return ok(ApiUtil.createResponse("No superclass URI has been provided to retrieve children of HADatAcClass", false));
        }
        return HAScOClassAPI.getClasses(HADatAcClass.getImmediateSubclasses(superUri));
    }

}
