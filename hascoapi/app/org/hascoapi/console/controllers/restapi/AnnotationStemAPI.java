package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import org.hascoapi.Constants;
import org.hascoapi.entity.pojo.Annotation;
import org.hascoapi.entity.pojo.AnnotationStem;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.HAScOMapper;
import org.hascoapi.vocabularies.VSTOI;
import play.mvc.Controller;
import play.mvc.Result;
import static org.hascoapi.Constants.*;
import java.util.List;


public class AnnotationStemAPI extends Controller {

    private Result createAnnotationStemResult(AnnotationStem annotationStem) {
        annotationStem.save();
        return ok(ApiUtil.createResponse("AnnotationStem <" + annotationStem.getUri() + "> has been CREATED.", true));
    }

    public Result createAnnotationStemsForTesting() {
        AnnotationStem testAnnotationStem1 = AnnotationStem.find(TEST_ANNOTATION_STEM1_URI);
        AnnotationStem testAnnotationStem2 = AnnotationStem.find(TEST_ANNOTATION_STEM2_URI);
        AnnotationStem testAnnotationStemInstruction = AnnotationStem.find(TEST_ANNOTATION_STEM_INSTRUCTION_URI);
        AnnotationStem testAnnotationStemPage = AnnotationStem.find(TEST_ANNOTATION_STEM_PAGE_URI);
        AnnotationStem testAnnotationStemDateField = AnnotationStem.find(TEST_ANNOTATION_STEM_DATEFIELD_URI);
        AnnotationStem testAnnotationStemCopyright = AnnotationStem.find(TEST_ANNOTATION_STEM_COPYRIGHT_URI);
        if (testAnnotationStem1 != null) {
            return ok(ApiUtil.createResponse("TestAnnotationStem1 already exists.", false));
        } else if (testAnnotationStem2 != null) {
            return ok(ApiUtil.createResponse("TestAnnotationStem2 already exists.", false));
        } else if (testAnnotationStemInstruction != null) {
            return ok(ApiUtil.createResponse("TestAnnotationStemInstruction already exists.", false));
        } else if (testAnnotationStemPage != null) {
            return ok(ApiUtil.createResponse("TestAnnotationStemPage already exists.", false));
        } else if (testAnnotationStemDateField != null) {
            return ok(ApiUtil.createResponse("TestAnnotationStemDateField already exists.", false));
        } else if (testAnnotationStemCopyright != null) {
            return ok(ApiUtil.createResponse("TestAnnotationStemCopyright already exists.", false));
        } else {
            testAnnotationStem1 = new AnnotationStem();
            testAnnotationStem1.setUri(TEST_ANNOTATION_STEM1_URI);
            testAnnotationStem1.setLabel("Test Annotation Stem 1");
            testAnnotationStem1.setTypeUri(VSTOI.ANNOTATION_STEM);
            testAnnotationStem1.setHascoTypeUri(VSTOI.ANNOTATION_STEM);
            testAnnotationStem1.setComment("This is a dummy Annotation Stem 1 created to test the SIR API.");
            testAnnotationStem1.setHasContent("Considering your feelings during the last two weeks, select the best answer for each question.");
            testAnnotationStem1.setHasLanguage("en"); // ISO 639-1
            testAnnotationStem1.setHasVersion("1");
            testAnnotationStem1.setHasSIRManagerEmail("me@example.com");
            testAnnotationStem1.setNamedGraph(Constants.TEST_KB);
            testAnnotationStem1.save();

            testAnnotationStem2 = new AnnotationStem();
            testAnnotationStem2.setUri(TEST_ANNOTATION_STEM2_URI);
            testAnnotationStem2.setLabel("Test Annotation Stem 2");
            testAnnotationStem2.setTypeUri(VSTOI.ANNOTATION_STEM);
            testAnnotationStem2.setHascoTypeUri(VSTOI.ANNOTATION_STEM);
            testAnnotationStem2.setComment("This is a dummy Annotation Stem 2 created to test the SIR API.");
            testAnnotationStem2.setHasContent("Page");
            testAnnotationStem2.setHasLanguage("en"); // ISO 639-1
            testAnnotationStem2.setHasVersion("1");
            testAnnotationStem2.setHasSIRManagerEmail("me@example.com");
            testAnnotationStem2.setNamedGraph(Constants.TEST_KB);
            testAnnotationStem2.save();

            testAnnotationStemInstruction = new AnnotationStem();
            testAnnotationStemInstruction.setUri(TEST_ANNOTATION_STEM_INSTRUCTION_URI);
            testAnnotationStemInstruction.setLabel("Test Annotation Stem Instruction");
            testAnnotationStemInstruction.setTypeUri(VSTOI.ANNOTATION_STEM);
            testAnnotationStemInstruction.setHascoTypeUri(VSTOI.ANNOTATION_STEM);
            testAnnotationStemInstruction.setComment("This is a dummy Annotation Stem Instruction created to test the SIR API.");
            testAnnotationStemInstruction.setHasContent("Please put a circle around the word that shows how often each of these things happens to you. There are no right or wrong answers. ");
            testAnnotationStemInstruction.setHasLanguage("en"); // ISO 639-1
            testAnnotationStemInstruction.setHasVersion("1");
            testAnnotationStemInstruction.setHasSIRManagerEmail("me@example.com");
            testAnnotationStemInstruction.setNamedGraph(Constants.TEST_KB);
            testAnnotationStemInstruction.save();

            testAnnotationStemPage = new AnnotationStem();
            testAnnotationStemPage.setUri(TEST_ANNOTATION_STEM_PAGE_URI);
            testAnnotationStemPage.setLabel("Test Annotation Stem Page");
            testAnnotationStemPage.setTypeUri(VSTOI.ANNOTATION_STEM);
            testAnnotationStemPage.setHascoTypeUri(VSTOI.ANNOTATION_STEM);
            testAnnotationStemPage.setComment("This is a dummy Annotation Stem Page created to test the SIR API.");
            testAnnotationStemPage.setHasContent("Page ");
            testAnnotationStemPage.setHasLanguage("en"); // ISO 639-1
            testAnnotationStemPage.setHasVersion("1");
            testAnnotationStemPage.setHasSIRManagerEmail("me@example.com");
            testAnnotationStemPage.setNamedGraph(Constants.TEST_KB);
            testAnnotationStemPage.save();

            testAnnotationStemDateField = new AnnotationStem();
            testAnnotationStemDateField.setUri(TEST_ANNOTATION_STEM_DATEFIELD_URI);
            testAnnotationStemDateField.setLabel("Test Annotation Stem DateField");
            testAnnotationStemDateField.setTypeUri(VSTOI.ANNOTATION_STEM);
            testAnnotationStemDateField.setHascoTypeUri(VSTOI.ANNOTATION_STEM);
            testAnnotationStemDateField.setComment("This is a dummy Annotation Stem DateField created to test the SIR API.");
            testAnnotationStemDateField.setHasContent("Date: ____________ ");
            testAnnotationStemDateField.setHasLanguage("en"); // ISO 639-1
            testAnnotationStemDateField.setHasVersion("1");
            testAnnotationStemDateField.setHasSIRManagerEmail("me@example.com");
            testAnnotationStemDateField.setNamedGraph(Constants.TEST_KB);
            testAnnotationStemDateField.save();

            testAnnotationStemCopyright = new AnnotationStem();
            testAnnotationStemCopyright.setUri(TEST_ANNOTATION_STEM_COPYRIGHT_URI);
            testAnnotationStemCopyright.setLabel("Test Annotation Stem Copyright");
            testAnnotationStemCopyright.setTypeUri(VSTOI.ANNOTATION_STEM);
            testAnnotationStemCopyright.setHascoTypeUri(VSTOI.ANNOTATION_STEM);
            testAnnotationStemCopyright.setComment("This is a dummy Annotation Stem Copyright created to test the SIR API.");
            testAnnotationStemCopyright.setHasContent("Copyright (c) 2000 HADatAc.org");
            testAnnotationStemCopyright.setHasLanguage("en"); // ISO 639-1
            testAnnotationStemCopyright.setHasVersion("1");
            testAnnotationStemCopyright.setHasSIRManagerEmail("me@example.com");
            testAnnotationStemCopyright.setNamedGraph(Constants.TEST_KB);
            testAnnotationStemCopyright.save();

            return ok(ApiUtil.createResponse("Test Annotation Stems 1, 2, Instruction, Page, DateField and Copyright have been CREATED.", true));
        }
    }

