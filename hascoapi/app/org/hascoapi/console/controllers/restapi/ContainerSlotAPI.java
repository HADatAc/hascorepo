package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

import org.hascoapi.Constants;
import org.hascoapi.entity.fhir.Questionnaire;
import org.hascoapi.entity.pojo.Container;
import org.hascoapi.entity.pojo.ContainerSlot;
import org.hascoapi.entity.pojo.SlotElement;
import org.hascoapi.entity.pojo.Instrument;
import org.hascoapi.entity.pojo.Subcontainer;
import org.hascoapi.entity.pojo.SlotOperations;
import org.hascoapi.transform.Renderings;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.HAScOMapper;
import org.hascoapi.vocabularies.VSTOI;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.hascoapi.Constants.*;

public class ContainerSlotAPI extends Controller {

    /** 
     *   MAINTAINING CONTAINERS SLOTS
     */

    public Result createContainerSlots(String containerUri, String totContainerSlots) {
        //System.out.println("createContainerSlots: called with following containerUri = [" + containerUri + "]");
        if (containerUri == null || containerUri.isEmpty()) {
            return ok(ApiUtil.createResponse("Cannot create container slots without providing a container URI.", false));
        }
        //System.out.println("createContainerSlots: trying to read as instrument");
        if (containerUri.indexOf("/" + Constants.PREFIX_INSTRUMENT) >= 0) {
            Instrument instrument = Instrument.find(containerUri);
            if (instrument != null) {
                //System.out.println("createContainerSlots: FOUND INSTRUMENT");
                return createContainerSlots((Container)instrument, totContainerSlots);
            }
        }            
        if (containerUri.indexOf("/" + Constants.PREFIX_SUBCONTAINER) >= 0) {
            //System.out.println("createContainerSlots: trying to read as subcontainer");
            Subcontainer subcontainer = Subcontainer.find(containerUri);
            if (subcontainer != null) {
                //System.out.println("createContainerSlots: FOUND SUBCONTAINER");
                //System.out.println("createContainerSlots: read subcontainer with belongsTo = [" + subcontainer.getBelongsTo() + "]");
                Container container = (Container)subcontainer;
                //System.out.println("createContainerSlots: subcontainer after casting with belongsTo = [" + container.getBelongsTo() + "]");
                return createContainerSlots((Container)subcontainer, totContainerSlots);
            } 
        }
        return ok(ApiUtil.createResponse("There is no container with uri [" + containerUri + "]", false));
    }

    public Result createContainerSlots(Container container, String totContainerSlots) {
        if (container == null) {
            return ok(ApiUtil.createResponse("Cannot create container slots on null container", false));
        }
        if (totContainerSlots == null || totContainerSlots.equals("")) {
            return ok(ApiUtil.createResponse("No total numbers of containerSlots to be created has been provided.", false));
        }
        int total = 0;
        try {
            total = Integer.parseInt(totContainerSlots);
        } catch (Exception e) {
            return ok(ApiUtil.createResponse("totContainerSlots is not a valid number of containerSlots.", false));
        }
        if (total <= 0) {
            return ok(ApiUtil.createResponse("Total numbers of containerSlots need to be greated than zero.", false));
        }
        if (ContainerSlot.createContainerSlots(container, total)) {
            return ok(ApiUtil.createResponse("A total of " + total + " containerSlots have been created for instrument/subContainer <" + container.getUri() + ">.", true));
        } else {
            return ok(ApiUtil.createResponse("Method failed to create containerSlots for instrument <" + container.getUri() + ">.", false));
        }
    }

    /** 
    public Result deleteContainerSlots(String containerUri) {
        if (containerUri == null || containerUri.isEmpty()) {
            return ok(ApiUtil.createResponse("Cannot delete container slots without providing a container URI.", false));
        }
        Container container = Instrument.find(containerUri);
        if (container == null) {
            container = Subcontainer.find(containerUri);
        }
        return deleteContainerSlots(container);
    }
    */

    /** 
    public Result deleteContainerSlots(Container container) {
        if (container == null) {
            return ok(ApiUtil.createResponse("No container with provided URI has been found.", false));
        }
        if (container.getSlotElements() == null) {
            return ok(ApiUtil.createResponse("Container has no containerSlot to be deleted.", false));
        }
        container.deleteContainerSlots();
        return ok(ApiUtil.createResponse("ContainerSlots for Container <" + container.getUri() + "> have been deleted.", true));
    }
    */

    /** 
     *   TESTING CONTAINER SLOTS
     */

