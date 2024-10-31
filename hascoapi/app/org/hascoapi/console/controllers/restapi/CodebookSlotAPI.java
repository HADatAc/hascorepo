package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import org.hascoapi.Constants;
import org.hascoapi.entity.pojo.Codebook;
import org.hascoapi.entity.pojo.CodebookSlot;
import org.hascoapi.entity.pojo.Container;
import org.hascoapi.entity.pojo.Instrument;
import org.hascoapi.entity.pojo.Subcontainer;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.HAScOMapper;
import org.hascoapi.vocabularies.VSTOI;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

import static org.hascoapi.Constants.*;

public class CodebookSlotAPI extends Controller {
    
    public Result createCodebookSlots(String codebookUri, String totCodebookSlots) {
        if (codebookUri == null || codebookUri.equals("")) {
            return ok(ApiUtil.createResponse("No codebook URI has been provided.", false));
        }
        Codebook codebook = Codebook.find(codebookUri);
        return createCodebookSlots(codebook, totCodebookSlots);
    }

    public Result createCodebookSlots(Codebook codebook, String totCodebookSlots) {
        if (codebook == null) {
            return ok(ApiUtil.createResponse("No codebook with provided URI has been found.", false));
        }
        if (codebook.getCodebookSlots() != null) {
            return ok(ApiUtil.createResponse(
                    "Codebook already has containerSlots. Delete existing containerSlots before creating new containerSlots",
                    false));
        }
        if (totCodebookSlots == null || totCodebookSlots.equals("")) {
            return ok(
                    ApiUtil.createResponse("No total numbers of containerSlots to be created has been provided.", false));
        }
        int total = 0;
        try {
            total = Integer.parseInt(totCodebookSlots);
        } catch (Exception e) {
            return ok(ApiUtil.createResponse("totCodebookSlots is not a valid number of containerSlots.", false));
        }
        if (total <= 0) {
            return ok(ApiUtil.createResponse("Total numbers of codebook slots need to be greater than zero.", false));
        }
        if (codebook.createCodebookSlots(total)) {
            return ok(ApiUtil.createResponse(
                    "A total of " + total + " codebook slots have been created for codebook <" + codebook.getUri() + ">.",
                    true));
        } else {
            return ok(ApiUtil.createResponse(
                    "Method failed to create codebook slots for codebook <" + codebook.getUri() + ">.", false));
        }
    }

    public Result createCodebookSlotsForTesting() {
        Codebook testCodebook = Codebook.find(TEST_CODEBOOK_URI);
        if (testCodebook == null) {
            return ok(ApiUtil.createResponse("Test codebook <" + TEST_CODEBOOK_URI
                    + "> needs to exist before its codebook slots can be created.", false));
        } else if (testCodebook.getCodebookSlots() != null && testCodebook.getCodebookSlots().size() > 0) {
            return ok(ApiUtil.createResponse("Test codebook <" + TEST_CODEBOOK_URI + "> already has codebook slots.",
                    false));
        } else {
            testCodebook.setNamedGraph(Constants.TEST_KB);
            return createCodebookSlots(testCodebook, TEST_CODEBOOK_TOT_CODEBOOK_SLOTS);
        }
    }

    public Result deleteCodebookSlots(String codebookUri) {
        //System.out.println("CodebookAPI.deleteCodebookSlots()");
        if (codebookUri == null || codebookUri.equals("")) {
            return ok(ApiUtil.createResponse("No codebook URI has been provided.", false));
        }
        Codebook codebook = Codebook.find(codebookUri);
        return deleteCodebookSlots(codebook);
    }

    public Result deleteCodebookSlots(Codebook codebook) {
        if (codebook == null) {
            return ok(ApiUtil.createResponse("No codebook with provided URI has been found.", false));
        }
        if (codebook.getCodebookSlots() == null) {
            return ok(ApiUtil.createResponse("Codebook has no codebook slots to be deleted.", false));
        }
        codebook.deleteCodebookSlots();
        return ok(ApiUtil.createResponse(
                "CodebookSlots for Codebook <" + codebook.getUri() + "> have been deleted.", true));
    }

    public Result deleteCodebookSlotsForTesting() {
        Codebook testCodebook = Codebook.find(TEST_CODEBOOK_URI);
        if (testCodebook == null) {
            return ok(ApiUtil.createResponse("Test codebook <" + TEST_CODEBOOK_URI
                    + "> needs to exist before its codebook slots can be deleted.", false));
        } else if (testCodebook.getCodebookSlots() == null || testCodebook.getCodebookSlots().size() == 0) {
            return ok(ApiUtil.createResponse(
                    "Test codebook <" + TEST_CODEBOOK_URI + "> has no codebook slots to be deleted.", false));
        } else {
            testCodebook.setNamedGraph(Constants.TEST_KB);
            return deleteCodebookSlots(testCodebook);
        }
    }

    /** 
     *   QUERYING CODEBOOK SLOTS
     */

    public Result getAllCodebookSlots() {
        List<CodebookSlot> results = CodebookSlot.find();
        return getCodebookSlots(results);
    }

    public Result getCodebookSlotsByCodebook(String codebookUri) {
        List<CodebookSlot> results = CodebookSlot.findByCodebook(codebookUri);
        return getCodebookSlots(results);
    }

    public static Result getCodebookSlots(List<CodebookSlot> results) {
        if (results == null) {
            return ok(ApiUtil.createResponse("No response option slot has been found", false));
        } else {
            ObjectMapper mapper = new ObjectMapper();
            SimpleFilterProvider filterProvider = new SimpleFilterProvider();
            filterProvider.addFilter("CodebookSlotFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "hasPriority", "hasResponseOption"));
            mapper.setFilterProvider(filterProvider);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }

}
