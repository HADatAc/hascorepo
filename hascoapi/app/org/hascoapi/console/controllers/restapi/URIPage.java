package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.hascoapi.Constants;
import org.hascoapi.RepositoryInstance;
import org.hascoapi.entity.pojo.*;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.HAScOMapper;
import org.hascoapi.utils.Utils;
import org.hascoapi.vocabularies.FOAF;
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.vocabularies.SCHEMA;
import org.hascoapi.vocabularies.SIO;
import org.hascoapi.vocabularies.VSTOI;
import play.mvc.Controller;
import play.mvc.Result;

public class URIPage extends Controller {

    public Result getUri(String uri) {

        //System.out.println("URIPage.getUri() with uri [" + uri + "]");
        if (!uri.startsWith("http://") && !uri.startsWith("https://")) {
            return ok(ApiUtil.createResponse("[" + uri + "] is an invalid URI", false));
        }

        HADatAcThing finalResult = URIPage.objectFromUri(uri);
        if (finalResult == null) {
            return ok(ApiUtil.createResponse("Uri [" + uri + "] returned no object from the knowledge graph", false));
        }

        String hascoTypeUri = finalResult.getHascoTypeUri();
        if (hascoTypeUri == null || hascoTypeUri.equals("")) {
            String typeUri = finalResult.getTypeUri();
            if (typeUri == null || typeUri.equals("")) {
                return ok(ApiUtil.createResponse("No type-specific instance found for uri [" + uri + "]", false));
            }
        }

        return processResult(finalResult, finalResult.getHascoTypeUri(), uri);

    }

    public Result uriGen(String elementType) {
        if (elementType == null) {
            return ok(ApiUtil.createResponse("No elementType has been provided.", false));
        }
        String repoUri = RepositoryInstance.getInstance().getBaseURL();
        if (repoUri == null || repoUri.isEmpty()) {
            return ok(ApiUtil.createResponse("Repository's base URL needs to be setup before URIs can be generated.", false));
        }

        String shortPrefix = Utils.shortPrefix(elementType);
        if (shortPrefix == null) {
            return ok(ApiUtil.createResponse("Cannot generate URI for elementType [" + elementType + "]", false));
        }

        if (!repoUri.endsWith("/")) {
            repoUri += "/";
        }

        String newUri = Utils.uriGen(repoUri, shortPrefix);
        String newUriJSON = "{\"uri\":" + newUri + "}";

        return ok(ApiUtil.createResponse(newUriJSON, true));
    }
    
    private static String getCurrentUserId() {
        // Implement this method to return the current user's ID
        // For example, if using Spring Security, you might do:
        // return SecurityContextHolder.getContext().getAuthentication().getName();
        return "12345";  // Placeholder implementation
    }
    
