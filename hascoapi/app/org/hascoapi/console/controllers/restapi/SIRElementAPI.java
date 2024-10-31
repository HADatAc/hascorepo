package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.hascoapi.entity.pojo.*;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.HAScOMapper;
import org.hascoapi.vocabularies.VSTOI;
import play.mvc.Controller;
import play.mvc.Result;
import java.util.List;
import java.util.ArrayList;

public class SIRElementAPI extends Controller {

    /**
     *   CREATE ELEMENT
     */

    public Result createElement(String elementType, String json) {
        System.out.println("Type: [" + elementType + "]  JSON [" + json + "]");
        if (json == null || json.equals("")) {
            return ok(ApiUtil.createResponse("No json content has been provided.", false));
        }
        if (elementType == null || elementType.isEmpty()) {
            return ok(ApiUtil.createResponse("No elementType has been provided", false));
        }
        Class clazz = GenericFind.getElementClass(elementType);
        if (clazz == null) {
            return ok(ApiUtil.createResponse("No valid elementType has been provided", false));
        }
        boolean success = true;
        String message = "";
        ObjectMapper objectMapper = new ObjectMapper();
        if (clazz == Instrument.class) {
            Instrument object;
            try {
                object = (Instrument)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                e.printStackTrace();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == InstrumentInstance.class) {
            InstrumentInstance object;
            try {
                object = (InstrumentInstance)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                e.printStackTrace();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == DetectorInstance.class) {
            DetectorInstance object;
            try {
                object = (DetectorInstance)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                e.printStackTrace();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == PlatformInstance.class) {
            PlatformInstance object;
            try {
                object = (PlatformInstance)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                e.printStackTrace();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == Subcontainer.class) {
            try {
                Subcontainer object;
                object = (Subcontainer)objectMapper.readValue(json, clazz);
                //System.out.println("SIRElementAPI.create(Subcontainer): JSON=[" + json + "]");
                object.saveAsSlot();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == DetectorStem.class) {
            try {
                DetectorStem object;
                object = (DetectorStem)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == Detector.class) {
            try {
                Detector object;
                object = (Detector)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == ContainerSlot.class) {
            // NOTE: Use ContainerSlot.createContainerSlots(container,totContainerSlots) to create container slots
        } else if (clazz == Codebook.class) {
            try {
                Codebook object;
                object = (Codebook)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == ResponseOption.class) {
            try {
                ResponseOption object;
                object = (ResponseOption)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == CodebookSlot.class) {
            try {
                CodebookSlot object;
                object = (CodebookSlot)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == AnnotationStem.class) {
            try {
                AnnotationStem object;
                object = (AnnotationStem)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == Annotation.class) {
            try {
                Annotation object;
                object = (Annotation)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == SemanticVariable.class) {
            try {
                SemanticVariable object;
                object = (SemanticVariable)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == InstrumentType.class) {
            try {
                InstrumentType object;
                object = (InstrumentType)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == DetectorStemType.class) {
            try {
                DetectorStemType object;
                object = (DetectorStemType)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == Entity.class) {
            try {
                Entity object;
                object = (Entity)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == Attribute.class) {
            try {
                Attribute object;
                object = (Attribute)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == Unit.class) {
            try {
                Unit object;
                object = (Unit)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == INS.class) {
            try {
                INS object;
                object = (INS)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == DA.class) {
            try {
                DA object;
                object = (DA)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == DD.class) {
            try {
                DD object;
                object = (DD)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == SDD.class) {
            try {
                SDD object;
                object = (SDD)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == SemanticDataDictionary.class) {
            try {
                SemanticDataDictionary object;
                object = (SemanticDataDictionary)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == SDDAttribute.class) {
            try {
                SDDAttribute object;
                object = (SDDAttribute)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                e.printStackTrace();
                return ok(ApiUtil.createResponse("Following JSON Exception while parsing JSON for " + clazz + ": " + e.getMessage(), false));
            } catch (Exception e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == SDDObject.class) {
            try {
                SDDObject object;
                object = (SDDObject)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == PossibleValue.class) {
            try {
                PossibleValue object;
                object = (PossibleValue)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                e.printStackTrace();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == DP2.class) {
            try {
                DP2 object;
                object = (DP2)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == STR.class) {
            try {
                STR object;
                object = (STR)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == DataFile.class) {
            try {
                DataFile object;
                object = (DataFile)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == DSG.class) {
            try {
                DSG object;
                object = (DSG)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == Study.class) {
            try {
                Study object;
                object = (Study)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == StudyObjectCollection.class) {
            try {
                StudyObjectCollection object;
                object = (StudyObjectCollection)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == StudyObject.class) {
            try {
                StudyObject object;
                object = (StudyObject)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == StudyRole.class) {
            try {
                StudyRole object;
                object = (StudyRole)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == VirtualColumn.class) {
            try {
                VirtualColumn object;
                object = (VirtualColumn)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == Platform.class) {
            try {
                Platform object;
                object = (Platform)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                System.out.println("Error processing Platform: " + e.getMessage());
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == Stream.class) {
            try {
                Stream object;
                object = (Stream)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                System.out.println("Error processing Stream: " + e.getMessage());
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == Deployment.class) {
            try {
                Deployment object;
                object = (Deployment)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                System.out.println("Error processing Deployment: " + e.getMessage());
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == Person.class) {
            try {
                Person object;
                object = (Person)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                System.out.println("Error processing Person: " + e.getMessage());
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == Organization.class) {
            try {
                Organization object;
                object = (Organization)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                System.out.println("Error processing Organization: " + e.getMessage());
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == Place.class) {
            try {
                Place object;
                object = (Place)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                System.out.println("Error processing Place: " + e.getMessage());
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } else if (clazz == PostalAddress.class) {
            try {
                PostalAddress object;
                object = (PostalAddress)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                System.out.println("Error processing PostalAddress: " + e.getMessage());
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
       } else if (clazz == KGR.class) {
            try {
                KGR object;
                object = (KGR)objectMapper.readValue(json, clazz);
                object.save();
            } catch (JsonProcessingException e) {
                System.out.println("Error processing KGR: " + e.getMessage());
                message = e.getMessage();
                return ok(ApiUtil.createResponse("Following error parsing JSON for " + clazz + ": " + e.getMessage(), false));
            }
        } 
        if (!success) {
            return ok(ApiUtil.createResponse("Error processing JSON: " + message, false));
        }
        return ok(ApiUtil.createResponse("Element has been saved", true));
    }

    /**
     *   DELETE ELEMENT
     */

    public Result deleteElement(String elementType, String uri) {
        //System.out.println("Delete element => Type: [" + elementType + "]  URI [" + uri + "]");
        if (uri == null || uri.equals("")) {
            return ok(ApiUtil.createResponse("No uri has been provided.", false));
        }
        if (elementType == null || elementType.isEmpty()) {
            return ok(ApiUtil.createResponse("No elementType has been provided", false));
        }
        Class clazz = GenericFind.getElementClass(elementType);
        if (clazz == null) {
            return ok(ApiUtil.createResponse("No valid elementType has been provided", false));
        }
        if (clazz == Instrument.class) {
            Instrument object = Instrument.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == InstrumentInstance.class) {
            InstrumentInstance object = InstrumentInstance.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == DetectorInstance.class) {
            DetectorInstance object = DetectorInstance.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == PlatformInstance.class) {
            PlatformInstance object = PlatformInstance.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == Subcontainer.class) {
            //Subcontainer object = Subcontainer.find(uri);
            if (!SlotOperations.deleteSlotElement(uri)) {
                return ok(ApiUtil.createResponse("Failed to delete element with URI [" + uri + "]", false));
            }
            //object.deleteAndDetach();
        } else if (clazz == SlotElement.class) {
            //Subcontainer object = Subcontainer.find(uri);
            if (!SlotOperations.deleteSlotElement(uri)) {
                return ok(ApiUtil.createResponse("Failed to delete element with URI [" + uri + "]", false));
            }
            //object.deleteAndDetach();
        } else if (clazz == DetectorStem.class) {
            DetectorStem object = DetectorStem.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == Detector.class) {
            Detector object = Detector.findDetector(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == ContainerSlot.class) {
            //ContainerSlot object = ContainerSlot.find(uri);
            if (!SlotOperations.deleteSlotElement(uri)) {
                return ok(ApiUtil.createResponse("Failed to delete element with URI [" + uri + "]", false));
            }
            //object.delete();
        } else if (clazz == Codebook.class) {
            Codebook object = Codebook.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == ResponseOption.class) {
            ResponseOption object = ResponseOption.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == CodebookSlot.class) {
            CodebookSlot object = CodebookSlot.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == AnnotationStem.class) {
            AnnotationStem object = AnnotationStem.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == Annotation.class) {
            Annotation object = Annotation.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == SemanticVariable.class) {
            SemanticVariable object = SemanticVariable.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == InstrumentType.class) {
            InstrumentType object = InstrumentType.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == DetectorStemType.class) {
            DetectorStemType object = DetectorStemType.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == Entity.class) {
            Entity object = Entity.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == Attribute.class) {
            Attribute object = Attribute.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == Unit.class) {
            Unit object = Unit.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == INS.class) {
            INS object = INS.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == DA.class) {
            DA object = DA.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == DD.class) {
            DD object = DD.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == SDD.class) {
            SDD object = SDD.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == SemanticDataDictionary.class) {
            SemanticDataDictionary object = SemanticDataDictionary.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == SDDAttribute.class) {
            SDDAttribute object = SDDAttribute.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == SDDObject.class) {
            SDDObject object = SDDObject.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == PossibleValue.class) {
            PossibleValue object = PossibleValue.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == DP2.class) {
            DP2 object = DP2.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == STR.class) {
            STR object = STR.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == DataFile.class) {
            DataFile object = DataFile.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == DSG.class) {
            DSG object = DSG.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == Study.class) {
            Study object = Study.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == StudyObjectCollection.class) {
            StudyObjectCollection object = StudyObjectCollection.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == StudyObject.class) {
            StudyObject object = StudyObject.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == StudyRole.class) {
            StudyRole object = StudyRole.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == VirtualColumn.class) {
            VirtualColumn object = VirtualColumn.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == Platform.class) {
            Platform object = Platform.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == Stream.class) {
            Stream object = Stream.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == Deployment.class) {
            Deployment object = Deployment.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == Person.class) {
            Person object = Person.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == Organization.class) {
            Organization object = Organization.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == Place.class) {
            Place object = Place.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == PostalAddress.class) {
            PostalAddress object = PostalAddress.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } else if (clazz == KGR.class) {
            KGR object = KGR.find(uri);
            if (object == null) {
                return ok(ApiUtil.createResponse("No element with URI [" + uri + "] has been found", false));
            }
            object.delete();
        } 
        return ok(ApiUtil.createResponse("Element with URI [" + uri + "] has been deleted", true));
    }

    /**
     *   GET ELEMENTS WITH PAGE
     */

    public Result getElementsWithPage(String elementType, int pageSize, int offset) {
        if (elementType == null || elementType.isEmpty()) {
            return ok(ApiUtil.createResponse("No elementType has been provided", false));
        }
        if (pageSize <=0) {
            return ok(ApiUtil.createResponse("Page size needs to be greated than zero", false));
        }
        if (offset < 0) {
            return ok(ApiUtil.createResponse("Offset needs to be igual or greater than zero", false));
        }
        Class clazz = GenericFind.getElementClass(elementType);
        if (clazz == null) {        
            return ok(ApiUtil.createResponse("[" + elementType + "] is not a valid elementType", false));
        }
        List<Object> results = (List<Object>)GenericFind.findWithPages(clazz,pageSize,offset);
        if (results != null && results.size() >= 0) {
            ObjectMapper mapper = HAScOMapper.getFilteredByClass("essential", clazz);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
        return ok(ApiUtil.createResponse("method getElements() failed to retrieve elements", false));    
    }

    public Result getTotalElements(String elementType) {
        if (elementType == null || elementType.isEmpty()) {
            return ok(ApiUtil.createResponse("No elementType has been provided", false));
        }
        Class clazz = GenericFind.getElementClass(elementType);
        if (clazz == null) {        
            return ok(ApiUtil.createResponse("[" + elementType + "] is not a valid elementType", false));
        }
        int totalElements = GenericFind.findTotal(clazz);
        if (totalElements >= 0) {
            String totalElementsJSON = "{\"total\":" + totalElements + "}";
            return ok(ApiUtil.createResponse(totalElementsJSON, true));
        }
        return ok(ApiUtil.createResponse("query method getTotalElements() failed to retrieve total number of element", false));
    }
    
    /**
     *   GET ELEMENTS BY KEYWORD WITH PAGE
     */

    public Result getElementsByKeywordWithPage(String elementType, String keyword, int pageSize, int offset) {
        if (keyword.equals("_")) {
            keyword = "";
        }
        if (elementType.equals("instrument")) {
            GenericFind<Instrument> query = new GenericFind<Instrument>();
            List<Instrument> results = query.findByKeywordWithPages(Instrument.class,keyword, pageSize, offset);
            return InstrumentAPI.getInstruments(results);
        } else if (elementType.equals("instrumentinstance")) {
            GenericFind<InstrumentInstance> query = new GenericFind<InstrumentInstance>();
            List<InstrumentInstance> results = query.findByKeywordWithPages(InstrumentInstance.class,keyword, pageSize, offset);
            return VSTOIInstanceAPI.getInstrumentInstances(results);
        } else if (elementType.equals("detectorinstance")) {
            GenericFind<DetectorInstance> query = new GenericFind<DetectorInstance>();
            List<DetectorInstance> results = query.findByKeywordWithPages(DetectorInstance.class,keyword, pageSize, offset);
            return VSTOIInstanceAPI.getDetectorInstances(results);
        } else if (elementType.equals("platforminstance")) {
            GenericFind<PlatformInstance> query = new GenericFind<PlatformInstance>();
            List<PlatformInstance> results = query.findByKeywordWithPages(PlatformInstance.class,keyword, pageSize, offset);
            return VSTOIInstanceAPI.getPlatformInstances(results);
        } else if (elementType.equals("detectorstem")) {
            GenericFind<DetectorStem> query = new GenericFind<DetectorStem>();
            List<DetectorStem> results = query.findByKeywordWithPages(DetectorStem.class,keyword, pageSize, offset);
            return DetectorStemAPI.getDetectorStems(results);
        } else if (elementType.equals("detector")) {
            GenericFind<Detector> query = new GenericFind<Detector>();
            List<Detector> results = query.findByKeywordWithPages(Detector.class,keyword, pageSize, offset);
            return DetectorAPI.getDetectors(results);
        } else if (elementType.equals("codebook")) {
            GenericFind<Codebook> query = new GenericFind<Codebook>();
            List<Codebook> results = query.findByKeywordWithPages(Codebook.class,keyword, pageSize, offset);
            return CodebookAPI.getCodebooks(results);
        } else if (elementType.equals("responseoption")) {
            GenericFind<ResponseOption> query = new GenericFind<ResponseOption>();
            List<ResponseOption> results = query.findByKeywordWithPages(ResponseOption.class,keyword, pageSize, offset);
            return ResponseOptionAPI.getResponseOptions(results);
        } else if (elementType.equals("annotationstem")) {
            GenericFind<AnnotationStem> query = new GenericFind<AnnotationStem>();
            List<AnnotationStem> results = query.findByKeywordWithPages(AnnotationStem.class,keyword, pageSize, offset);
            return AnnotationStemAPI.getAnnotationStems(results);
        } else if (elementType.equals("annotation")) {
            GenericFind<Annotation> query = new GenericFind<Annotation>();
            List<Annotation> results = query.findByKeywordWithPages(Annotation.class,keyword, pageSize, offset);
            return AnnotationAPI.getAnnotations(results);
        } else if (elementType.equals("semanticvariable")) {
            GenericFind<SemanticVariable> query = new GenericFind<SemanticVariable>();
            List<SemanticVariable> results = query.findByKeywordWithPages(SemanticVariable.class,keyword, pageSize, offset);
            return SemanticVariableAPI.getSemanticVariables(results);
        } else if (elementType.equals("entity")) {
            GenericFind<Entity> query = new GenericFind<Entity>();
            List<Entity> results = query.findByKeywordWithPages(Entity.class,keyword, pageSize, offset);
            return EntityAPI.getEntities(results);
        }  else if (elementType.equals("attribute")) {
            GenericFind<Attribute> query = new GenericFind<Attribute>();
            List<Attribute> results = query.findByKeywordWithPages(Attribute.class,keyword, pageSize, offset);
            return AttributeAPI.getAttributes(results);
        }  else if (elementType.equals("unit")) {
            GenericFind<Unit> query = new GenericFind<Unit>();
            List<Unit> results = query.findByKeywordWithPages(Unit.class,keyword, pageSize, offset);
            return UnitAPI.getUnits(results);
        }  else if (elementType.equals("ins")) {
            GenericFind<INS> query = new GenericFind<INS>();
            List<INS> results = query.findByKeywordWithPages(INS.class,keyword, pageSize, offset);
            return INSAPI.getINSs(results);
        }  else if (elementType.equals("da")) {
            GenericFind<DA> query = new GenericFind<DA>();
            List<DA> results = query.findByKeywordWithPages(DA.class,keyword, pageSize, offset);
            return DAAPI.getDAs(results);
        }  else if (elementType.equals("dd")) {
            GenericFind<DD> query = new GenericFind<DD>();
            List<DD> results = query.findByKeywordWithPages(DD.class,keyword, pageSize, offset);
            return DDAPI.getDDs(results);
        }  else if (elementType.equals("sdd")) {
            GenericFind<SDD> query = new GenericFind<SDD>();
            List<SDD> results = query.findByKeywordWithPages(SDD.class,keyword, pageSize, offset);
            return SDDAPI.getSDDs(results);
        }  else if (elementType.equals("semanticdatadictionary")) {
            GenericFind<SemanticDataDictionary> query = new GenericFind<SemanticDataDictionary>();
            List<SemanticDataDictionary> results = query.findByKeywordWithPages(SemanticDataDictionary.class,keyword, pageSize, offset);
            return SemanticDataDictionaryAPI.getSemanticDataDictionaries(results);
        }  else if (elementType.equals("sddattribute")) {
            GenericFind<SDDAttribute> query = new GenericFind<SDDAttribute>();
            List<SDDAttribute> results = query.findByKeywordWithPages(SDDAttribute.class,keyword, pageSize, offset);
            return SemanticDataDictionaryAPI.getSDDAttributes(results);
        }  else if (elementType.equals("sddobject")) {
            GenericFind<SDDObject> query = new GenericFind<SDDObject>();
            List<SDDObject> results = query.findByKeywordWithPages(SDDObject.class,keyword, pageSize, offset);
            return SemanticDataDictionaryAPI.getSDDObjects(results);
        }  else if (elementType.equals("dp2")) {
            GenericFind<DP2> query = new GenericFind<DP2>();
            List<DP2> results = query.findByKeywordWithPages(DP2.class,keyword, pageSize, offset);
            return DP2API.getDP2s(results);
        }  else if (elementType.equals("str")) {
            GenericFind<STR> query = new GenericFind<STR>();
            List<STR> results = query.findByKeywordWithPages(STR.class,keyword, pageSize, offset);
            return STRAPI.getSTRs(results);
        }  else if (elementType.equals("datafile")) {
            GenericFind<DataFile> query = new GenericFind<DataFile>();
            List<DataFile> results = query.findByKeywordWithPages(DataFile.class,keyword, pageSize, offset);
            return DataFileAPI.getDataFiles(results);
        }  else if (elementType.equals("dsg")) {
            GenericFind<DSG> query = new GenericFind<DSG>();
            List<DSG> results = query.findByKeywordWithPages(DSG.class,keyword, pageSize, offset);
            return DSGAPI.getDSGs(results);
        }  else if (elementType.equals("study")) {
            GenericFind<Study> query = new GenericFind<Study>();
            List<Study> results = query.findByKeywordWithPages(Study.class,keyword, pageSize, offset);
            return StudyAPI.getStudies(results);
        }  else if (elementType.equals("studyobjectcollection")) {
            GenericFind<StudyObjectCollection> query = new GenericFind<StudyObjectCollection>();
            List<StudyObjectCollection> results = query.findByKeywordWithPages(StudyObjectCollection.class,keyword, pageSize, offset);
            return StudyObjectCollectionAPI.getStudyObjectCollections(results);
        }  else if (elementType.equals("studyobject")) {
            GenericFind<StudyObject> query = new GenericFind<StudyObject>();
            List<StudyObject> results = query.findByKeywordWithPages(StudyObject.class,keyword, pageSize, offset);
            return StudyObjectAPI.getStudyObjects(results);
        }  else if (elementType.equals("studyrole")) {
            GenericFind<StudyRole> query = new GenericFind<StudyRole>();
            List<StudyRole> results = query.findByKeywordWithPages(StudyRole.class,keyword, pageSize, offset);
            return StudyRoleAPI.getStudyRoles(results);
        }  else if (elementType.equals("virtualcolumn")) {
            GenericFind<VirtualColumn> query = new GenericFind<VirtualColumn>();
            List<VirtualColumn> results = query.findByKeywordWithPages(VirtualColumn.class,keyword, pageSize, offset);
            return VirtualColumnAPI.getVirtualColumns(results);
        }  else if (elementType.equals("platform")) {
            GenericFind<Platform> query = new GenericFind<Platform>();
            List<Platform> results = query.findByKeywordWithPages(Platform.class,keyword, pageSize, offset);
            return PlatformAPI.getPlatforms(results);
        }  else if (elementType.equals("stream")) {
            GenericFind<Stream> query = new GenericFind<Stream>();
            List<Stream> results = query.findByKeywordWithPages(Stream.class,keyword, pageSize, offset);
            return StreamAPI.getStreams(results);
        }  else if (elementType.equals("deployment")) {
            GenericFind<Deployment> query = new GenericFind<Deployment>();
            List<Deployment> results = query.findByKeywordWithPages(Deployment.class,keyword, pageSize, offset);
            return DeploymentAPI.getDeployments(results);
        }  else if (elementType.equals("person")) {
            GenericFind<Person> query = new GenericFind<Person>();
            List<Person> results = query.findByKeywordWithPages(Person.class,keyword, pageSize, offset);
            return PersonAPI.getPeople(results);
        }  else if (elementType.equals("organization")) {
            GenericFind<Organization> query = new GenericFind<Organization>();
            List<Organization> results = query.findByKeywordWithPages(Organization.class,keyword, pageSize, offset);
            return OrganizationAPI.getOrganizations(results);
        }  else if (elementType.equals("place")) {
            GenericFind<Place> query = new GenericFind<Place>();
            List<Place> results = query.findByKeywordWithPages(Place.class,keyword, pageSize, offset);
            return PlaceAPI.getPlaces(results);
        }  else if (elementType.equals("postaladdress")) {
            GenericFind<PostalAddress> query = new GenericFind<PostalAddress>();
            List<PostalAddress> results = query.findByKeywordWithPages(PostalAddress.class,keyword, pageSize, offset);
            return PostalAddressAPI.getPostalAddresses(results);
        }  else if (elementType.equals("kgr")) {
            GenericFind<KGR> query = new GenericFind<KGR>();
            List<KGR> results = query.findByKeywordWithPages(KGR.class,keyword, pageSize, offset);
            return KGRAPI.getKGRs(results);
        } 
        return ok("[getElementsByKeywordWithPage] No valid element type.");
    }

    public Result getTotalElementsByKeyword(String elementType, String keyword) {
        if (keyword.equals("_")) {
            keyword = "";
        }
        if (elementType == null || elementType.isEmpty()) {
            return ok(ApiUtil.createResponse("No elementType has been provided", false));
        }
        Class clazz = GenericFind.getElementClass(elementType);
        if (clazz == null) {        
            return ok(ApiUtil.createResponse("[" + elementType + "] is not a valid elementType", false));
        }
        int totalElements = GenericFind.findTotalByKeyword(clazz, keyword);
        if (totalElements >= 0) {
            String totalElementsJSON = "{\"total\":" + totalElements + "}";
            return ok(ApiUtil.createResponse(totalElementsJSON, true));
        }
        return ok(ApiUtil.createResponse("query method getTotalElementsByKeyword() failed to retrieve total number of [" + elementType + "]", false));
    }
        
    /**
     *   GET ELEMENTS BY KEYWORD AND LANGUAGE WITH PAGE
     */
                
    public Result getElementsByKeywordAndLanguageWithPage(String elementType, String keyword, String language, int pageSize, int offset) {
        if (keyword.equals("_")) {
            keyword = "";
        }
        if (language.equals("_")) {
            language = "";
        }
        if (elementType.equals("instrument")) {
            GenericFind<Instrument> query = new GenericFind<Instrument>();
            List<Instrument> results = query.findByKeywordAndLanguageWithPages(Instrument.class, keyword, language, pageSize, offset);
            return InstrumentAPI.getInstruments(results);
        } else if (elementType.equals("detectorstem")) {
            GenericFind<DetectorStem> query = new GenericFind<DetectorStem>();
            List<DetectorStem> results = query.findByKeywordAndLanguageWithPages(DetectorStem.class, keyword, language, pageSize, offset);
            return DetectorStemAPI.getDetectorStems(results);
        } else if (elementType.equals("detector")) {
            GenericFind<Detector> query = new GenericFind<Detector>();
            List<Detector> results = query.findByKeywordAndLanguageWithPages(Detector.class, keyword, language, pageSize, offset);
            return DetectorAPI.getDetectors(results);
        } else if (elementType.equals("codebook")) {
            GenericFind<Codebook> query = new GenericFind<Codebook>();
            List<Codebook> results = query.findByKeywordAndLanguageWithPages(Codebook.class, keyword, language, pageSize, offset);
            return CodebookAPI.getCodebooks(results);
        } else if (elementType.equals("responseoption")) {
            GenericFind<ResponseOption> query = new GenericFind<ResponseOption>();
            List<ResponseOption> results = query.findByKeywordAndLanguageWithPages(ResponseOption.class, keyword, language, pageSize, offset);
            return ResponseOptionAPI.getResponseOptions(results);
        } else if (elementType.equals("annotationstem")) {
            GenericFind<AnnotationStem> query = new GenericFind<AnnotationStem>();
            List<AnnotationStem> results = query.findByKeywordAndLanguageWithPages(AnnotationStem.class, keyword, language, pageSize, offset);
            return AnnotationStemAPI.getAnnotationStems(results);
        } else if (elementType.equals("annotation")) {
            GenericFind<Annotation> query = new GenericFind<Annotation>();
            List<Annotation> results = query.findByKeywordAndLanguageWithPages(Annotation.class, keyword, language, pageSize, offset);
            return AnnotationAPI.getAnnotations(results);
        } else if (elementType.equals("semanticvariable")) {
            GenericFind<SemanticVariable> query = new GenericFind<SemanticVariable>();
            List<SemanticVariable> results = query.findByKeywordAndLanguageWithPages(SemanticVariable.class, keyword, language, pageSize, offset);
            return SemanticVariableAPI.getSemanticVariables(results);
        } else if (elementType.equals("entity")) {
            GenericFind<Entity> query = new GenericFind<Entity>();
            List<Entity> results = query.findByKeywordAndLanguageWithPages(Entity.class, keyword, language, pageSize, offset);
            return EntityAPI.getEntities(results);
        }  else if (elementType.equals("attribute")) {
            GenericFind<Attribute> query = new GenericFind<Attribute>();
            List<Attribute> results = query.findByKeywordAndLanguageWithPages(Attribute.class, keyword, language, pageSize, offset);
            return AttributeAPI.getAttributes(results);
        }  else if (elementType.equals("unit")) {
            GenericFind<Unit> query = new GenericFind<Unit>();
            List<Unit> results = query.findByKeywordAndLanguageWithPages(Unit.class, keyword, language, pageSize, offset);
            return UnitAPI.getUnits(results);
        /* NOTE: THE CLASSES BELOW DO NOT HAVE LANGUAGE PROPERTY 
        }  else if (elementType.equals("ins")) {
            GenericFind<INS> query = new GenericFind<INS>();
            List<INS> results = query.findByKeywordAndLanguageWithPages(INS.class, keyword, language, pageSize, offset);
            return INSAPI.getINSs(results);
        }  else if (elementType.equals("da")) {
            GenericFind<DA> query = new GenericFind<DA>();
            List<DA> results = query.findByKeywordAndLanguageWithPages(DA.class, keyword, language, pageSize, offset);
            return DAAPI.getDAs(results);
        }  else if (elementType.equals("dd")) {
            GenericFind<DD> query = new GenericFind<DD>();
            List<DD> results = query.findByKeywordAndLanguageWithPages(DD.class, keyword, language, pageSize, offset);
            return DDAPI.getDDs(results);
        }  else if (elementType.equals("sdd")) {
            GenericFind<SDD> query = new GenericFind<SDD>();
            List<SDD> results = query.findByKeywordAndLanguageWithPages(SDD.class, keyword, language, pageSize, offset);
            return SDDAPI.getSDDs(results);
        }  else if (elementType.equals("dp2")) {
            GenericFind<DP2> query = new GenericFind<DP2>();
            List<DP2> results = query.findByKeywordAndLanguageWithPages(DP2.class, keyword, language, pageSize, offset);
            return DP2API.getDP2s(results);
        }  else if (elementType.equals("str")) {
            GenericFind<STR> query = new GenericFind<STR>();
            List<STR> results = query.findByKeywordAndLanguageWithPages(STR.class, keyword, language, pageSize, offset);
            return STRAPI.getSTRs(results);
        }  else if (elementType.equals("datafile")) {
            GenericFind<DataFile> query = new GenericFind<DataFile>();
            List<DataFile> results = query.findByKeywordAndLanguageWithPages(DataFile.class, keyword, language, pageSize, offset);
            return DataFileAPI.getDataFiles(results);
        }  else if (elementType.equals("dsg")) {
            GenericFind<DSG> query = new GenericFind<DSG>();
            List<DSG> results = query.findByKeywordAndLanguageWithPages(DSG.class, keyword, language, pageSize, offset);
            return DSGAPI.getDSGs(results);
        }  else if (elementType.equals("study")) {
            GenericFind<Study> query = new GenericFind<Study>();
            List<Study> results = query.findByKeywordAndLanguageWithPages(Study.class, keyword, language, pageSize, offset);
            return StudyAPI.getStudies(results);
        }  else if (elementType.equals("studyobjectcollection")) {
            GenericFind<StudyObjectCollection> query = new GenericFind<StudyObjectCollection>();
            List<StudyObjectCollection> results = query.findByKeywordAndLanguageWithPages(StudyObjectCollection.class, keyword, language, pageSize, offset);
            return StudyObjectCollectionAPI.getStudyObjectCollections(results);
        }  else if (elementType.equals("studyobject")) {
            GenericFind<StudyObject> query = new GenericFind<StudyObject>();
            List<StudyObject> results = query.findByKeywordAndLanguageWithPages(StudyObject.class, keyword, language, pageSize, offset);
            return StudyObjectAPI.getStudyObjects(results);
        }  else if (elementType.equals("studyrole")) {
            GenericFind<StudyRole> query = new GenericFind<StudyRole>();
            List<StudyRole> results = query.findByKeywordAndLanguageWithPages(StudyRole.class, keyword, language, pageSize, offset);
            return StudyRoleAPI.getStudyRoles(results);
        }  else if (elementType.equals("virtualcolumn")) {
            GenericFind<VirtualColumn> query = new GenericFind<VirtualColumn>();
            List<VirtualColumn> results = query.findByKeywordAndLanguageWithPages(VirtualColumn.class, keyword, language, pageSize, offset);
            return VirtualColumnAPI.getVirtualColumns(results);
        }  else if (elementType.equals("person")) {
            GenericFind<Person> query = new GenericFind<Person>();
            List<Person> results = query.findByKeywordAndLanguageWithPages(Person.class, keyword, language, pageSize, offset);
            return PersonAPI.getPeople(results);
        }  else if (elementType.equals("organization")) {
            GenericFind<Organization> query = new GenericFind<Organization>();
            List<Organization> results = query.findByKeywordAndLanguageWithPages(Organization.class, keyword, language, pageSize, offset);
            return OrganizationAPI.getOrganizations(results);
        }  else if (elementType.equals("place")) {
            GenericFind<Place> query = new GenericFind<Place>();
            List<Place> results = query.findByKeywordAndLanguageWithPages(Place.class, keyword, language, pageSize, offset);
            return PlaceAPI.getPlaces(results);
        }  else if (elementType.equals("postaladdress")) {
            GenericFind<PostalAddress> query = new GenericFind<PostalAddress>();
            List<PostalAddress> results = query.findByKeywordAndLanguageWithPages(PostalAddress.class, keyword, language, pageSize, offset);
            return PostalAddressAPI.getPostalAddresses(results);
        }  else if (elementType.equals("kgr")) {
            GenericFind<KGR> query = new GenericFind<KGR>();
            List<KGR> results = query.findByKeywordAndLanguageWithPages(KGR.class, keyword, language, pageSize, offset);
            return KGRAPI.getKGRs(results);
        */
        } 
        return ok("[getElementsByKeywordAndLanguageWithPage] No valid element type.");
    }

    public Result getTotalElementsByKeywordAndLanguage(String elementType, String keyword, String language) {
        //System.out.println("ElementType: " + elementType);
        if (keyword.equals("_")) {
            keyword = "";
        }
        if (language.equals("_")) {
            language = "";
        }
        if (elementType == null || elementType.isEmpty()) {
            return ok(ApiUtil.createResponse("No elementType has been provided", false));
        }
        Class clazz = GenericFind.getElementClass(elementType);
        if (clazz == null) {        
            return ok(ApiUtil.createResponse("[" + elementType + "] is not a valid elementType", false));
        }
        int totalElements = GenericFind.findTotalByKeywordAndLanguage(clazz, keyword, language);
        if (totalElements >= 0) {
            String totalElementsJSON = "{\"total\":" + totalElements + "}";
            return ok(ApiUtil.createResponse(totalElementsJSON, true));
        }
        return ok(ApiUtil.createResponse("query method getTotalElementsByKeyword() failed to retrieve total number of [" + elementType + "]", false));
    }
        
    /**
     *   GET ELEMENTS BY MANAGER EMAIL WITH PAGE
     */

    public Result getElementsByManagerEmail(String elementType, String managerEmail, int pageSize, int offset) {
        if (managerEmail == null || managerEmail.isEmpty()) {
            return ok(ApiUtil.createResponse("No Manager Email has been provided", false));
        }
        if (elementType.equals("semanticvariable")) {
            GenericFind<SemanticVariable> query = new GenericFind<SemanticVariable>();
            List<SemanticVariable> results = query.findByManagerEmailWithPages(SemanticVariable.class, managerEmail, pageSize, offset);
            return SemanticVariableAPI.getSemanticVariables(results);
        } else if (elementType.equals("instrument")) {
            GenericFind<Instrument> query = new GenericFind<Instrument>();
            List<Instrument> results = query.findByManagerEmailWithPages(Instrument.class, managerEmail, pageSize, offset);
            return InstrumentAPI.getInstruments(results);
        } else if (elementType.equals("instrumentinstance")) {
            GenericFind<InstrumentInstance> query = new GenericFind<InstrumentInstance>();
            List<InstrumentInstance> results = query.findByManagerEmailWithPages(InstrumentInstance.class, managerEmail, pageSize, offset);
            return VSTOIInstanceAPI.getInstrumentInstances(results);
        } else if (elementType.equals("detectorinstance")) {
            GenericFind<DetectorInstance> query = new GenericFind<DetectorInstance>();
            List<DetectorInstance> results = query.findByManagerEmailWithPages(DetectorInstance.class, managerEmail, pageSize, offset);
            return VSTOIInstanceAPI.getDetectorInstances(results);
        } else if (elementType.equals("platforminstance")) {
            GenericFind<PlatformInstance> query = new GenericFind<PlatformInstance>();
            List<PlatformInstance> results = query.findByManagerEmailWithPages(PlatformInstance.class, managerEmail, pageSize, offset);
            return VSTOIInstanceAPI.getPlatformInstances(results);
        }  else if (elementType.equals("detectorstem")) {
            GenericFind<DetectorStem> query = new GenericFind<DetectorStem>();
            List<DetectorStem> results = query.findByManagerEmailWithPages(DetectorStem.class, managerEmail, pageSize, offset);
            return DetectorStemAPI.getDetectorStems(results);
        }  else if (elementType.equals("detector")) {
            GenericFind<Detector> query = new GenericFind<Detector>();
            List<Detector> results = query.findByManagerEmailWithPages(Detector.class, managerEmail, pageSize, offset);
            return DetectorAPI.getDetectors(results);
        }  else if (elementType.equals("codebook")) {
            GenericFind<Codebook> query = new GenericFind<Codebook>();
            List<Codebook> results = query.findByManagerEmailWithPages(Codebook.class, managerEmail, pageSize, offset);
            return CodebookAPI.getCodebooks(results);
        }  else if (elementType.equals("responseoption")) {
            GenericFind<ResponseOption> query = new GenericFind<ResponseOption>();
            List<ResponseOption> results = query.findByManagerEmailWithPages(ResponseOption.class, managerEmail, pageSize, offset);
            return ResponseOptionAPI.getResponseOptions(results);
        }  else if (elementType.equals("annotationstem")) {
            GenericFind<AnnotationStem> query = new GenericFind<AnnotationStem>();
            List<AnnotationStem> results = query.findByManagerEmailWithPages(AnnotationStem.class, managerEmail, pageSize, offset);
            return AnnotationStemAPI.getAnnotationStems(results);
        }  else if (elementType.equals("annotation")) {
            GenericFind<Annotation> query = new GenericFind<Annotation>();
            List<Annotation> results = query.findByManagerEmailWithPages(Annotation.class, managerEmail, pageSize, offset);
            return AnnotationAPI.getAnnotations(results);
        }  else if (elementType.equals("ins")) {
            GenericFind<INS> query = new GenericFind<INS>();
            List<INS> results = query.findByManagerEmailWithPages(INS.class, managerEmail, pageSize, offset);
            return INSAPI.getINSs(results);
        }  else if (elementType.equals("da")) {
            GenericFind<DA> query = new GenericFind<DA>();
            List<DA> results = query.findByManagerEmailWithPages(DA.class, managerEmail, pageSize, offset);
            return DAAPI.getDAs(results);
        }  else if (elementType.equals("dd")) {
            GenericFind<DD> query = new GenericFind<DD>();
            List<DD> results = query.findByManagerEmailWithPages(DD.class, managerEmail, pageSize, offset);
            return DDAPI.getDDs(results);
        }  else if (elementType.equals("sdd")) {
            GenericFind<SDD> query = new GenericFind<SDD>();
            List<SDD> results = query.findByManagerEmailWithPages(SDD.class, managerEmail, pageSize, offset);
            return SDDAPI.getSDDs(results);
        }  else if (elementType.equals("semanticdatadictionary")) {
            GenericFind<SemanticDataDictionary> query = new GenericFind<SemanticDataDictionary>();
            List<SemanticDataDictionary> results = query.findByManagerEmailWithPages(SemanticDataDictionary.class, managerEmail, pageSize, offset);
            return SemanticDataDictionaryAPI.getSemanticDataDictionaries(results);
        }  else if (elementType.equals("dp2")) {
            GenericFind<DP2> query = new GenericFind<DP2>();
            List<DP2> results = query.findByManagerEmailWithPages(DP2.class, managerEmail, pageSize, offset);
            return DP2API.getDP2s(results);
        }  else if (elementType.equals("str")) {
            GenericFind<STR> query = new GenericFind<STR>();
            List<STR> results = query.findByManagerEmailWithPages(STR.class, managerEmail, pageSize, offset);
            return STRAPI.getSTRs(results);
        }  else if (elementType.equals("datafile")) {
            GenericFind<DataFile> query = new GenericFind<DataFile>();
            List<DataFile> results = query.findByManagerEmailWithPages(DataFile.class, managerEmail, pageSize, offset);
            return DataFileAPI.getDataFiles(results);
        }  else if (elementType.equals("dsg")) {
            GenericFind<DSG> query = new GenericFind<DSG>();
            List<DSG> results = query.findByManagerEmailWithPages(DSG.class, managerEmail, pageSize, offset);
            return DSGAPI.getDSGs(results);
        }  else if (elementType.equals("study")) {
            GenericFind<Study> query = new GenericFind<Study>();
            List<Study> results = query.findByManagerEmailWithPages(Study.class, managerEmail, pageSize, offset);
            return StudyAPI.getStudies(results);
        }  else if (elementType.equals("studyobjectcollection")) {
            GenericFind<StudyObjectCollection> query = new GenericFind<StudyObjectCollection>();
            List<StudyObjectCollection> results = query.findByManagerEmailWithPages(StudyObjectCollection.class, managerEmail, pageSize, offset);
            return StudyObjectCollectionAPI.getStudyObjectCollections(results);
        }  else if (elementType.equals("studyobject")) {
            GenericFind<StudyObject> query = new GenericFind<StudyObject>();
            List<StudyObject> results = query.findByManagerEmailWithPages(StudyObject.class, managerEmail, pageSize, offset);
            return StudyObjectAPI.getStudyObjects(results);
        }  else if (elementType.equals("studyrole")) {
            GenericFind<StudyRole> query = new GenericFind<StudyRole>();
            List<StudyRole> results = query.findByManagerEmailWithPages(StudyRole.class, managerEmail, pageSize, offset);
            return StudyRoleAPI.getStudyRoles(results);
        }  else if (elementType.equals("virtualcolumn")) {
            GenericFind<VirtualColumn> query = new GenericFind<VirtualColumn>();
            List<VirtualColumn> results = query.findByManagerEmailWithPages(VirtualColumn.class, managerEmail, pageSize, offset);
            return VirtualColumnAPI.getVirtualColumns(results);
        }  else if (elementType.equals("platform")) {
            GenericFind<Platform> query = new GenericFind<Platform>();
            List<Platform> results = query.findByManagerEmailWithPages(Platform.class, managerEmail, pageSize, offset);
            return PlatformAPI.getPlatforms(results);
        }  else if (elementType.equals("stream")) {
            GenericFind<Stream> query = new GenericFind<Stream>();
            List<Stream> results = query.findByManagerEmailWithPages(Stream.class, managerEmail, pageSize, offset);
            return StreamAPI.getStreams(results);
        }  else if (elementType.equals("deployment")) {
            GenericFind<Deployment> query = new GenericFind<Deployment>();
            List<Deployment> results = query.findByManagerEmailWithPages(Deployment.class, managerEmail, pageSize, offset);
            return DeploymentAPI.getDeployments(results);
        }  else if (elementType.equals("person")) {
            GenericFind<Person> query = new GenericFind<Person>();
            List<Person> results = query.findByManagerEmailWithPages(Person.class, managerEmail, pageSize, offset);
            return PersonAPI.getPeople(results);
        }  else if (elementType.equals("organization")) {
            GenericFind<Organization> query = new GenericFind<Organization>();
            List<Organization> results = query.findByManagerEmailWithPages(Organization.class, managerEmail, pageSize, offset);
            return OrganizationAPI.getOrganizations(results);
        }  else if (elementType.equals("place")) {
            GenericFind<Place> query = new GenericFind<Place>();
            List<Place> results = query.findByManagerEmailWithPages(Place.class, managerEmail, pageSize, offset);
            return PlaceAPI.getPlaces(results);
        }  else if (elementType.equals("postaladdress")) {
            GenericFind<PostalAddress> query = new GenericFind<PostalAddress>();
            List<PostalAddress> results = query.findByManagerEmailWithPages(PostalAddress.class, managerEmail, pageSize, offset);
            return PostalAddressAPI.getPostalAddresses(results);
        }  else if (elementType.equals("kgr")) {
            GenericFind<KGR> query = new GenericFind<KGR>();
            List<KGR> results = query.findByManagerEmailWithPages(KGR.class, managerEmail, pageSize, offset);
            return KGRAPI.getKGRs(results);
        } 
        return ok("[getElementsByManagerEmail] No valid element type.");

    }

    public Result getTotalElementsByManagerEmail(String elementType, String managerEmail){
        //System.out.println("SIRElementAPI: getTotalElementsByManagerEmail");
        if (elementType == null || elementType.isEmpty()) {
            return ok(ApiUtil.createResponse("No elementType has been provided", false));
        }
        Class clazz = GenericFind.getElementClass(elementType);
        if (clazz == null) {        
            return ok(ApiUtil.createResponse("[" + elementType + "] is not a valid elementType", false));
        }
        
        int totalElements = totalElements = GenericFind.findTotalByManagerEmail(clazz, managerEmail);
        if (totalElements >= 0) {
            String totalElementsJSON = "{\"total\":" + totalElements + "}";
            return ok(ApiUtil.createResponse(totalElementsJSON, true));
        }
        return ok(ApiUtil.createResponse("query method getTotalElements() failed to retrieve total number of element", false));

    }

    public Result usage(String elementUri){
        HADatAcThing object = URIPage.objectFromUri(elementUri);
        if (object == null || object.getHascoTypeUri() == null) {
            return ok("[usage] No valid element type. Provided uri is null.");
        }
        String elementType = object.getHascoTypeUri();
        //System.out.println("SIREelementAPI: element type is " + elementType);
        if (elementType.equals(VSTOI.DETECTOR)) {
            List<ContainerSlot> results = Detector.usage(elementUri);
            //System.out.println("SIREelementAPI: Results is " + results.size());
            return ContainerSlotAPI.getContainerSlots(results);
        } //else if (elementType.equals("detector")) {
        //    int totalDetectors = Detector.findTotalByManagerEmail(managerEmail);
        //    String totalDetectorsJSON = "{\"total\":" + totalDetectors + "}";
        //    return ok(ApiUtil.createResponse(totalDetectorsJSON, true));
        //}
        return ok("[usage] No valid element type.");
    }

    public Result derivation(String elementUri){
        HADatAcThing object = URIPage.objectFromUri(elementUri);
        if (object == null || object.getHascoTypeUri() == null) {
            return ok("[derivation] No valid element type. Provided uri is null.");
        }
        String elementType = object.getHascoTypeUri();
        //System.out.println("SIREelementAPI: element type is " + elementType);
        if (elementType.equals(VSTOI.DETECTOR)) {
            List<Detector> results = Detector.derivationDetector(elementUri);
            //System.out.println("SIREelementAPI: Results is " + results.size());
            return DetectorAPI.getDetectors(results);
        } //else if (elementType.equals("detector")) {
        //    int totalDetectors = Detector.findTotalByManagerEmail(managerEmail);
        //    String totalDetectorsJSON = "{\"total\":" + totalDetectors + "}";
        //    return ok(ApiUtil.createResponse(totalDetectorsJSON, true));
        //}
        return ok("[derivation] No valid element type.");
    }

    public Result hascoType(String classUri){
        if (classUri == null || classUri.isEmpty()) {
            return ok("[hascoType] No valid classUri has been provided.");
        }
        GenericInstance instance = new GenericInstance();
        String response = instance.getHascoType(classUri);
        if (response != null) {
            String respJSON = "{\"hascoType\":\"" + response + "\"}";
            return ok(ApiUtil.createResponse(respJSON, true));
        }     
        return ok("No recognizable HASCO type.");
    }

}
