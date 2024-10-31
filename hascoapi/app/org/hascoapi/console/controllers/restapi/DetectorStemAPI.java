package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import org.hascoapi.Constants;
import org.hascoapi.entity.pojo.ContainerSlot;
import org.hascoapi.entity.pojo.DetectorStem;
import org.hascoapi.entity.pojo.Instrument;
import org.hascoapi.entity.pojo.SemanticVariable;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.HAScOMapper;
import org.hascoapi.vocabularies.VSTOI;

import play.mvc.Controller;
import play.mvc.Result;
import static org.hascoapi.Constants.*;
import java.util.List;


public class DetectorStemAPI extends Controller {

    private Result createDetectorStemResult(DetectorStem detectorStem) {
        detectorStem.save();
        return ok(ApiUtil.createResponse("DetectorStem <" + detectorStem.getUri() + "> has been CREATED.", true));
    }

    public Result createDetectorStemsForTesting() {
        DetectorStem testDetectorStem1 = DetectorStem.find(TEST_DETECTOR_STEM1_URI);
        DetectorStem testDetectorStem2 = DetectorStem.find(TEST_DETECTOR_STEM2_URI);
        SemanticVariable testSemanticVariable1 = SemanticVariable.find(TEST_SEMANTIC_VARIABLE1_URI);
        SemanticVariable testSemanticVariable2 = SemanticVariable.find(TEST_SEMANTIC_VARIABLE2_URI);
        if (testDetectorStem1 != null) {
            return ok(ApiUtil.createResponse("TestDetectorStem1 already exists.", false));
        } else if (testDetectorStem2 != null) {
            return ok(ApiUtil.createResponse("TestDetectorStem2 already exists.", false));
        } else if (testSemanticVariable1 == null || testSemanticVariable2 == null) {
            return ok(ApiUtil.createResponse("Create TestSemanticVariables 1 and 2 before creating TestDetectorStems.", false));
        } else {
            testDetectorStem1 = new DetectorStem(VSTOI.DETECTOR_STEM);
            testDetectorStem1.setUri(TEST_DETECTOR_STEM1_URI);
            testDetectorStem1.setLabel("Test Detector Stem 1");
            testDetectorStem1.setTypeUri(VSTOI.DETECTOR_STEM);
            testDetectorStem1.setHascoTypeUri(VSTOI.DETECTOR_STEM);
            testDetectorStem1.setComment("This is a dummy Detector Stem 1 created to test the SIR API.");
            testDetectorStem1.setHasContent("During the last 2 weeks, have you lost appetite?");
            testDetectorStem1.setHasLanguage("en"); // ISO 639-1
            testDetectorStem1.setHasVersion("1");
            testDetectorStem1.setHasSIRManagerEmail("me@example.com");
            testDetectorStem1.setDetects(TEST_SEMANTIC_VARIABLE1_URI);
            testDetectorStem1.setNamedGraph(Constants.TEST_KB);
            testDetectorStem1.save();

            testDetectorStem2 = new DetectorStem(VSTOI.DETECTOR_STEM);
            testDetectorStem2.setUri(TEST_DETECTOR_STEM2_URI);
            testDetectorStem2.setLabel("Test Detector Stem 2");
            testDetectorStem2.setTypeUri(VSTOI.DETECTOR_STEM);
            testDetectorStem2.setHascoTypeUri(VSTOI.DETECTOR_STEM);
            testDetectorStem2.setComment("This is a dummy Detector Stem 2 created to test the SIR API.");
            testDetectorStem2.setHasContent("During the last 2 weeks, have you gain appetite?");
            testDetectorStem2.setHasLanguage("en"); // ISO 639-1
            testDetectorStem2.setHasVersion("1");
            testDetectorStem2.setHasSIRManagerEmail("me@example.com");
            testDetectorStem2.setDetects(TEST_SEMANTIC_VARIABLE2_URI);
            testDetectorStem2.setNamedGraph(Constants.TEST_KB);
            testDetectorStem2.save();
            return ok(ApiUtil.createResponse("Test Detector Stems 1 and 2 have been CREATED.", true));
        }
    }

    public Result createDetectorStem(String json) {
        if (json == null || json.equals("")) {
            return ok(ApiUtil.createResponse("No json content has been provided.", false));
        }
        //System.out.println("(CreateDetectorStem) Value of json: [" + json + "]");
        ObjectMapper objectMapper = new ObjectMapper();
        DetectorStem newDetectorStem;
        try {
            //convert json string to Instrument instance
            newDetectorStem  = objectMapper.readValue(json, DetectorStem.class);
        } catch (Exception e) {
            //System.out.println("(createDetector) Failed to parse json.");
            return ok(ApiUtil.createResponse("Failed to parse json.", false));
        }
        return createDetectorStemResult(newDetectorStem);
    }

    private Result deleteDetectorStemResult(DetectorStem detectorStem) {
        String uri = detectorStem.getUri();
        detectorStem.delete();
        return ok(ApiUtil.createResponse("Detector Stem <" + uri + "> has been DELETED.", true));
    }

    public Result deleteDetectorStemsForTesting(){
        DetectorStem test1 = DetectorStem.find(TEST_DETECTOR_STEM1_URI);
        DetectorStem test2 = DetectorStem.find(TEST_DETECTOR_STEM2_URI);
        if (test1 == null) {
            return ok(ApiUtil.createResponse("There is no Test Detector Stem 1 to be deleted.", false));
        } else if (test2 == null) {
            return ok(ApiUtil.createResponse("There is no Test Detector Stem 2 to be deleted.", false));
        } else {
            test1.setNamedGraph(Constants.TEST_KB);
            test1.delete();
            test2.setNamedGraph(Constants.TEST_KB);
            test2.delete();
            return ok(ApiUtil.createResponse("Test Detector Stems 1 and 2 have been DELETED.", true));
        }
    }

    public Result deleteDetectorStem(String uri){
        if (uri == null || uri.equals("")) {
            return ok(ApiUtil.createResponse("No detector setm URI has been provided.", false));
        }
        DetectorStem detectorStem = DetectorStem.find(uri);
        if (detectorStem == null) {
            return ok(ApiUtil.createResponse("There is no detector stem with URI <" + uri + "> to be deleted.", false));
        } else {
            return deleteDetectorStemResult(detectorStem);
        }
    }

    public Result getDetectorStemsByInstrument(String instrumentUri){
        List<DetectorStem> results = DetectorStem.findByInstrument(instrumentUri);
        return getDetectorStems(results);
    }

    public static Result getDetectorStems(List<DetectorStem> results){
        if (results == null) {
            return ok(ApiUtil.createResponse("No detector stem has been found", false));
        } else {
            ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,VSTOI.DETECTOR_STEM);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }

}
