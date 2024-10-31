package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import java.util.ArrayList;
import java.util.List;

import org.hascoapi.Constants;
import org.hascoapi.entity.pojo.Container;
import org.hascoapi.entity.pojo.Instrument;
import org.hascoapi.entity.pojo.Subcontainer;
import org.hascoapi.entity.pojo.SlotOperations;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.HAScOMapper;
import org.hascoapi.vocabularies.VSTOI;
import play.mvc.Controller;
import play.mvc.Result;

import static org.hascoapi.Constants.*;

public class SubcontainerAPI extends Controller {

    /** 
     *   MAINTAINING SUBCONTAINERS
     */

    private Result createSubcontainerResult(Subcontainer subcontainer) {
        if (subcontainer.saveAsSlot()) {
            return ok(ApiUtil.createResponse("Subcontainer <" + subcontainer.getUri() + "> has been CREATED.", true));
        } else {
            return ok(ApiUtil.createResponse("Subcontainer <" + subcontainer.getUri() + "> FAILED to be created.", false));
        }
    }

    public Result updateSubcontainer(String subcontainerJson) {
        //System.out.println("Inside ContainerAPI.updateSubcontainer. JSON is [" + subcontainerJson + "]");
        if (subcontainerJson == null || subcontainerJson.isEmpty()) {
            return ok(ApiUtil.createResponse("A container JSON needs to be provided.", false));
        }
        if (Subcontainer.updateAsSlot(subcontainerJson)) {
            return ok(ApiUtil.createResponse("A subcontainer for the provided JSON has been sucessfully updated.", true));
        }
        return ok(ApiUtil.createResponse("Method Subcontainer.updateAsSlot(JSON) has failed to update the content of the JSO file provided.", false));
    }
        
    /** 
     *   TESTING SUBCONTAINERS
     */

    public Result createSubcontainerForTesting() {
        Instrument testInstrument = Instrument.find(TEST_INSTRUMENT_URI);
        testInstrument.setHasFirst(TEST_SUBCONTAINER1_URI);
        testInstrument.save();

        Subcontainer testSubcontainer1 = Subcontainer.find(TEST_SUBCONTAINER1_URI);
        Subcontainer testSubcontainer2 = Subcontainer.find(TEST_SUBCONTAINER2_URI);
        if (testInstrument == null) {
            return ok(ApiUtil.createResponse("Test instrument <" + TEST_INSTRUMENT_URI + "> is required before the subcontainer can be created.", false));
        } else if (testSubcontainer1 != null) {
            return ok(ApiUtil.createResponse("Test subcontainer <" + TEST_SUBCONTAINER1_URI + "> already exists.", false));
        } else if (testSubcontainer2 != null) {
            return ok(ApiUtil.createResponse("Test subcontainer <" + TEST_SUBCONTAINER2_URI + "> already exists.", false));
        } else {

            // Insert list of subcontainers in the reverse order

            testSubcontainer2 = new Subcontainer(VSTOI.SUBCONTAINER);
            testSubcontainer2.setUri(TEST_SUBCONTAINER2_URI);
            testSubcontainer2.setBelongsTo(TEST_INSTRUMENT_URI);
            testSubcontainer2.setLabel("Test Subcontainer 2");
            testSubcontainer2.setTypeUri(VSTOI.SUBCONTAINER);
            testSubcontainer2.setHascoTypeUri(VSTOI.SUBCONTAINER);
            testSubcontainer2.setHasShortName("SUBCONTAINER TEST 2");
            testSubcontainer2.setComment("Test subcontainer 2 to be added to the main Test Instrument.");
            testSubcontainer2.setHasPrevious(TEST_SUBCONTAINER1_URI);
            testSubcontainer2.setHasSIRManagerEmail("me@example.com");
            testSubcontainer2.setNamedGraph(Constants.TEST_KB);
            if (!testSubcontainer2.saveAsSlot()) {
                return ok(ApiUtil.createResponse("Failed to create Subcontainers 2.", false));
            }

            testSubcontainer1 = new Subcontainer(VSTOI.SUBCONTAINER);
            testSubcontainer1.setUri(TEST_SUBCONTAINER1_URI);
            testSubcontainer1.setBelongsTo(TEST_INSTRUMENT_URI);
            testSubcontainer1.setLabel("Test Subcontainer 1");
            testSubcontainer1.setTypeUri(VSTOI.SUBCONTAINER);
            testSubcontainer1.setHascoTypeUri(VSTOI.SUBCONTAINER);
            testSubcontainer1.setHasShortName("SUBCONTAINER TEST 1");
            testSubcontainer1.setComment("Test subcontainer 1 to be added to the main Test Instrument.");
            testSubcontainer1.setHasNext(TEST_SUBCONTAINER2_URI);
            testSubcontainer1.setHasSIRManagerEmail("me@example.com");
            testSubcontainer1.setNamedGraph(Constants.TEST_KB);
            if (testSubcontainer1.saveAsSlot()) {
                return ok(ApiUtil.createResponse("Subcontainers 1 and 2 has been CREATED.", true));
            } else {
                return ok(ApiUtil.createResponse("Failed to create Subcontainers 1.", false));
            }
        }
    }

    public Result deleteSubcontainerForTesting(){
        Subcontainer testSubcontainer1 = Subcontainer.find(TEST_SUBCONTAINER1_URI);
        Subcontainer testSubcontainer2 = Subcontainer.find(TEST_SUBCONTAINER2_URI);
        String msg = "";
        if (testSubcontainer1 == null) {
            msg += "No Test subcontainer 1 to be deleted. ";
        } else {
            SlotOperations.deleteSlotElement(TEST_SUBCONTAINER1_URI);
            //testSubcontainer1.setNamedGraph(Constants.TEST_KB);
            //testSubcontainer1.deleteAndDetach();
        } 
        if (testSubcontainer2 == null) {
            msg += "No Test subcontainer 2 to be deleted. ";
        } else {
            SlotOperations.deleteSlotElement(TEST_SUBCONTAINER2_URI);
            //testSubcontainer2.setNamedGraph(Constants.TEST_KB);
            //testSubcontainer2.deleteAndDetach();
        }
        if (msg.isEmpty()) {
            return ok(ApiUtil.createResponse("Subcontainers 1 and 2 has been DELETED.", true));
        } else {
            return ok(ApiUtil.createResponse(msg, false));
        }
    }

    /**
     *   QUERYING SUBCONTAINER
     */
 
    public static Result getSubcontainers(List<Subcontainer> results){
        if (results == null) {
            return ok(ApiUtil.createResponse("No subcontainer has been found", false));
        } else {
            ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,VSTOI.SUBCONTAINER);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }

}
