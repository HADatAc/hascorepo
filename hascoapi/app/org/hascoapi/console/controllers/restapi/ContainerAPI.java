package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

import org.hascoapi.Constants;
import org.hascoapi.entity.fhir.Questionnaire;
import org.hascoapi.entity.pojo.Annotation;
import org.hascoapi.entity.pojo.Container;
import org.hascoapi.entity.pojo.ContainerSlot;
import org.hascoapi.entity.pojo.SlotElement;
import org.hascoapi.entity.pojo.StudyObject;
import org.hascoapi.entity.pojo.Instrument;
import org.hascoapi.entity.pojo.Subcontainer;
import org.hascoapi.entity.pojo.Detector;
import org.hascoapi.entity.pojo.GenericFind;
import org.hascoapi.transform.Renderings;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.HAScOMapper;
import org.hascoapi.vocabularies.VSTOI;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.hascoapi.Constants.*;

public class ContainerAPI extends Controller {

    /** 
     *   MAINTAINING CONTAINER
     */

    public Result attach(String uri, String containerSlotUri){
        if (uri == null || uri.equals("")) {
            return ok(ApiUtil.createResponse("No detector URI has been provided.", false));
        }
        Detector detector = Detector.findDetector(uri);
        if (detector == null) {
            return ok(ApiUtil.createResponse("There is no detector with URI <" + uri + "> to be attached.", false));
        }
        if (containerSlotUri == null || containerSlotUri.equals("")) {
            return ok(ApiUtil.createResponse("No containerSlot URI has been provided.", false));
        }
        ContainerSlot containerSlot = ContainerSlot.find(containerSlotUri);
        if (containerSlot == null) {
            return ok(ApiUtil.createResponse("There is no containerSlot with uri <" + containerSlotUri + ">.", false));
        }
        if (Detector.attach(containerSlot, detector)) {
            return ok(ApiUtil.createResponse("Detector <" + uri + "> successfully attached to containerSlot <" + containerSlotUri + ">.", true));
        }
        return ok(ApiUtil.createResponse("Detector <" + uri + "> failed to associate with containerSlot  <" + containerSlotUri + ">.", false));
    }

    public Result detach(String containerSlotUri){
        if (containerSlotUri == null || containerSlotUri.equals("")) {
            return ok(ApiUtil.createResponse("No containerSlot URI has been provided.", false));
        }
        ContainerSlot containerSlot = ContainerSlot.find(containerSlotUri);
        if (containerSlot == null) {
            return ok(ApiUtil.createResponse("There is no containerSlot with URI <" + containerSlotUri + ">.", false));
        }
        if (Detector.detach(containerSlot)) {
            return ok(ApiUtil.createResponse("No detector is associated with containerSlot <" + containerSlotUri + ">.", true));
        }
        return ok(ApiUtil.createResponse("A detector has failed to be removed from containerSlot <" + containerSlotUri + ">.", false));
    }

    /** 
     *   TESTING CONTAINER
     */

