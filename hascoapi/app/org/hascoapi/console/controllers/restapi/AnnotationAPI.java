package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import org.hascoapi.Constants;
import org.hascoapi.entity.pojo.AnnotationStem;
import org.hascoapi.entity.pojo.Annotation;
import org.hascoapi.entity.pojo.Instrument;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.HAScOMapper;
import org.hascoapi.vocabularies.VSTOI;
import play.mvc.Controller;
import play.mvc.Result;
import static org.hascoapi.Constants.*;
import java.util.List;
import java.util.ArrayList;


public class AnnotationAPI extends Controller {

    private Result createAnnotationResult(Annotation annotation) {
        annotation.save();
        return ok(ApiUtil.createResponse("Annotation <" + annotation.getUri() + "> has been CREATED.", true));
    }

    public Result createAnnotationsForTesting() {
        Annotation testAnnotation1 = Annotation.find(TEST_ANNOTATION1_URI);
        Annotation testAnnotation2 = Annotation.find(TEST_ANNOTATION2_URI);
        Annotation testAnnotationInstruction = Annotation.find(TEST_ANNOTATION_INSTRUCTION_URI);
        Annotation testAnnotationPage = Annotation.find(TEST_ANNOTATION_PAGE_URI);
        Annotation testAnnotationDateField = Annotation.find(TEST_ANNOTATION_DATEFIELD_URI);
        Annotation testAnnotationCopyright = Annotation.find(TEST_ANNOTATION_COPYRIGHT_URI);
        if (testAnnotation1 != null) {
            return ok(ApiUtil.createResponse("Test annotation 1 already exists.", false));
        } else if (testAnnotation2 != null) {
            return ok(ApiUtil.createResponse("Test annotation 2 already exists.", false));
        } else if (testAnnotationInstruction != null) {
            return ok(ApiUtil.createResponse("Test annotation instruction already exists.", false));
        } else if (testAnnotationPage != null) {
            return ok(ApiUtil.createResponse("Test annotation page already exists.", false));
        } else if (testAnnotationDateField != null) {
            return ok(ApiUtil.createResponse("Test annotation date field already exists.", false));
        } else if (testAnnotationCopyright != null) {
            return ok(ApiUtil.createResponse("Test annotation copyright already exists.", false));
        } else {
            Instrument testInstrument = Instrument.find(TEST_INSTRUMENT_URI);
            AnnotationStem testAnnotationStem1 = AnnotationStem.find(TEST_ANNOTATION_STEM1_URI);
            AnnotationStem testAnnotationStem2 = AnnotationStem.find(TEST_ANNOTATION_STEM2_URI);
            AnnotationStem testAnnotationStemInstruction = AnnotationStem.find(TEST_ANNOTATION_STEM_INSTRUCTION_URI);
            AnnotationStem testAnnotationStemPage = AnnotationStem.find(TEST_ANNOTATION_STEM_PAGE_URI);
            AnnotationStem testAnnotationStemDateField = AnnotationStem.find(TEST_ANNOTATION_STEM_DATEFIELD_URI);
            AnnotationStem testAnnotationStemCopyright = AnnotationStem.find(TEST_ANNOTATION_STEM_COPYRIGHT_URI);
            if (testInstrument == null) {
              return ok(ApiUtil.createResponse("Required TestInstrument does not exist.", false));
            } else if (testAnnotationStem1 == null) {
              return ok(ApiUtil.createResponse("Required TestAnnotationStem1 does not exist.", false));
            } else if (testAnnotationStem2 == null) {
              return ok(ApiUtil.createResponse("Required TestAnnotationStem2 does not exist.", false));
            } else if (testAnnotationStemInstruction == null) {
              return ok(ApiUtil.createResponse("Required TestAnnotationStemInstruction does not exist.", false));
            } else if (testAnnotationStemPage == null) {
              return ok(ApiUtil.createResponse("Required TestAnnotationStemPage does not exist.", false));
            } else if (testAnnotationStemDateField == null) {
              return ok(ApiUtil.createResponse("Required TestAnnotationStemDateField does not exist.", false));
            } else if (testAnnotationStemCopyright == null) {
              return ok(ApiUtil.createResponse("Required TestAnnotationStemCopyright does not exist.", false));
            } else {
                testAnnotation1 = new Annotation();
                testAnnotation1.setUri(TEST_ANNOTATION1_URI);
                testAnnotation1.setLabel("Test Annotation 1");
                testAnnotation1.setTypeUri(VSTOI.ANNOTATION);
                testAnnotation1.setHascoTypeUri(VSTOI.ANNOTATION);
                testAnnotation1.setComment("This is a dummy Annotation 1 created to test the SIR API.");
                testAnnotation1.setBelongsTo(TEST_INSTRUMENT_URI);
                testAnnotation1.setHasAnnotationStem(TEST_ANNOTATION_STEM1_URI);
                testAnnotation1.setHasPosition(VSTOI.NOT_VISIBLE);
                testAnnotation1.setHasContentWithStyle("");
                testAnnotation1.setHasSIRManagerEmail("me@example.com");
                testAnnotation1.setNamedGraph(Constants.TEST_KB);
                testAnnotation1.save();

                testAnnotation2 = new Annotation();
                testAnnotation2.setUri(TEST_ANNOTATION2_URI);
                testAnnotation2.setLabel("Test Annotation 2");
                testAnnotation2.setTypeUri(VSTOI.ANNOTATION);
                testAnnotation2.setHascoTypeUri(VSTOI.ANNOTATION);
                testAnnotation2.setComment("This is a dummy Annotation 2 created to test the SIR API.");
                testAnnotation2.setBelongsTo(TEST_INSTRUMENT_URI);
                testAnnotation2.setHasAnnotationStem(TEST_ANNOTATION_STEM2_URI);
                testAnnotation2.setHasPosition(VSTOI.NOT_VISIBLE);
                testAnnotation2.setHasContentWithStyle("");
                testAnnotation2.setHasSIRManagerEmail("me@example.com");
                testAnnotation2.setNamedGraph(Constants.TEST_KB);
                testAnnotation2.save();

                testAnnotationInstruction = new Annotation();
                testAnnotationInstruction.setUri(TEST_ANNOTATION_INSTRUCTION_URI);
                testAnnotationInstruction.setLabel("Test Annotation Instruction");
                testAnnotationInstruction.setTypeUri(VSTOI.ANNOTATION);
                testAnnotationInstruction.setHascoTypeUri(VSTOI.ANNOTATION);
                testAnnotationInstruction.setComment("This is a dummy Annotation Instruction created to test the SIR API.");
                testAnnotationInstruction.setBelongsTo(TEST_INSTRUMENT_URI);
                testAnnotationInstruction.setHasAnnotationStem(TEST_ANNOTATION_STEM_INSTRUCTION_URI);
                testAnnotationInstruction.setHasPosition(VSTOI.PAGE_LINE_BELOW_TOP);
                testAnnotationInstruction.setHasContentWithStyle("");
                testAnnotationInstruction.setHasSIRManagerEmail("me@example.com");
                testAnnotationInstruction.setNamedGraph(Constants.TEST_KB);
                testAnnotationInstruction.save();

                testAnnotationPage = new Annotation();
                testAnnotationPage.setUri(TEST_ANNOTATION_PAGE_URI);
                testAnnotationPage.setLabel("Test Annotation Page");
                testAnnotationPage.setTypeUri(VSTOI.ANNOTATION);
                testAnnotationPage.setHascoTypeUri(VSTOI.ANNOTATION);
                testAnnotationPage.setComment("This is a dummy Annotation Page created to test the SIR API.");
                testAnnotationPage.setBelongsTo(TEST_INSTRUMENT_URI);
                testAnnotationPage.setHasAnnotationStem(TEST_ANNOTATION_STEM_PAGE_URI);
                testAnnotationPage.setHasPosition(VSTOI.PAGE_BOTTOM_LEFT);
                testAnnotationPage.setHasContentWithStyle("");
                testAnnotationPage.setHasSIRManagerEmail("me@example.com");
                testAnnotationPage.setNamedGraph(Constants.TEST_KB);
                testAnnotationPage.save();

                testAnnotationDateField = new Annotation();
                testAnnotationDateField.setUri(TEST_ANNOTATION_DATEFIELD_URI);
                testAnnotationDateField.setLabel("Test Annotation DateField");
                testAnnotationDateField.setTypeUri(VSTOI.ANNOTATION);
                testAnnotationDateField.setHascoTypeUri(VSTOI.ANNOTATION);
                testAnnotationDateField.setComment("This is a dummy Annotation DateField created to test the SIR API.");
                testAnnotationDateField.setBelongsTo(TEST_INSTRUMENT_URI);
                testAnnotationDateField.setHasAnnotationStem(TEST_ANNOTATION_STEM_DATEFIELD_URI);
                testAnnotationDateField.setHasPosition(VSTOI.PAGE_TOP_RIGHT);
                testAnnotationDateField.setHasContentWithStyle("");
                testAnnotationDateField.setHasSIRManagerEmail("me@example.com");
                testAnnotationDateField.setNamedGraph(Constants.TEST_KB);
                testAnnotationDateField.save();

                testAnnotationCopyright = new Annotation();
                testAnnotationCopyright.setUri(TEST_ANNOTATION_COPYRIGHT_URI);
                testAnnotationCopyright.setLabel("Test Annotation Copyright");
                testAnnotationCopyright.setTypeUri(VSTOI.ANNOTATION);
                testAnnotationCopyright.setHascoTypeUri(VSTOI.ANNOTATION);
                testAnnotationCopyright.setComment("This is a dummy Annotation Copyright created to test the SIR API.");
                testAnnotationCopyright.setBelongsTo(TEST_INSTRUMENT_URI);
                testAnnotationCopyright.setHasAnnotationStem(TEST_ANNOTATION_STEM_COPYRIGHT_URI);
                testAnnotationCopyright.setHasPosition(VSTOI.PAGE_BOTTOM_RIGHT);
                testAnnotationCopyright.setHasContentWithStyle("");
                testAnnotationCopyright.setHasSIRManagerEmail("me@example.com");
                testAnnotationCopyright.setNamedGraph(Constants.TEST_KB);
                testAnnotationCopyright.save();

            }
            return ok(ApiUtil.createResponse("Test Annotations 1, 2, Instruction, Page, DateField and Copyright have been CREATED.", true));
        }
    }

