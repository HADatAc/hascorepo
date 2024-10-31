package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import org.hascoapi.Constants;
import org.hascoapi.entity.pojo.Place;
import org.hascoapi.entity.pojo.Organization;
import org.hascoapi.entity.pojo.Person;
import org.hascoapi.entity.pojo.PostalAddress;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.HAScOMapper;
import org.hascoapi.vocabularies.SCHEMA;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class PostalAddressAPI extends Controller {

    public static Result getPostalAddresses(List<PostalAddress> results){
        if (results == null) {
            return ok(ApiUtil.createResponse("No PostalAdress has been found", false));
        } else {
            JsonNode jsonObject = null;
            try {
                ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,SCHEMA.POSTAL_ADDRESS);
                jsonObject = mapper.convertValue(results, JsonNode.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }

    public Result findContainsPostalAddress(String placeuri, int pageSize, int offset) {
        List<PostalAddress> results = PostalAddress.findContainsPostalAddress(placeuri, pageSize, offset);
        return PostalAddressAPI.getPostalAddresses(results);
       
    }

    public Result findTotalContainsPostalAddress(String placeuri) {
        int totalElements = PostalAddress.findTotalContainsPostalAddress(placeuri);
        if (totalElements >= 0) {
            String totalElementsJSON = "{\"total\":" + totalElements + "}";
            return ok(ApiUtil.createResponse(totalElementsJSON, true));
        }     
        return ok(ApiUtil.createResponse("Query method findTotalContainsPostalAddress() failed to retrieve total number of element", false));   
    }

    public Result findContainsElement(String placeuri, String elementtype, int pageSize, int offset) {
        if (elementtype == null || elementtype.isEmpty()) {
            return ok(ApiUtil.createResponse("elementtype is required for method findContainsElement()", false));   
        }
        if (elementtype.equals("person")) {
            List<Person> results = PostalAddress.findContainsElement(placeuri, elementtype, pageSize, offset);
            return PersonAPI.getPeople(results);
        } else if (elementtype.equals("organization")) {
            List<Organization> results = PostalAddress.findContainsElement(placeuri, elementtype, pageSize, offset);
            return OrganizationAPI.getOrganizations(results);
        }
        return ok(ApiUtil.createResponse("invalid elementtype provided for method findContainsElement()", false));   
    }

    public Result findTotalContainsElement(String placeuri, String elementtype) {
        int totalElements = PostalAddress.findTotalContainsElement(placeuri, elementtype);
        if (totalElements >= 0) {
            String totalElementsJSON = "{\"total\":" + totalElements + "}";
            return ok(ApiUtil.createResponse(totalElementsJSON, true));
        }     
        return ok(ApiUtil.createResponse("Query method findTotalContainsElement() failed to retrieve total number of element", false));   
    }

}