    private static int convertEmailToNumber(String email) {
        try {
            // Create a MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // Digest the email bytes
            byte[] hashBytes = md.digest(email.getBytes(StandardCharsets.UTF_8));

            // Convert the hash bytes to a positive integer
            int hashInt = Math.abs(bytesToInt(hashBytes));

            // Map the integer to a 5-digit number (range 10000 to 99999)
            int fiveDigitNumber = 10000 + (hashInt % 90000);

            return fiveDigitNumber;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    private static int bytesToInt(byte[] bytes) {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result <<= 8;
            result |= (bytes[i] & 0xFF);
        }
        return result;
    }

    public static HADatAcThing objectFromUri(String uri) {
        //System.out.println("URIPage.objectFromUri(): URI [" + uri + "]");
        String typeUri = "";
        try {

            /*
             * Now uses GenericInstance to process URI against TripleStore content
             */

            Object finalResult = null;
            GenericInstance result = GenericInstance.find(uri);

            if (result == null) {
                System.out.println("[WARNING] URIPage.objectFromUri(): No generic instance found for uri [" + uri + "]");
                return null;
            }

            //System.out.println("URIPage.objectFromUri(): HASCO TYPE [" + result.getHascoTypeUri() + "]");

            /*
             * if (result.getHascoTypeUri() == null || result.getHascoTypeUri().isEmpty()) {
             * System.out.println("inside getUri(): typeUri [" + result.getTypeUri() + "]");
             * if (!result.getTypeUri().equals("http://www.w3.org/2002/07/owl#Class")) {
             * return notFound(ApiUtil.createResponse("No valid HASCO type found for uri ["
             * + uri + "]", false));
             * }
             * }
             */

            if (result.getHascoTypeUri().equals(VSTOI.ANNOTATION)) {
                finalResult = Annotation.find(uri);
            } else if (result.getHascoTypeUri().equals(VSTOI.ANNOTATION_STEM)) {
                finalResult = AnnotationStem.find(uri);
            } else if (result.getHascoTypeUri().equals(SIO.ATTRIBUTE)) {
                finalResult = Attribute.find(uri);
            } else if (result.getHascoTypeUri().equals(VSTOI.CODEBOOK)) {
                finalResult = Codebook.find(uri);            
            } else if (result.getHascoTypeUri().equals(VSTOI.CODEBOOK_SLOT)) {
                finalResult = CodebookSlot.find(uri);
            } else if (result.getHascoTypeUri().equals(VSTOI.CONTAINER_SLOT)) {
                finalResult = ContainerSlot.find(uri);
            } else if (result.getHascoTypeUri().equals(HASCO.DATA_ACQUISITION)) {
                finalResult = DA.find(uri);
            } else if (result.getHascoTypeUri().equals(HASCO.DATAFILE)) {
                finalResult = DataFile.find(uri);
            } else if (result.getHascoTypeUri().equals(HASCO.DD)) {
                finalResult = DD.find(uri);
            } else if (result.getHascoTypeUri().equals(VSTOI.DEPLOYMENT)) {
                finalResult = Deployment.find(uri);
            } else if (result.getHascoTypeUri().equals(VSTOI.DETECTOR)) {
                finalResult = Detector.findDetector(uri);
            } else if (result.getHascoTypeUri().equals(VSTOI.DETECTOR_INSTANCE)) {
                finalResult = DetectorInstance.find(uri);
            } else if (result.getHascoTypeUri().equals(VSTOI.DETECTOR_STEM)) {
                finalResult = DetectorStem.find(uri);
            } else if (result.getHascoTypeUri().equals(HASCO.DP2)) {
                finalResult = DP2.find(uri);
            } else if (result.getHascoTypeUri().equals(HASCO.DSG)) {
                finalResult = DSG.find(uri);
            } else if (result.getHascoTypeUri().equals(SIO.ENTITY)) {
                finalResult = Entity.find(uri);
            } else if (result.getHascoTypeUri().equals(HASCO.INS)) {
                finalResult = INS.find(uri);
            } else if (result.getHascoTypeUri().equals(VSTOI.INSTRUMENT)) {
                finalResult = Instrument.find(uri);
            } else if (result.getHascoTypeUri().equals(VSTOI.INSTRUMENT_INSTANCE)) {
                finalResult = InstrumentInstance.find(uri);
            } else if (result.getHascoTypeUri().equals(HASCO.KNOWLEDGE_GRAPH)) {
                finalResult = KGR.find(uri);
            } else if (result.getHascoTypeUri().equals(FOAF.ORGANIZATION)) {
                finalResult = Organization.find(uri);
            } else if (result.getHascoTypeUri().equals(FOAF.PERSON)) {
                finalResult = Person.find(uri);
            } else if (result.getHascoTypeUri().equals(SCHEMA.PLACE)) {
                finalResult = Place.find(uri);
            } else if (result.getHascoTypeUri().equals(VSTOI.PLATFORM)) {
                finalResult = Platform.find(uri);
            } else if (result.getHascoTypeUri().equals(VSTOI.PLATFORM_INSTANCE)) {
                finalResult = PlatformInstance.find(uri);
            } else if (result.getHascoTypeUri().equals(HASCO.POSSIBLE_VALUE)) {
                finalResult = PossibleValue.find(uri);
            } else if (result.getHascoTypeUri().equals(SCHEMA.POSTAL_ADDRESS)) {
                finalResult = PostalAddress.find(uri);
            } else if (result.getHascoTypeUri().equals(VSTOI.RESPONSE_OPTION)) {
                finalResult = ResponseOption.find(uri);
            } else if (result.getHascoTypeUri().equals(HASCO.SDD)) {
                finalResult = SDD.find(uri);
            } else if (result.getHascoTypeUri().equals(HASCO.SDD_ATTRIBUTE)) {
                finalResult = SDDAttribute.find(uri);
            } else if (result.getHascoTypeUri().equals(HASCO.SDD_OBJECT)) {
                finalResult = SDDObject.find(uri);
            } else if (result.getHascoTypeUri().equals(HASCO.SEMANTIC_DATA_DICTIONARY)) {
                finalResult = SemanticDataDictionary.find(uri);
            } else if (result.getHascoTypeUri().equals(HASCO.SEMANTIC_VARIABLE)) {
                finalResult = SemanticVariable.find(uri);
            } else if (result.getHascoTypeUri().equals(HASCO.STR)) {
                finalResult = STR.find(uri);
            } else if (result.getHascoTypeUri().equals(HASCO.STREAM)) {
                finalResult = Stream.find(uri);
            } else if (result.getHascoTypeUri().equals(HASCO.STUDY)) {
                finalResult = Study.find(uri);
            } else if (result.getHascoTypeUri().equals(HASCO.STUDY_OBJECT)) {
                finalResult = StudyObject.find(uri);
            } else if (result.getHascoTypeUri().equals(HASCO.STUDY_OBJECT_COLLECTION)) {
                finalResult = StudyObjectCollection.find(uri);
            } else if (result.getHascoTypeUri().equals(HASCO.STUDY_ROLE)) {
                finalResult = StudyRole.find(uri);
            } else if (result.getHascoTypeUri().equals(VSTOI.SUBCONTAINER)) {
                finalResult = Subcontainer.find(uri);
            } else if (result.getHascoTypeUri().equals(SIO.UNIT)) {
                finalResult = Unit.find(uri);
            } else if (result.getHascoTypeUri().equals(HASCO.VIRTUAL_COLUMN)) {
                finalResult = VirtualColumn.find(uri);
            } else {
                finalResult = result;
            }
            return (HADatAcThing) finalResult;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Result processResult(Object result, String typeResult, String uri) {
        ObjectMapper mapper = HAScOMapper.getFiltered("full",typeResult);

        //System.out.println("[RestAPI] generating JSON for following object: " + uri + " and typeResult: " + typeResult);
        JsonNode jsonObject = null;
        try {
            ObjectNode obj = mapper.convertValue(result, ObjectNode.class);
            jsonObject = mapper.convertValue(obj, JsonNode.class);
            //System.out.println(org.hascoapi.console.controllers.restapi.URIPage.prettyPrintJsonString(jsonObject));
        } catch (Exception e) {
            e.printStackTrace();
            return ok(ApiUtil.createResponse("Error processing the json object for URI [" + uri + "]", false));
        }
        return ok(ApiUtil.createResponse(jsonObject, true));
    }

    public static String prettyPrintJsonString(JsonNode jsonNode) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object json = mapper.readValue(jsonNode.toString(), Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
