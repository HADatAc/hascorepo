package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

import org.hascoapi.Constants;
import org.hascoapi.entity.pojo.Attribute;
import org.hascoapi.transform.Renderings;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.HAScOMapper;
import org.hascoapi.vocabularies.SIO;
import org.hascoapi.vocabularies.VSTOI;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.hascoapi.Constants.TEST_ATTRIBUTE1_URI;
import static org.hascoapi.Constants.TEST_ATTRIBUTE2_URI;

public class AttributeAPI extends Controller {

    private Result createAttributeResult(Attribute attribute) {
        attribute.save();
        return ok(ApiUtil.createResponse("Attribute <" + attribute.getUri() + "> has been CREATED.", true));
    }

    public Result createAttribute(String json) {
        if (json == null || json.equals("")) {
            return ok(ApiUtil.createResponse("No json content has been provided.", false));
        }
        //System.out.println("(AttributeAPI) Value of json in createAttribute: [" + json + "]");
        ObjectMapper objectMapper = new ObjectMapper();
        Attribute newInst;
        try {
            //convert json string to Attribute attributeance
            newInst  = objectMapper.readValue(json, Attribute.class);
        } catch (Exception e) {
            //System.out.println("(AttributeAPI) Failed to parse json for [" + json + "]");
            return ok(ApiUtil.createResponse("Failed to parse json.", false));
        }
        return createAttributeResult(newInst);
    }

    public Result createAttributesForTesting() {
        Attribute testAttribute1 = Attribute.find(TEST_ATTRIBUTE1_URI);
        Attribute testAttribute2 = Attribute.find(TEST_ATTRIBUTE2_URI);
        if (testAttribute1 != null) {
            return ok(ApiUtil.createResponse("Test attribute <" + TEST_ATTRIBUTE1_URI + "> already exists.", false));
        } else if (testAttribute2 != null) {
            return ok(ApiUtil.createResponse("Test attribute <" + TEST_ATTRIBUTE2_URI + "> already exists.", false));
        } else {            
            testAttribute1 = new Attribute();
            testAttribute1.setUri(TEST_ATTRIBUTE1_URI);
            testAttribute1.setSuperUri(SIO.ATTRIBUTE);
            testAttribute1.setLabel("Test Attribute");
            testAttribute1.setTypeUri(TEST_ATTRIBUTE1_URI);
            testAttribute1.setHascoTypeUri(SIO.ATTRIBUTE);
            testAttribute1.setComment("This is a dummy attribute created to test the SIR API.");
            testAttribute1.setNamedGraph(Constants.TEST_KB);
//            testAttribute1.setHasSIRManagerEmail("me@example.com");
            testAttribute1.save();
            testAttribute2 = new Attribute();
            testAttribute2.setUri(TEST_ATTRIBUTE2_URI);
            testAttribute2.setSuperUri(SIO.ATTRIBUTE);
            testAttribute2.setLabel("Test Attribute");
            testAttribute2.setTypeUri(TEST_ATTRIBUTE2_URI);
            testAttribute2.setHascoTypeUri(SIO.ATTRIBUTE);
            testAttribute2.setComment("This is a dummy attribute created to test the SIR API.");
            testAttribute2.setNamedGraph(Constants.TEST_KB);
//            testAttribute2.setHasSIRManagerEmail("me@example.com");
            testAttribute2.save();
            return ok(ApiUtil.createResponse("Testing attributes 1 and 2 have been CREATED.", true));
        }
    }

    private Result deleteAttributeResult(Attribute attribute) {
        String uri = attribute.getUri();
        attribute.delete();
        return ok(ApiUtil.createResponse("Attribute <" + uri + "> has been DELETED.", true));
    }

    public Result deleteAttribute(String uri){
        if (uri == null || uri.equals("")) {
            return ok(ApiUtil.createResponse("No attribute URI has been provided.", false));
        }
        Attribute attribute = Attribute.find(uri);
        if (attribute == null) {
            return ok(ApiUtil.createResponse("There is no attribute with URI <" + uri + "> to be deleted.", false));
        } else {
            return deleteAttributeResult(attribute);
        }
    }

    public Result deleteAttributesForTesting(){
        Attribute test1 = Attribute.find(TEST_ATTRIBUTE1_URI);
        Attribute test2 = Attribute.find(TEST_ATTRIBUTE2_URI);
        boolean exist1 = (test1 != null);
        boolean exist2 = (test2 != null);
        if (exist1) {
            test1.setNamedGraph(Constants.TEST_KB);
            test1.delete();
        } 
        if (exist2) {
            test2.setNamedGraph(Constants.TEST_KB);
            test2.delete();
        } 
        if (exist1 && !exist2) {
            return ok(ApiUtil.createResponse("Testing Attribute 1 has been DELETED.", true));
        }
        if (!exist1 && exist2) {
            return ok(ApiUtil.createResponse("Testing Attribute 2 has been DELETED.", true));
        }
        if (exist1 && exist2) {
            return ok(ApiUtil.createResponse("Testing Attributes 1 and 2 have been DELETED.", true));
        }
        return ok(ApiUtil.createResponse("No Testing Attribute has been DELETED.", false));
    }

    public static Result getAttributes(List<Attribute> results){
        if (results == null) {
            return ok(ApiUtil.createResponse("No attribute has been found", false));
        } else {
            ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,SIO.ATTRIBUTE);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }

}
