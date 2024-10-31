package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

import org.hascoapi.Constants;
import org.hascoapi.entity.pojo.SemanticVariable;
import org.hascoapi.entity.pojo.Entity;
import org.hascoapi.entity.pojo.Attribute;
import org.hascoapi.entity.pojo.Unit;
import org.hascoapi.transform.Renderings;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.HAScOMapper;
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.vocabularies.VSTOI;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.hascoapi.Constants.TEST_SEMANTIC_VARIABLE1_URI;
import static org.hascoapi.Constants.TEST_SEMANTIC_VARIABLE2_URI;
import static org.hascoapi.Constants.TEST_ENTITY_URI;
import static org.hascoapi.Constants.TEST_ATTRIBUTE1_URI;
import static org.hascoapi.Constants.TEST_ATTRIBUTE2_URI;
import static org.hascoapi.Constants.TEST_UNIT_URI;

public class SemanticVariableAPI extends Controller {

    private Result createSemanticVariableResult(SemanticVariable semanticVariable) {
        semanticVariable.save();
        return ok(ApiUtil.createResponse("SemanticVariable <" + semanticVariable.getUri() + "> has been CREATED.", true));
    }

    public Result createSemanticVariable(String json) {
        if (json == null || json.equals("")) {
            return ok(ApiUtil.createResponse("No json content has been provided.", false));
        }
        //System.out.println("(SemanticVariableAPI) Value of json in createSemanticVariable: [" + json + "]");
        ObjectMapper objectMapper = new ObjectMapper();
        SemanticVariable newSemanticVariable;
        try {
            //convert json string to SemanticVariable semanticVariableance
            newSemanticVariable  = objectMapper.readValue(json, SemanticVariable.class);
        } catch (Exception e) {
            //System.out.println("(SemanticVariableAPI) Failed to parse json for [" + json + "]");
            return ok(ApiUtil.createResponse("Failed to parse json.", false));
        }
        return createSemanticVariableResult(newSemanticVariable);
    }