    public Result attachForTesting(){

        // VERIFYING INSTRUMENT
        Instrument testInstrument = Instrument.find(TEST_INSTRUMENT_URI);
        if (testInstrument == null) {
            return ok(ApiUtil.createResponse("create test instrument before trying to attach detectors.", false));
        }
        if (testInstrument.getSlotElements() == null) {
            return ok(ApiUtil.createResponse("Create containerSlots for test instrument before trying to attach detectors.", false));
        }
        Detector detector1 = Detector.findDetector(TEST_DETECTOR1_URI);
        Detector detector2 = Detector.findDetector(TEST_DETECTOR2_URI);
        ContainerSlot slot1 = ContainerSlot.find(TEST_CONTAINER_SLOT1_URI);
        ContainerSlot slot2 = ContainerSlot.find(TEST_CONTAINER_SLOT2_URI);
        if (detector1 == null) {
            return ok(ApiUtil.createResponse("There is no Test Detector 1 to be attached to test instrument.", false));
        } else if (detector2 == null) {
            return ok(ApiUtil.createResponse("There is no Test Detector 2 to be attached to test instrument.", false));
        } else if (slot1 == null) {
            return ok(ApiUtil.createResponse("There is no Test Container Slot 1 in test instrument.", false));
        } else if (slot2 == null) {
            return ok(ApiUtil.createResponse("There is no Test Container Slot 2 in test instrument.", false));
        } else if (slot1.getDetector() != null) {
            return ok(ApiUtil.createResponse("There is a Test Detector already attached to Slot 1.", false));
        } else if (slot2.getDetector() != null) {
            return ok(ApiUtil.createResponse("There is a Test Detector already attached to Slot 2.", false));
        } 
        
        // VERIFYING SUBCONTAINER
        Subcontainer testSubcontainer = Subcontainer.find(TEST_SUBCONTAINER1_URI);
        if (testSubcontainer == null) {
            return ok(ApiUtil.createResponse("create test subcontainer before trying to attach detectors.", false));
        }
        if (testSubcontainer.getSlotElements() == null) {
            return ok(ApiUtil.createResponse("Create containerSlots for test subcontainer before trying to attach detectors.", false));
        }
        Detector detector3 = Detector.findDetector(TEST_DETECTOR3_URI);
        Detector detector4 = Detector.findDetector(TEST_DETECTOR4_URI);
        ContainerSlot slot3 = ContainerSlot.find(TEST_CONTAINER_SLOT3_URI);
        ContainerSlot slot4 = ContainerSlot.find(TEST_CONTAINER_SLOT4_URI);
        if (detector3 == null) {
            return ok(ApiUtil.createResponse("There is no Test Detector 3 to be attached to test subcontainer.", false));
        } else if (detector4 == null) {
            return ok(ApiUtil.createResponse("There is no Test Detector 4 to be attached to test subcontainer.", false));
        } else if (slot3 == null) {
            return ok(ApiUtil.createResponse("There is no Test Container Slot 3 in test subcontainer.", false));
        } else if (slot4 == null) {
            return ok(ApiUtil.createResponse("There is no Test Container Slot 4 in test subcontainer.", false));
        } else if (slot3.getDetector() != null) {
            return ok(ApiUtil.createResponse("There is a Test Detector already attached to Slot 3.", false));
        } else if (slot2.getDetector() != null) {
            return ok(ApiUtil.createResponse("There is a Test Detector already attached to Slot 4.", false));
        }  
            
        // PERFORM ATTACHMENTS
        boolean done = Detector.attach(slot1, detector1);
        if (!done) {
            return ok(ApiUtil.createResponse("The use of ContainerSlot1 to attach TestDetector1 to Test Istrument HAS FAILED.", false));
        } else {
            done = Detector.attach(slot2, detector2);
            if (!done) {
                return ok(ApiUtil.createResponse("The use of ContainerSlot2 to attach TestDetector2 to Test Instrument HAS FAILED.", false));
            } else {
              done = Detector.attach(slot3, detector3);
              if (!done) {
                  return ok(ApiUtil.createResponse("The use of ContainerSlot3 to attach TestDetector3 to Test Subcontainer HAS FAILED.", false));
              } else {
                done = Detector.attach(slot4, detector4);
                if (!done) {
                  return ok(ApiUtil.createResponse("The use of ContainerSlot4 to attach TestDetector4 to Test Subcontainer HAS FAILED.", false));
                }
              }
            }
        }

        return ok(ApiUtil.createResponse("Test Detectors 1 and 2 have been ATTACHED to Test Container.", true));
    }

    public Result detachForTesting(){

        // VERIFY INSTRUMENT SETTING FOR DETACH
        Instrument testInst = Instrument.find(TEST_INSTRUMENT_URI);
        if (testInst == null) {
            return ok(ApiUtil.createResponse("There is no test instrument to detach detectors.", false));
        }
        if (testInst.getSlotElements() == null) {
            return ok(ApiUtil.createResponse("Test instrument has no containerSlots for detectors.", false));
        }
        Detector test1 = Detector.findDetector(TEST_DETECTOR1_URI);
        Detector test2 = Detector.findDetector(TEST_DETECTOR2_URI);
        ContainerSlot slot1 = ContainerSlot.find(TEST_CONTAINER_SLOT1_URI);
        ContainerSlot slot2 = ContainerSlot.find(TEST_CONTAINER_SLOT2_URI);
        if (test1 == null) {
            return ok(ApiUtil.createResponse("There is no Test Detector 1 to be attached to test instrument.", false));
        } else if (test2 == null) {
            return ok(ApiUtil.createResponse("There is no Test Detector 2 to be attached to test instrument.", false));
        } else if (slot1 == null) {
            return ok(ApiUtil.createResponse("There is no Test Container Slot 1 in test instrument.", false));
        } else if (slot2 == null) {
            return ok(ApiUtil.createResponse("There is no Test Container Slot 2 in test instrument.", false));
        } else if (slot1.getDetector() == null) {
            return ok(ApiUtil.createResponse("There is no Test Detector to be detached from Slot 1.", false));
        } else if (slot2.getDetector() == null) {
            return ok(ApiUtil.createResponse("There is no Test Detector to be detached from Slot 2.", false));
        } 

        // VERIFY SUBCONTAINER SETTING FOR DETACH
        Subcontainer testSubcontainer = Subcontainer.find(TEST_SUBCONTAINER1_URI);
        if (testSubcontainer == null) {
            return ok(ApiUtil.createResponse("There is no test subcontainer to detach detectors.", false));
        }
        if (testSubcontainer.getSlotElements() == null) {
            return ok(ApiUtil.createResponse("Test subcontainer has no containerSlots for detectors.", false));
        }
        Detector detector3 = Detector.findDetector(TEST_DETECTOR3_URI);
        Detector detector4 = Detector.findDetector(TEST_DETECTOR4_URI);
        ContainerSlot slot3 = ContainerSlot.find(TEST_CONTAINER_SLOT3_URI);
        ContainerSlot slot4 = ContainerSlot.find(TEST_CONTAINER_SLOT4_URI);
        if (detector3 == null) {
            return ok(ApiUtil.createResponse("There is no Test Detector 3 to be attached to test subcontainer.", false));
        } else if (detector4 == null) {
            return ok(ApiUtil.createResponse("There is no Test Detector 4 to be attached to test subcontainer.", false));
        } else if (slot3 == null) {
            return ok(ApiUtil.createResponse("There is no Test Container Slot 3 in test subcontainer.", false));
        } else if (slot4 == null) {
            return ok(ApiUtil.createResponse("There is no Test Container Slot 4 in test subcontainer.", false));
        } else if (slot3.getDetector() == null) {
            return ok(ApiUtil.createResponse("There is no Test Detector to be detached from Slot 3.", false));
        } else if (slot4.getDetector() == null) {
            return ok(ApiUtil.createResponse("There is no Test Detector to be detached from Slot 4.", false));
        } 

        // PERFORM DETACHING 
        String msg = "";
        if (!Detector.detach(slot1)) {
            msg += "No datachment at slot 1. ";
        }
        if (!Detector.detach(slot2)) {
            msg += "No datachment at slot 2. ";
        }
        if (!Detector.detach(slot3)) {
            msg += "No datachment at slot 3. ";
        } 
        if (!Detector.detach(slot4)) {
            msg += "No datachment at slot 4. ";
        } 
        if (msg.isEmpty()) {
            return ok(ApiUtil.createResponse("Existing Test Detectors have been DETACHED from Test Instrument and Test Subcontainer.", true));
        }        
        return ok(ApiUtil.createResponse("The detachment of existing Test Detectors from Test Instrument and Test Subcontainer HAS FAILED.", false));
    }

