package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

import org.hascoapi.Constants;
import org.hascoapi.entity.pojo.SDD;
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

public class SDDAPI extends Controller {

    private Result createSDDResult(SDD sdd) {
        sdd.save();
        return ok(ApiUtil.createResponse("SDD <" + sdd.getUri() + "> has been CREATED.", true));
    }

    public Result createSDD(String json) {
        if (json == null || json.equals("")) {
            return ok(ApiUtil.createResponse("No json content has been provided.", false));
        }
        //System.out.println("(UnitAPI) Value of json in createUnit: [" + json + "]");
        ObjectMapper objectMapper = new ObjectMapper();
        SDD newSDD;
        try {
            //convert json string to Unit unitance
            newSDD  = objectMapper.readValue(json, SDD.class);
        } catch (Exception e) {
            //System.out.println("(UnitAPI) Failed to parse json for [" + json + "]");
            return ok(ApiUtil.createResponse("Failed to parse json.", false));
        }
        return createSDDResult(newSDD);
    }

    /** 
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
    */

    public static Result getSDDs(List<SDD> results){
        if (results == null) {
            return ok(ApiUtil.createResponse("No SDD has been found", false));
        } else {
            //for (SDD sdd: results) {
            //    System.out.println(sdd.getLabel() + "  [" + sdd.getHasDataFile() + "]");
            //}
            ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,HASCO.SDD);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }



}
