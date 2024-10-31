package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import org.hascoapi.Constants;
import org.hascoapi.entity.pojo.ContainerSlot;
import org.hascoapi.entity.pojo.DetectorStem;
import org.hascoapi.entity.pojo.Detector;
import org.hascoapi.entity.pojo.Instrument;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.HAScOMapper;
import org.hascoapi.vocabularies.VSTOI;

import play.mvc.Controller;
import play.mvc.Result;
import static org.hascoapi.Constants.*;
import java.util.List;


public class DetectorAPI extends Controller {

    /** 
     *   MAINTAINING DETECTORS
     */

    private Result createDetectorResult(Detector detector) {
        detector.save();
        return ok(ApiUtil.createResponse("Detector <" + detector.getUri() + "> has been CREATED.", true));
    }

    public Result createDetector(String json) {
        if (json == null || json.equals("")) {
            return ok(ApiUtil.createResponse("No json content has been provided.", false));
        }
        //System.out.println("(CreateDetector) Value of json: [" + json + "]");
        ObjectMapper objectMapper = new ObjectMapper();
        Detector newDetector;
        try {
            //convert json string to Container instance
            newDetector  = objectMapper.readValue(json, Detector.class);
        } catch (Exception e) {
            //System.out.println("(createDetector) Failed to parse json.");
            return ok(ApiUtil.createResponse("Failed to parse json.", false));
        }
        return createDetectorResult(newDetector);
    }

    private Result deleteDetectorResult(Detector detector) {
        String uri = detector.getUri();
        detector.delete();
        return ok(ApiUtil.createResponse("Detector <" + uri + "> has been DELETED.", true));
    }

    public Result deleteDetector(String uri){
        if (uri == null || uri.equals("")) {
            return ok(ApiUtil.createResponse("No detector URI has been provided.", false));
        }
        Detector detector = Detector.findDetector(uri);
        if (detector == null) {
            return ok(ApiUtil.createResponse("There is no detector with URI <" + uri + "> to be deleted.", false));
        } else {
            return deleteDetectorResult(detector);
        }
    }

    /** 
     *   TESTING DETECTORS
     */

