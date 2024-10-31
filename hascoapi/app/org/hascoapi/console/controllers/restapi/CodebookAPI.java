package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import org.hascoapi.Constants;
import org.hascoapi.entity.pojo.Codebook;
import org.hascoapi.entity.pojo.CodebookSlot;
import org.hascoapi.entity.pojo.Instrument;
import org.hascoapi.entity.pojo.ResponseOption;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.HAScOMapper;
import org.hascoapi.vocabularies.VSTOI;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

import static org.hascoapi.Constants.*;

public class CodebookAPI extends Controller {

    /** 
     *   MAINTAINING CODEBOOKS
     */

    private Result createCodebookResult(Codebook codebook) {
        codebook.save();
        return ok(ApiUtil.createResponse("Codebook <" + codebook.getUri() + "> has been CREATED.", true));
    }

    public Result createCodebook(String json) {
        if (json == null || json.equals("")) {
            return ok(ApiUtil.createResponse("No json content has been provided.", false));
        }
        //System.out.println("[createCodebook] Value of json: [" + json + "]");
        ObjectMapper objectMapper = new ObjectMapper();
        Codebook newCodebook;
        try {
            // convert json string to Instrument instance
            newCodebook = objectMapper.readValue(json, Codebook.class);
        } catch (Exception e) {
            e.printStackTrace();
            return ok(ApiUtil.createResponse("Failed to parse json.", false));
        }
        return createCodebookResult(newCodebook);
    }

    private Result deleteCodebookResult(Codebook codebook) {
        String uri = codebook.getUri();
        codebook.delete();
        return ok(ApiUtil.createResponse("Codebook <" + uri + "> has been DELETED.", true));
    }

    public Result deleteCodebook(String uri) {
        if (uri == null || uri.equals("")) {
            return ok(ApiUtil.createResponse("No codebook URI has been provided.", false));
        }
        Codebook codebook = Codebook.find(uri);
        if (codebook == null) {
            return ok(ApiUtil.createResponse("There is no codebook with URI <" + uri + "> to be deleted.", false));
        } else {
            return deleteCodebookResult(codebook);
        }
    }

    public Result attach(String uri, String codebookSlotUri) {
        if (uri == null || uri.equals("")) {
            return ok(ApiUtil.createResponse("No response option URI has been provided.", false));
        }
        ResponseOption responseOption = ResponseOption.find(uri);
        if (responseOption == null) {
            return ok(ApiUtil.createResponse("There is no detector with URI <" + uri + "> to be attached.", false));
        }
        if (codebookSlotUri == null || codebookSlotUri.equals("")) {
            return ok(ApiUtil.createResponse("No CodebookSlot URI has been provided.", false));
        }
        CodebookSlot codebookSlot = CodebookSlot.find(codebookSlotUri);
        if (codebookSlot == null) {
            return ok(ApiUtil.createResponse("There is no CodebookSlot with uri <" + codebookSlotUri + ">.",
                    false));
        }
        if (ResponseOption.attach(codebookSlot, responseOption)) {
            return ok(ApiUtil.createResponse("ResponseOption <" + uri
                    + "> successfully attached to CodebookSlot <" + codebookSlotUri + ">.", true));
        }
        return ok(ApiUtil.createResponse("ResponseOption <" + uri + "> failed to associate with CodebookSlot  <"
                + codebookSlotUri + ">.", false));
    }

    public Result detach(String codebookSlotUri) {
        if (codebookSlotUri == null || codebookSlotUri.equals("")) {
            return ok(ApiUtil.createResponse("No containerSlot URI has been provided.", false));
        }
        CodebookSlot codebookSlot = CodebookSlot.find(codebookSlotUri);
        if (codebookSlot == null) {
            return ok(ApiUtil.createResponse("There is no Codebook Slot with URI <" + codebookSlotUri + ">.",
                    false));
        }
        if (ResponseOption.detach(codebookSlot)) {
            return ok(ApiUtil.createResponse(
                    "No Response Option is associated with Codebook Slot <" + codebookSlotUri + ">.", true));
        }
        return ok(ApiUtil.createResponse(
                "A Response Option has failed to be removed from Codebook Slot <" + codebookSlotUri + ">.",
                false));
    }

    /** 
     *   TESTING CODEBOOKS
     */

    public Result createCodebookForTesting() {
        Codebook testCodebook = Codebook.find(TEST_CODEBOOK_URI);
        if (testCodebook != null) {
            return ok(ApiUtil.createResponse("Test Codebook already exists.", false));
        } else {
            testCodebook = new Codebook();
            testCodebook.setUri(TEST_CODEBOOK_URI);
            testCodebook.setLabel("Test Codebook");
            testCodebook.setTypeUri(VSTOI.CODEBOOK);
            testCodebook.setHascoTypeUri(VSTOI.CODEBOOK);
            testCodebook.setHasLanguage("en");
            testCodebook.setHasVersion("1");
            testCodebook.setHasSIRManagerEmail("me@example.com");
            testCodebook.setNamedGraph(Constants.TEST_KB);
            testCodebook.save();
            return ok(ApiUtil.createResponse("Test Codebook been CREATED.", true));
        }
    }

    public Result deleteCodebookForTesting() {
        Codebook codebook = Codebook.find(TEST_CODEBOOK_URI);
        if (codebook == null) {
            return ok(ApiUtil.createResponse("There is no Test Codebook to be deleted.", false));
        } else {
            codebook.setNamedGraph(Constants.TEST_KB);
            codebook.delete();
            return ok(ApiUtil.createResponse("Test Codebook has been DELETED.", true));
        }
    }