    public Result createSemanticVariablesForTesting() {
        SemanticVariable testSemanticVariable1 = SemanticVariable.find(TEST_SEMANTIC_VARIABLE1_URI);
        SemanticVariable testSemanticVariable2 = SemanticVariable.find(TEST_SEMANTIC_VARIABLE2_URI);
        Entity testEntity = Entity.find(TEST_ENTITY_URI);
        Attribute testAttribute1 = Attribute.find(TEST_ATTRIBUTE1_URI);
        Attribute testAttribute2 = Attribute.find(TEST_ATTRIBUTE2_URI);
        Unit testUnit = Unit.find(TEST_UNIT_URI);
        if (testSemanticVariable1 != null) {
            return ok(ApiUtil.createResponse("Test semanticVariable <" + TEST_SEMANTIC_VARIABLE1_URI + "> already exists.", false));
        } else if (testSemanticVariable2 != null) {
            return ok(ApiUtil.createResponse("Test semanticVariable <" + TEST_SEMANTIC_VARIABLE2_URI + "> already exists.", false));
        } else if (testEntity == null) {
            return ok(ApiUtil.createResponse("Test entity <" + TEST_ENTITY_URI + "> needs to be created before the test semantic variables are created.", false));
        } else if (testAttribute1 == null) {
            return ok(ApiUtil.createResponse("Test attribute <" + TEST_ATTRIBUTE1_URI + "> needs to be created before the test semantic variables are created.", false));
        } else if (testAttribute2 == null) {
            return ok(ApiUtil.createResponse("Test attribute <" + TEST_ATTRIBUTE2_URI + "> needs to be created before the test semantic variables are created.", false));
        } else if (testUnit == null) {
            return ok(ApiUtil.createResponse("Test unit <" + TEST_UNIT_URI + "> needs to be created before the test semantic variables are created.", false));
        } else {            
            testSemanticVariable1 = new SemanticVariable();
            testSemanticVariable1.setUri(TEST_SEMANTIC_VARIABLE1_URI);
            testSemanticVariable1.setLabel("Test SemanticVariable");
            testSemanticVariable1.setTypeUri(HASCO.SEMANTIC_VARIABLE);
            testSemanticVariable1.setHascoTypeUri(HASCO.SEMANTIC_VARIABLE);
            testSemanticVariable1.setEntityUri(TEST_ENTITY_URI);
            testSemanticVariable1.setAttributeUri(TEST_ATTRIBUTE1_URI);
            testSemanticVariable1.setIsCategorical(false);
            testSemanticVariable1.setUnitUri(TEST_UNIT_URI);
            testSemanticVariable1.setComment("This is a dummy semanticVariable created to test the SIR API.");
            testSemanticVariable1.setNamedGraph(Constants.TEST_KB);

//            testSemanticVariable1.setHasSIRManagerEmail("me@example.com");
            testSemanticVariable1.save();

            testSemanticVariable2 = new SemanticVariable();
            testSemanticVariable2.setUri(TEST_SEMANTIC_VARIABLE2_URI);
            testSemanticVariable2.setLabel("Test SemanticVariable");
            testSemanticVariable2.setTypeUri(HASCO.SEMANTIC_VARIABLE);
            testSemanticVariable2.setHascoTypeUri(HASCO.SEMANTIC_VARIABLE);
            testSemanticVariable2.setEntityUri(TEST_ENTITY_URI);
            testSemanticVariable2.setAttributeUri(TEST_ATTRIBUTE2_URI);
            testSemanticVariable2.setIsCategorical(true);
            testSemanticVariable2.setComment("This is a dummy semanticVariable created to test the SIR API.");
            testSemanticVariable2.setNamedGraph(Constants.TEST_KB);
//            testSemanticVariable2.setHasSIRManagerEmail("me@example.com");
            testSemanticVariable2.save();
            return ok(ApiUtil.createResponse("Testing semanticVariables 1 and 2 have been CREATED.", true));
        }
    }

    private Result deleteSemanticVariableResult(SemanticVariable semanticVariable) {
        String uri = semanticVariable.getUri();
        semanticVariable.delete();
        return ok(ApiUtil.createResponse("SemanticVariable <" + uri + "> has been DELETED.", true));
    }

    public Result deleteSemanticVariable(String uri){
        if (uri == null || uri.equals("")) {
            return ok(ApiUtil.createResponse("No semanticVariable URI has been provided.", false));
        }
        SemanticVariable semanticVariable = SemanticVariable.find(uri);
        if (semanticVariable == null) {
            return ok(ApiUtil.createResponse("There is no semanticVariable with URI <" + uri + "> to be deleted.", false));
        } else {
            return deleteSemanticVariableResult(semanticVariable);
        }
    }

    public Result deleteSemanticVariablesForTesting(){
        SemanticVariable test1 = SemanticVariable.find(TEST_SEMANTIC_VARIABLE1_URI);
        SemanticVariable test2 = SemanticVariable.find(TEST_SEMANTIC_VARIABLE2_URI);
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
            return ok(ApiUtil.createResponse("Testing SemanticVariable 1 has been DELETED.", true));
        }
        if (!exist1 && exist2) {
            return ok(ApiUtil.createResponse("Testing SemanticVariable 2 has been DELETED.", true));
        }
        if (exist1 && exist2) {
            return ok(ApiUtil.createResponse("Testing SemanticVariables 1 and 2 have been DELETED.", true));
        }
        return ok(ApiUtil.createResponse("No Testing SemanticVariable has been DELETED.", false));
    }

    public static Result getSemanticVariables(List<SemanticVariable> results){
        if (results == null) {
            return ok(ApiUtil.createResponse("No semantic variable has been found", false));
        } else {
            ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,HASCO.SEMANTIC_VARIABLE);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }



}