    public Result createAnnotation(String json) {
        if (json == null || json.equals("")) {
            return ok(ApiUtil.createResponse("No json content has been provided.", false));
        }
        //System.out.println("(CreateAnnotation) Value of json: [" + json + "]");
        ObjectMapper objectMapper = new ObjectMapper();
        Annotation newAnnotation;
        try {
            //convert json string to Annotation instance
            newAnnotation  = objectMapper.readValue(json, Annotation.class);
        } catch (Exception e) {
            //System.out.println("(createAnnotation) Failed to parse json.");
            return ok(ApiUtil.createResponse("Failed to parse json.", false));
        }
        return createAnnotationResult(newAnnotation);
    }

    private Result deleteAnnotationResult(Annotation annotation) {
        String uri = annotation.getUri();
        annotation.delete();
        return ok(ApiUtil.createResponse("Annotation <" + uri + "> has been DELETED.", true));
    }

    public Result deleteAnnotationsForTesting(){
        Annotation test1 = Annotation.find(TEST_ANNOTATION1_URI);
        Annotation test2 = Annotation.find(TEST_ANNOTATION2_URI);
        Annotation testInstruction = Annotation.find(TEST_ANNOTATION_INSTRUCTION_URI);
        Annotation testPage = Annotation.find(TEST_ANNOTATION_PAGE_URI);
        Annotation testDateField = Annotation.find(TEST_ANNOTATION_DATEFIELD_URI);
        Annotation testCopyright = Annotation.find(TEST_ANNOTATION_COPYRIGHT_URI);
        String msg = "";
        if (test1 == null) {
            msg += "No Test Annotation 1. ";
        } else {
            test1.setNamedGraph(Constants.TEST_KB);
            test1.delete();
        } 
        if (test2 == null) {
            msg += "No Test Annotation 2. ";
        } else {
            test2.setNamedGraph(Constants.TEST_KB);
            test2.delete();
        } 
        if (testInstruction == null) {
            msg += "No Test Annotation Instruction. ";
        } else {
            testInstruction.setNamedGraph(Constants.TEST_KB);
            testInstruction.delete();
        }
        if (testPage == null) {
            msg += "No Test Annotation Page. ";
        } else {
            testPage.setNamedGraph(Constants.TEST_KB);
            testPage.delete();
        }
        if (testDateField == null) {
            msg += "No Test Annotation DateField. ";
        } else {
            testDateField.setNamedGraph(Constants.TEST_KB);
            testDateField.delete();
        }
        if (testCopyright == null) {
            msg += "No Test Annotation Copyright. ";
        } else {
            testCopyright.setNamedGraph(Constants.TEST_KB);
            testCopyright.delete();
        }
        if (!msg.isEmpty()) {
            return ok(ApiUtil.createResponse(msg, false));
        } else {
            return ok(ApiUtil.createResponse("Existing Test Annotations have been DELETED.", true));
        }
    }

    public Result getAnnotationsByContainer(String containerUri){
        List<Annotation> results = Annotation.findByContainer(containerUri);
        return getAnnotations(results);
    }

    public Result getAnnotationByContainerAndPosition(String containerUri, String positionUri){
        //System.out.println("AnnotationAPI.getAnnotationByContainerAndPosition - containerUri [" + containerUri + "]   positionUri [" + positionUri + "]");
        List<Annotation> results = new ArrayList<Annotation>();
        Annotation annotation = Annotation.findByContainerAndPosition(containerUri,positionUri);
        if (annotation != null) {
            results.add(annotation);
        }
        return getAnnotations(results);
    }

    public static Result getAnnotations(List<Annotation> results){
        if (results == null) {
            return ok(ApiUtil.createResponse("No annotation has been found", false));
        } else {
            ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,VSTOI.ANNOTATION);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            //System.out.println("DetectorAPI: [" + ApiUtil.createResponse(jsonObject, true) + "]");
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }

}
