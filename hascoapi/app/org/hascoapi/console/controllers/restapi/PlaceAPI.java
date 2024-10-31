package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import org.hascoapi.Constants;
import org.hascoapi.entity.pojo.Place;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.HAScOMapper;
import org.hascoapi.vocabularies.SCHEMA;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class PlaceAPI extends Controller {

    public static Result getPlaces(List<Place> results){
        if (results == null) {
            return ok(ApiUtil.createResponse("No Place has been found", false));
        } else {
            ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,SCHEMA.PLACE);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }

    public Result findContainsPlace(String uri, int pageSize, int offset) {
        List<Place> results = Place.findContainsPlace(uri, pageSize, offset);
        return PlaceAPI.getPlaces(results);
       
    }

    public Result findTotalContainsPlace(String uri) {
        int totalElements = Place.findTotalContainsPlace(uri);
        if (totalElements >= 0) {
            String totalElementsJSON = "{\"total\":" + totalElements + "}";
            return ok(ApiUtil.createResponse(totalElementsJSON, true));
        }     
        return ok(ApiUtil.createResponse("Query method findTotalContainsPlace() failed to retrieve total number of element", false));   
    }



}
