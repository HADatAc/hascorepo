package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

import org.hascoapi.Constants;
import org.hascoapi.entity.pojo.Unit;
import org.hascoapi.transform.Renderings;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.HAScOMapper;
import org.hascoapi.vocabularies.SIO;
import org.hascoapi.vocabularies.VSTOI;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.hascoapi.Constants.TEST_UNIT_URI;

public class UnitAPI extends Controller {

    private Result createUnitResult(Unit unit) {
        unit.save();
        return ok(ApiUtil.createResponse("Unit <" + unit.getUri() + "> has been CREATED.", true));
    }

    public Result createUnit(String json) {
        if (json == null || json.equals("")) {
            return ok(ApiUtil.createResponse("No json content has been provided.", false));
        }
        //System.out.println("(UnitAPI) Value of json in createUnit: [" + json + "]");
        ObjectMapper objectMapper = new ObjectMapper();
        Unit newInst;
        try {
            //convert json string to Unit unitance
            newInst  = objectMapper.readValue(json, Unit.class);
        } catch (Exception e) {
            //System.out.println("(UnitAPI) Failed to parse json for [" + json + "]");
            return ok(ApiUtil.createResponse("Failed to parse json.", false));
        }
        return createUnitResult(newInst);
    }

    public Result createUnitForTesting() {
        Unit testUnit = Unit.find(TEST_UNIT_URI);
        if (testUnit != null) {
            return ok(ApiUtil.createResponse("Test unit <" + TEST_UNIT_URI + "> already exists.", false));
        } else {
            testUnit = new Unit();
            testUnit.setUri(TEST_UNIT_URI);
            testUnit.setSuperUri(SIO.UNIT);
            testUnit.setLabel("Test Unit");
            testUnit.setTypeUri(TEST_UNIT_URI);
            testUnit.setHascoTypeUri(SIO.UNIT);
            testUnit.setComment("This is a dummy unit created to test the SIR API.");
            testUnit.setNamedGraph(Constants.TEST_KB);
            //testUnit.setHasSIRManagerEmail("me@example.com");

            return createUnitResult(testUnit);
        }
    }

    private Result deleteUnitResult(Unit unit) {
        String uri = unit.getUri();
        unit.delete();
        return ok(ApiUtil.createResponse("Unit <" + uri + "> has been DELETED.", true));
    }

    public Result deleteUnit(String uri){
        if (uri == null || uri.equals("")) {
            return ok(ApiUtil.createResponse("No unit URI has been provided.", false));
        }
        Unit unit = Unit.find(uri);
        if (unit == null) {
            return ok(ApiUtil.createResponse("There is no unit with URI <" + uri + "> to be deleted.", false));
        } else {
            return deleteUnitResult(unit);
        }
    }

    public Result deleteUnitForTesting(){
        Unit test;
        test = Unit.find(TEST_UNIT_URI);
        if (test == null) {
            return ok(ApiUtil.createResponse("There is no Test unit to be deleted.", false));
        } else {
            test.setNamedGraph(Constants.TEST_KB);
            return deleteUnitResult(test);
        }
    }

    public static Result getUnits(List<Unit> results){
        if (results == null) {
            return ok(ApiUtil.createResponse("No unit has been found", false));
        } else {
            ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,SIO.UNIT);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }



}