    public Result createContainerSlotsForTesting() {

        // VERIFY IF TEST INSTRUMENT AND TEST SUBCONTAINER EXIST
        Instrument testInstrument = Instrument.find(TEST_INSTRUMENT_URI);
        if (testInstrument == null) {
            return ok(ApiUtil.createResponse("Test instrument <" + TEST_INSTRUMENT_URI + "> needs to exist before its containerSlots can be created.", false));
        } 
        Subcontainer testSubcontainer = Subcontainer.find(TEST_SUBCONTAINER1_URI);
        if (testSubcontainer == null) {
            return ok(ApiUtil.createResponse("Test subcontainer <" + TEST_SUBCONTAINER1_URI + "> needs to exist before its containerSlots can be created.", false));
        } 
        
        // CREATE CONTAINER SLOTS
        List<ContainerSlot> instrumentSlots = testInstrument.getContainerSlots();
        if (instrumentSlots != null && instrumentSlots.size() > 0) {
            return ok(ApiUtil.createResponse("Test instrument <" + TEST_INSTRUMENT_URI + "> already has container slots.", false));
        } 
        List<ContainerSlot> subcontainerSlots = testSubcontainer.getContainerSlots();
        if (subcontainerSlots != null && subcontainerSlots.size() > 0) {
            return ok(ApiUtil.createResponse("Test subcontainer <" + TEST_SUBCONTAINER1_URI + "> already has container slots.", false));
        } 

        testInstrument.setNamedGraph(Constants.TEST_KB);
        ContainerSlot.createContainerSlots(testInstrument,TEST_INSTRUMENT_TOT_CONTAINER_SLOTS);
        testSubcontainer.setNamedGraph(Constants.TEST_KB);
        ContainerSlot.createContainerSlots(testSubcontainer,TEST_SUBCONTAINER_TOT_CONTAINER_SLOTS);
        
        return ok(ApiUtil.createResponse("Required containerSlots for testing containers have been created.", false));

    }

    public Result deleteContainerSlotsForTesting() {

        // VERIFY IF TEST INSTRUMENT AND TEST SUBCONTAINER EXIST 
        Instrument testInstrument = Instrument.find(TEST_INSTRUMENT_URI);
        if (testInstrument == null) {
            return ok(ApiUtil.createResponse("Test instrument <" + TEST_INSTRUMENT_URI + "> needs to exist before its containerSlots can be deleted.", false));
        } 
        Subcontainer testSubcontainer = Subcontainer.find(TEST_SUBCONTAINER1_URI);
        if (testSubcontainer == null) {
            return ok(ApiUtil.createResponse("Test subcontainer <" + TEST_SUBCONTAINER1_URI + "> needs to exist before its containerSlots can be deleted.", false));
        }
        
        // DELETE EXISTING CONTAINER SLOTS 
        List<ContainerSlot> instrumentSlots = testInstrument.getContainerSlots();
        if (instrumentSlots == null || instrumentSlots.size() == 0) {
            return ok(ApiUtil.createResponse("Test instrument <" + TEST_INSTRUMENT_URI + "> has no container slots to be deleted.", false));
        } 
        List<ContainerSlot> subcontainerSlots = testSubcontainer.getContainerSlots();
        if (subcontainerSlots == null || subcontainerSlots.size() == 0) {
            return ok(ApiUtil.createResponse("Test subcontainer <" + TEST_SUBCONTAINER1_URI + "> has no container slots to be deleted.", false));
        } 
        
        testInstrument.setNamedGraph(Constants.TEST_KB);
        List<SlotElement> slotList = Container.getSlotElements(testInstrument);
        if (slotList != null) {
            for (SlotElement slot : slotList) {
                slot.setNamedGraph(Constants.TEST_KB);
                SlotOperations.deleteSlotElement(slot);
            }
        }
        testSubcontainer.setNamedGraph(Constants.TEST_KB);
        slotList = Container.getSlotElements(testSubcontainer);
        if (slotList != null) {
            for (SlotElement slot : slotList) {
                slot.setNamedGraph(Constants.TEST_KB);
                SlotOperations.deleteSlotElement(slot);
            }
        }
        
        return ok(ApiUtil.createResponse("Existing container slots for testing containers have been deleted.", false));

    }

    /** 
     *   QUERYING CONTAINERS SLOTS
     */

    public Result getAllContainerSlots(){
        List<ContainerSlot> results = ContainerSlot.find();
        return getContainerSlots(results);
    }

    public static Result getContainerSlots(List<ContainerSlot> results){
        if (results == null) {
            return ok(ApiUtil.createResponse("No containerSlot has been found", false));
        } else {
            ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,VSTOI.CONTAINER_SLOT);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }

    public static Result getSlotElements(List<SlotElement> results){
        if (results == null) {
            return ok(ApiUtil.createResponse("No slotListelement has been found", false));
        } else {
            // TODO: may need to change second argument of getfiltered()
            ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,VSTOI.CONTAINER_SLOT);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }

}
