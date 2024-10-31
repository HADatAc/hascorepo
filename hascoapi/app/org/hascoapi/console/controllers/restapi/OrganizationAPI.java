package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import org.hascoapi.Constants;
import org.hascoapi.entity.pojo.Organization;
import org.hascoapi.entity.pojo.Person;
import org.hascoapi.entity.pojo.Place;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.HAScOMapper;
import org.hascoapi.vocabularies.HASCO;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class OrganizationAPI extends Controller {

    public static Result getOrganizations(List<Organization> results){
        if (results == null) {
            return ok(ApiUtil.createResponse("No Organization has been found", false));
        } else {
            ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,HASCO.STUDY);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }

    public Result findSubOrganizations(String uri, int pageSize, int offset) {
        //System.out.println("OrganizationAPI: " + uri);
        List<Organization> results = Organization.findSubOrganizations(uri, pageSize, offset);
        return OrganizationAPI.getOrganizations(results);
       
    }

    public Result findTotalSubOrganizations(String uri) {
        int totalElements = Organization.findTotalSubOrganizations(uri);
        if (totalElements >= 0) {
            String totalElementsJSON = "{\"total\":" + totalElements + "}";
            return ok(ApiUtil.createResponse(totalElementsJSON, true));
        }     
        return ok(ApiUtil.createResponse("Query method findTotalSubOrganizations() failed to retrieve total number of element", false));   
    }

    public Result findAffiliations(String uri, int pageSize, int offset) {
        List<Person> results = Organization.findAffiliations(uri, pageSize, offset);
        return PersonAPI.getPeople(results);
       
    }

    public Result findTotalAffiliations(String uri) {
        int totalElements = Organization.findTotalAffiliations(uri);
        if (totalElements >= 0) {
            String totalElementsJSON = "{\"total\":" + totalElements + "}";
            return ok(ApiUtil.createResponse(totalElementsJSON, true));
        }     
        return ok(ApiUtil.createResponse("Query method findTotalAffiliations() failed to retrieve total number of element", false));   
    }

}