    public Result createAnnotationStem(String json) {
        if (json == null || json.equals("")) {
            return ok(ApiUtil.createResponse("No json content has been provided.", false));
        }
        //System.out.println("(CreateAnnotationStem) Value of json: [" + json + "]");
        ObjectMapper objectMapper = new ObjectMapper();
        AnnotationStem newAnnotationStem;
        try {
            //convert json string to Instrument instance
            newAnnotationStem  = objectMapper.readValue(json, AnnotationStem.class);
        } catch (Exception e) {
            //System.out.println("(createDetector) Failed to parse json.");
            return ok(ApiUtil.createResponse("Failed to parse json.", false));
        }
        return createAnnotationStemResult(newAnnotationStem);
    }

    private Result deleteAnnotationStemResult(AnnotationStem annotationStem) {
        String uri = annotationStem.getUri();
        annotationStem.delete();
        return ok(ApiUtil.createResponse("Annotation Stem <" + uri + "> has been DELETED.", true));
    }

    public Result deleteAnnotationStemsForTesting(){
        AnnotationStem test1 = AnnotationStem.find(TEST_ANNOTATION_STEM1_URI);
        AnnotationStem test2 = AnnotationStem.find(TEST_ANNOTATION_STEM2_URI);
        AnnotationStem testInstruction = AnnotationStem.find(TEST_ANNOTATION_STEM_INSTRUCTION_URI);
        AnnotationStem testPage = AnnotationStem.find(TEST_ANNOTATION_STEM_PAGE_URI);
        AnnotationStem testDateField = AnnotationStem.find(TEST_ANNOTATION_STEM_DATEFIELD_URI);
        AnnotationStem testCopyright = AnnotationStem.find(TEST_ANNOTATION_STEM_COPYRIGHT_URI);
        String msg = "";

        if (test1 == null) {
            msg += "There is no Test Annotation Stem 1. ";
        } else {
            test1.setNamedGraph(Constants.TEST_KB);
            test1.delete();
        }
        
        if (test2 == null) {
            msg += "There is no Test Annotation Stem 2. ";
        } else {
            test2.setNamedGraph(Constants.TEST_KB);
            test2.delete();
        }

        if (testInstruction == null) {
            msg += "There is no Test Annotation Stem Instruction. ";
        } else {
            testInstruction.setNamedGraph(Constants.TEST_KB);
            testInstruction.delete();
        }
            
        if (testPage == null) {
            msg += "There is no Test Annotation Stem Page. ";
        } else { 
            testPage.setNamedGraph(Constants.TEST_KB);
            testPage.delete();
        } 
        
        if (testDateField == null) {
            msg += "There is no Test Annotation Stem DateField. ";
        } else {
            testDateField.setNamedGraph(Constants.TEST_KB);
            testDateField.delete();
        }
        
        if (testCopyright == null) {
            msg += "There is no Test Annotation Stem Copyright. ";
        } else {
            testCopyright.setNamedGraph(Constants.TEST_KB);
            testCopyright.delete();
        }

        if (msg.isEmpty()) {
            return ok(ApiUtil.createResponse("Test Annotation Stems 1, 2, Instruction, Page, DateField, and Copyright have been DELETED.", true));
        }
        return ok(ApiUtil.createResponse(msg, true));
    }

    public Result deleteAnnotationStem(String uri){
        if (uri == null || uri.equals("")) {
            return ok(ApiUtil.createResponse("No annotation stem URI has been provided.", false));
        }
        AnnotationStem annotationStem = AnnotationStem.find(uri);
        if (annotationStem == null) {
            return ok(ApiUtil.createResponse("There is no annotation stem with URI <" + uri + "> to be deleted.", false));
        } else {
            return deleteAnnotationStemResult(annotationStem);
        }
    }

    public static Result getAnnotationStems(List<AnnotationStem> results){
        if (results == null) {
            return ok(ApiUtil.createResponse("No annotation stem has been found", false));
        } else {
            ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,VSTOI.ANNOTATION_STEM);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            //System.out.println("DetecttorAPI: [" + ApiUtil.createResponse(jsonObject, true) + "]");
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }

    public Result getUsage(String annotationStemUri){
        List<Annotation> results = AnnotationStem.usage(annotationStemUri);
        return AnnotationAPI.getAnnotations(results);
    }


}