    /**
     *   GET ELEMENTS BY MANAGER EMAIL AND CONTAINER WITH PAGE
     */
    public Result getElementsByManagerEmailByContainer(String containeruri, String elementtype, String manageremail, int pagesize, int offset) {
        if (manageremail == null || manageremail.isEmpty()) {
            return ok(ApiUtil.createResponse("No Manager Email has been provided", false));
        }
        if (elementtype.equals("annotation")) {
            GenericFind<Annotation> query = new GenericFind<Annotation>();
            List<Annotation> results = query.findByManagerEmailWithPagesByContainer(Annotation.class, containeruri, manageremail, pagesize, offset);
            return AnnotationAPI.getAnnotations(results);
        }  
        return ok("[getElementsByManagerEmailByContainer] No valid element type.");
    }

    public Result getTotalElementsByManagerEmailByContainer(String containeruri, String elementtype, String manageremail){
        //System.out.println("SIRElementAPI: getTotalElementsByManagerEmailByStudy");
        if (elementtype == null || elementtype.isEmpty()) {
            return ok(ApiUtil.createResponse("No elementtype has been provided", false));
        }
        Class clazz = GenericFind.getElementClass(elementtype);
        if (clazz == null) {        
            return ok(ApiUtil.createResponse("[" + elementtype + "] is not a valid elementtype", false));
        }
        int totalElements = totalElements = GenericFind.findTotalByManagerEmailByContainer(clazz, containeruri, manageremail);
        if (totalElements >= 0) {
            String totalElementsJSON = "{\"total\":" + totalElements + "}";
            return ok(ApiUtil.createResponse(totalElementsJSON, true));
        }
        return ok(ApiUtil.createResponse("query method getTotalElementsByManagerEmailByContainer() failed to retrieve total number of element", false));
    }

    /** 
     *  QUERYING CONTAINER
     */

    public Result getSlotElements(String containerUri) {
        //System.out.println("Inside ContainerAPI.getSlotElements(" + containerUri + ")");
        if (containerUri == null || containerUri.isEmpty()) {
            return ok(ApiUtil.createResponse("A container uri needs to be provided.", false));
        }
        Container container = Container.find(containerUri);
        return getSlotElements(container);
    }
        
    public Result getSlotElements(Container container) {
        if (container == null) {
            return ok(ApiUtil.createResponse("Container cannot be null.", false));
        }
        List<SlotElement> slotElements = Container.getSlotElements(container);
        if (slotElements == null) {
            return ok(ApiUtil.createResponse("No slot element has been found for given URI", false));
        } else {
            ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,VSTOI.CONTAINER_SLOT);
            JsonNode jsonObject = mapper.convertValue(slotElements, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }

    public static Result getContainers(List<Container> results){
        if (results == null) {
            return ok(ApiUtil.createResponse("No container has been found", false));
        } else {
            ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,VSTOI.CONTAINER);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }

}