    public Result createDetectorsForTesting() {
        Detector testDetector1 = Detector.findDetector(TEST_DETECTOR1_URI);
        Detector testDetector2 = Detector.findDetector(TEST_DETECTOR2_URI);
        Detector testDetector3 = Detector.findDetector(TEST_DETECTOR3_URI);
        Detector testDetector4 = Detector.findDetector(TEST_DETECTOR4_URI);
        if (testDetector1 != null) {
            return ok(ApiUtil.createResponse("Test detector 1 already exists.", false));
        } else if (testDetector2 != null) {
            return ok(ApiUtil.createResponse("Test detector 2 already exists.", false));
        } else if (testDetector3 != null) {
            return ok(ApiUtil.createResponse("Test detector 3 already exists.", false));
        } else if (testDetector4 != null) {
            return ok(ApiUtil.createResponse("Test detector 4 already exists.", false));
        } else {
            DetectorStem testDetectorStem1 = DetectorStem.find(TEST_DETECTOR_STEM1_URI);
            DetectorStem testDetectorStem2 = DetectorStem.find(TEST_DETECTOR_STEM2_URI);
            if (testDetectorStem1 == null) {
              return ok(ApiUtil.createResponse("Required TestDetectorStem1 does not exist.", false));
            } else if (testDetectorStem2 == null) {
              return ok(ApiUtil.createResponse("Required TestDetectorStem2 does not exist.", false));
            } else {
                testDetector1 = new Detector(VSTOI.DETECTOR);
                testDetector1.setUri(TEST_DETECTOR1_URI);
                testDetector1.setLabel("Test Detector 1");
                testDetector1.setTypeUri(VSTOI.DETECTOR);
                testDetector1.setHascoTypeUri(VSTOI.DETECTOR);
                testDetector1.setComment("This is a dummy Detector 1 created to test the SIR API.");
                testDetector1.setHasDetectorStem(TEST_DETECTOR_STEM1_URI);
                testDetector1.setHasCodebook(TEST_CODEBOOK_URI);
                testDetector1.setHasLanguage("en");
                testDetector1.setHasVersion("1");
                testDetector1.setHasSIRManagerEmail("me@example.com");
                testDetector1.setNamedGraph(Constants.TEST_KB);
                testDetector1.save();

                testDetector2 = new Detector(VSTOI.DETECTOR);
                testDetector2.setUri(TEST_DETECTOR2_URI);
                testDetector2.setLabel("Test Detector 2");
                testDetector2.setTypeUri(VSTOI.DETECTOR);
                testDetector2.setHascoTypeUri(VSTOI.DETECTOR);
                testDetector2.setComment("This is a dummy Detector 2 created to test the SIR API.");
                testDetector2.setHasDetectorStem(TEST_DETECTOR_STEM2_URI);
                testDetector2.setHasCodebook(TEST_CODEBOOK_URI);
                testDetector2.setHasLanguage("en");
                testDetector2.setHasVersion("1");
                testDetector2.setHasSIRManagerEmail("me@example.com");
                testDetector2.setNamedGraph(Constants.TEST_KB);
                testDetector2.save();

                testDetector3 = new Detector(VSTOI.DETECTOR);
                testDetector3.setUri(TEST_DETECTOR3_URI);
                testDetector3.setLabel("Test Detector 3");
                testDetector3.setTypeUri(VSTOI.DETECTOR);
                testDetector3.setHascoTypeUri(VSTOI.DETECTOR);
                testDetector3.setComment("This is a dummy Detector 3 created to test the SIR API.");
                testDetector3.setHasDetectorStem(TEST_DETECTOR_STEM1_URI);
                testDetector3.setHasCodebook(TEST_CODEBOOK_URI);
                testDetector3.setHasLanguage("en");
                testDetector3.setHasVersion("1");
                testDetector3.setHasSIRManagerEmail("me@example.com");
                testDetector3.setNamedGraph(Constants.TEST_KB);
                testDetector3.save();

                testDetector4 = new Detector(VSTOI.DETECTOR);
                testDetector4.setUri(TEST_DETECTOR4_URI);
                testDetector4.setLabel("Test Detector 4");
                testDetector4.setTypeUri(VSTOI.DETECTOR);
                testDetector4.setHascoTypeUri(VSTOI.DETECTOR);
                testDetector4.setComment("This is a dummy Detector 4 created to test the SIR API.");
                testDetector4.setHasDetectorStem(TEST_DETECTOR_STEM2_URI);
                testDetector4.setHasCodebook(TEST_CODEBOOK_URI);
                testDetector4.setHasLanguage("en");
                testDetector4.setHasVersion("1");
                testDetector4.setHasSIRManagerEmail("me@example.com");
                testDetector4.setNamedGraph(Constants.TEST_KB);
                testDetector4.save();

            }
            return ok(ApiUtil.createResponse("Test Detectors 1 and 2 have been CREATED.", true));
        }
    }

    public Result deleteDetectorsForTesting(){
        Detector test1 = Detector.findDetector(TEST_DETECTOR1_URI);
        Detector test2 = Detector.findDetector(TEST_DETECTOR2_URI);
        Detector test3 = Detector.findDetector(TEST_DETECTOR3_URI);
        Detector test4 = Detector.findDetector(TEST_DETECTOR4_URI);
        String msg = "";
        if (test1 == null) {
            msg += "Test Detector 1. ";
        } else {
            test1.setNamedGraph(Constants.TEST_KB);
            test1.delete();
        } 
        if (test2 == null) {
            msg += "Test Detector 2. ";
        } else {
            test2.setNamedGraph(Constants.TEST_KB);
            test2.delete();
        }
        if (test3 == null) {
            msg += "Test Detector 3. ";
        } else {
            test3.setNamedGraph(Constants.TEST_KB);
            test3.delete();
        } 
        if (test4 == null) {
            msg += "Test Detector 4. ";
        } else {
            test4.setNamedGraph(Constants.TEST_KB);
            test4.delete();
        }
        if (msg.isEmpty()) {
            return ok(ApiUtil.createResponse("Following detectors did not exist: " + msg, false));
        } else {
            return ok(ApiUtil.createResponse("Existing Test Detectors have been DELETED.", true));
        }
    }

    /*** 
     *  QUERYING DETECTORS
     */


    public Result getDetectorsByContainer(String instrumentUri){
        List<Detector> results = Detector.findDetectorsByContainer(instrumentUri);
        return getDetectors(results);
    }

    public static Result getDetectors(List<Detector> results){
        if (results == null) {
            return ok(ApiUtil.createResponse("No detector has been found", false));
        } else {
            ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,VSTOI.DETECTOR);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            //System.out.println("DetecttorAPI: [" + ApiUtil.createResponse(jsonObject, true) + "]");
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }

    public Result getUsage(String detectorUri){
        List<ContainerSlot> results = Detector.usage(detectorUri);
        return ContainerSlotAPI.getContainerSlots(results);
    }

}
