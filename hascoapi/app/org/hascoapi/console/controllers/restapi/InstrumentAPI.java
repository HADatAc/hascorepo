package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

import org.hascoapi.Constants;
import org.hascoapi.entity.fhir.Questionnaire;
import org.hascoapi.entity.pojo.Instrument;
import org.hascoapi.transform.Renderings;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.HAScOMapper;
import org.hascoapi.vocabularies.VSTOI;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.hascoapi.Constants.TEST_INSTRUMENT_URI;
import static org.hascoapi.Constants.TEST_INSTRUMENT_TOT_CONTAINER_SLOTS;

public class InstrumentAPI extends Controller {

    private Result createInstrumentResult(Instrument inst) {
        inst.save();
        return ok(ApiUtil.createResponse("Instrument <" + inst.getUri() + "> has been CREATED.", true));
    }

    public Result createInstrumentForTesting() {
        Instrument testInstrument = Instrument.find(TEST_INSTRUMENT_URI);
        if (testInstrument != null) {
            return ok(ApiUtil.createResponse("Test instrument <" + TEST_INSTRUMENT_URI + "> already exists.", false));
        } else {
            testInstrument = new Instrument(VSTOI.INSTRUMENT);
            testInstrument.setUri(TEST_INSTRUMENT_URI);
            testInstrument.setLabel("Test Instrument");
            testInstrument.setTypeUri(VSTOI.QUESTIONNAIRE);
            testInstrument.setHascoTypeUri(VSTOI.INSTRUMENT);
            testInstrument.setHasInformant(VSTOI.DEFAULT_INFORMANT);
            testInstrument.setHasShortName("TEST");
            testInstrument.setHasLanguage(VSTOI.DEFAULT_LANGUAGE); // ISO 639-1
            testInstrument.setComment("This is a dummy instrument created to test the SIR API.");
            testInstrument.setHasVersion("1");
            testInstrument.setHasSIRManagerEmail("me@example.com");
            testInstrument.setNamedGraph(Constants.TEST_KB);

            return createInstrumentResult(testInstrument);
        }
    }

    public Result createInstrument(String json) {
        if (json == null || json.equals("")) {
            return ok(ApiUtil.createResponse("No json content has been provided.", false));
        }
        //System.out.println("(InstrumentAPI) Value of json in createInstrument: [" + json + "]");
        ObjectMapper objectMapper = new ObjectMapper();
        Instrument newInst;
        try {
            //convert json string to Instrument instance
            newInst  = objectMapper.readValue(json, Instrument.class);
        } catch (Exception e) {
            //System.out.println("(InstrumentAPI) Failed to parse json for [" + json + "]");
            return ok(ApiUtil.createResponse("Failed to parse json.", false));
        }
        return createInstrumentResult(newInst);
    }

    private Result deleteInstrumentResult(Instrument inst) {
        String uri = inst.getUri();
        inst.delete();
        return ok(ApiUtil.createResponse("Instrument <" + uri + "> has been DELETED.", true));
    }

    public Result deleteInstrumentForTesting(){
        Instrument test;
        test = Instrument.find(TEST_INSTRUMENT_URI);
        if (test == null) {
            return ok(ApiUtil.createResponse("There is no Test instrument to be deleted.", false));
        } else {
            test.setNamedGraph(Constants.TEST_KB);
            return deleteInstrumentResult(test);
        }
    }

    public Result deleteInstrument(String uri){
        if (uri == null || uri.equals("")) {
            return ok(ApiUtil.createResponse("No instrument URI has been provided.", false));
        }
        Instrument inst = Instrument.find(uri);
        if (inst == null) {
            return ok(ApiUtil.createResponse("There is no instrument with URI <" + uri + "> to be deleted.", false));
        } else {
            return deleteInstrumentResult(inst);
        }
    }

    public static Result getInstruments(List<Instrument> results){
        if (results == null) {
            return ok(ApiUtil.createResponse("No instrument has been found", false));
        } else {
            ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,VSTOI.INSTRUMENT);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }

    public Result toTextPlain(String uri) {
        if (uri  == null || uri.equals("")) {
            return ok(ApiUtil.createResponse("No URI has been provided", false));
        }
        String instrumentText = Renderings.toString(uri, 80);
        if (instrumentText == null || instrumentText.equals("")) {
            return ok(ApiUtil.createResponse("No instrument has been found", false));
        } else {
            return ok(instrumentText).as("text/plain");
        }
    }

    public Result toTextHTML(String uri) {
        if (uri  == null || uri.equals("")) {
            return ok(ApiUtil.createResponse("No URI has been provided", false));
        }
        String instrumentText = Renderings.toHTML(uri, 80);
        if (instrumentText == null || instrumentText.equals("")) {
            return ok(ApiUtil.createResponse("No instrument has been found", false));
        } else {
            return ok(instrumentText).as("text/html");
        }
    }

    public Result toTextPDF(String uri) {
        if (uri  == null || uri.equals("")) {
            return ok(ApiUtil.createResponse("No URI has been provided", false));
        }
        ByteArrayOutputStream instrumentText = Renderings.toPDF(uri, 80);
        if (instrumentText == null || instrumentText.equals("")) {
            return ok(ApiUtil.createResponse("No instrument has been found", false));
        } else {
            return ok(instrumentText.toByteArray()).as("application/pdf");
        }
    }

    public Result toFHIR(String uri) {
        if (uri  == null || uri.equals("")) {
            return ok(ApiUtil.createResponse("No URI has been provided", false));
        }
        Instrument instr = Instrument.find(uri);
        if (instr == null) {
            return ok(ApiUtil.createResponse("No instrument instance found for uri [" + uri + "]", false));
        }

        Questionnaire quest = new Questionnaire(instr);

        FhirContext ctx = FhirContext.forR4();
        IParser parser = ctx.newJsonParser();
        String serialized = parser.encodeResourceToString(quest.getFHIRObject());

        return ok(serialized).as("application/json");
    }

    public Result toRDF(String uri) {
        if (uri  == null || uri.equals("")) {
            return ok(ApiUtil.createResponse("No URI has been provided", false));
        }
        Instrument instr = Instrument.find(uri);
        if (instr == null) {
            return ok(ApiUtil.createResponse("No instrument instance found for uri [" + uri + "]", false));
        }

        String serialized = instr.printRDF();

        return ok(serialized).as("application/xml");
    }
}