    public Result attachForTesting() {
        Codebook testCodebook = Codebook.find(TEST_CODEBOOK_URI);
        if (testCodebook == null) {
            return ok(ApiUtil.createResponse("Create test codebook before attaching response options.", false));
        }
        if (testCodebook.getCodebookSlots() == null) {
            return ok(ApiUtil.createResponse(
                    "Create response option slots for test codebook before attaching response options.", false));
        }
        CodebookSlot slot1 = CodebookSlot.find(TEST_CODEBOOK_SLOT1_URI);
        CodebookSlot slot2 = CodebookSlot.find(TEST_CODEBOOK_SLOT2_URI);
        if (slot1 == null || slot2 == null) {
            return ok(ApiUtil.createResponse(
                    "Either Test Codebook Slot 1 or 2 is unavailable to allow the containerSlot of ResponseOptions 1 and 2 to test codebook.",
                    false));
        }
        if (slot1.getHasResponseOption() != null) {
            return ok(ApiUtil.createResponse("Test Codebook Slot 1 already has an attached Response Option", false));
        }
        if (slot2.getHasResponseOption() != null) {
            return ok(ApiUtil.createResponse("Test Codebook Slot 2 already has an attached Response Option", false));
        }
        ResponseOption test1 = ResponseOption.find(TEST_RESPONSE_OPTION1_URI);
        ResponseOption test2 = ResponseOption.find(TEST_RESPONSE_OPTION2_URI);
        if (test1 == null || test2 == null) {
            return ok(ApiUtil.createResponse(
                    "Either Test Response Option 1 or 2 is unavailable to be attached to test codebook.", false));
        } else {
            slot1.setNamedGraph(Constants.TEST_KB);
            slot2.setNamedGraph(Constants.TEST_KB);
            test1.setNamedGraph(Constants.TEST_KB);
            test2.setNamedGraph(Constants.TEST_KB);
            boolean done = ResponseOption.attach(slot1, test1);
            if (!done) {
                return ok(ApiUtil.createResponse(
                        "The containerSlot of Test Response Option 1 to Test CodebookSlot1 HAS FAILED.", false));
            } else {
                done = ResponseOption.attach(slot2, test2);
                if (!done) {
                    return ok(ApiUtil.createResponse(
                            "The containerSlot of Test Response Option 2 to Test CodebookSlot2 HAS FAILED.", false));
                }
            }
        }
        return ok(ApiUtil.createResponse(
                "Test Response Options 1 and 2 have been ATTACHED to Test Codebook Slots 1 and 2.", true));
    }

    public Result detachForTesting() {
        Codebook testCodebook = Codebook.find(TEST_CODEBOOK_URI);
        if (testCodebook == null) {
            return ok(ApiUtil.createResponse("There is no test codebook to have their response options detached.",
                    false));
        }
        if (testCodebook.getCodebookSlots() == null) {
            return ok(ApiUtil.createResponse("Test codebook has no CodebookSlots for Response Options.", false));
        }
        ResponseOption test1 = ResponseOption.find(TEST_RESPONSE_OPTION1_URI);
        ResponseOption test2 = ResponseOption.find(TEST_RESPONSE_OPTION2_URI);
        CodebookSlot slot1 = CodebookSlot.find(TEST_CODEBOOK_SLOT1_URI);
        CodebookSlot slot2 = CodebookSlot.find(TEST_CODEBOOK_SLOT2_URI);
        if (test1 == null) {
            return ok(ApiUtil.createResponse(
                    "There is no Test Response Option 1 to be detached from test code book slot.", false));
        } else if (test2 == null) {
            return ok(ApiUtil.createResponse(
                    "There is no Test Response Option 2 to be detached from test code book slot.", false));
        } else if (slot1 == null) {
            return ok(ApiUtil.createResponse(
                    "There is no Test Codebook Slot 1 in test code book.", false));
        } else if (slot2 == null) {
            return ok(ApiUtil.createResponse(
                    "There is no Test Codebook Slot 2 in test code book.", false));
        } else if (slot1.getResponseOption() == null) {
            return ok(ApiUtil.createResponse(
                    "There is no Test Response Option to be detached from Slot 1.", false));
        } else if (slot2.getResponseOption() == null) {
            return ok(ApiUtil.createResponse(
                    "There is no Test Response Option to be detached from Slot 2.", false));
        } else {
            slot1.setNamedGraph(Constants.TEST_KB);
            slot2.setNamedGraph(Constants.TEST_KB);
            boolean done = ResponseOption.detach(slot1);
            if (done) {
                done = ResponseOption.detach(slot2);
            }
            if (done) {
                return ok(ApiUtil.createResponse(
                        "Test Response Options 1 and 2 have been DETACHED from Test Codebook Slot 1.", true));
            }
        }
        return ok(ApiUtil.createResponse("The detachment of Test Detectors 1 and 2 from Test Instrument HAS FAILED.",
                false));
    }
    
    /** 
     *   QUERYING CODEBOOKS
     */

    public static Result getCodebooks(List<Codebook> results) {
        if (results == null) {
            return ok(ApiUtil.createResponse("No codebook has been found", false));
        } else {
            ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,VSTOI.CODEBOOK);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }

}
