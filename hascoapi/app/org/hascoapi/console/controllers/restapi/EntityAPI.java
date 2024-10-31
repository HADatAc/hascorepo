package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

import org.hascoapi.Constants;
import org.hascoapi.entity.fhir.Questionnaire;
import org.hascoapi.entity.pojo.Entity;
import org.hascoapi.transform.Renderings;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.HAScOMapper;
import org.hascoapi.vocabularies.SIO;
import org.hascoapi.vocabularies.VSTOI;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.hascoapi.Constants.TEST_ENTITY_URI;

public class EntityAPI extends Controller {

    private Result createEntityResult(Entity entity) {
        entity.save();
        return ok(ApiUtil.createResponse("Entity <" + entity.getUri() + "> has been CREATED.", true));
    }

    public Result createEntity(String json) {
        if (json == null || json.equals("")) {
            return ok(ApiUtil.createResponse("No json content has been provided.", false));
        }
        //System.out.println("(EntityAPI) Value of json in createEntity: [" + json + "]");
        ObjectMapper objectMapper = new ObjectMapper();
        Entity newEntity;
        try {
            //convert json string to Entity entityance
            newEntity  = objectMapper.readValue(json, Entity.class);
        } catch (Exception e) {
            //System.out.println("(EntityAPI) Failed to parse json for [" + json + "]");
            return ok(ApiUtil.createResponse("Failed to parse json.", false));
        }
        return createEntityResult(newEntity);
    }

    public Result createEntityForTesting() {
        Entity testEntity = Entity.find(TEST_ENTITY_URI);
        if (testEntity != null) {
            return ok(ApiUtil.createResponse("Test entity <" + TEST_ENTITY_URI + "> already exists.", false));
        } else {
            testEntity = new Entity();
            testEntity.setUri(TEST_ENTITY_URI);
            testEntity.setSuperUri(SIO.ENTITY);
            testEntity.setLabel("Test Entity");
            testEntity.setTypeUri(TEST_ENTITY_URI);
            testEntity.setHascoTypeUri(SIO.ENTITY);
            testEntity.setComment("This is a dummy entity created to test the SIR API.");
            testEntity.setNamedGraph(Constants.TEST_KB);

            //testEntity.setHasSIRManagerEmail("me@example.com");

            return createEntityResult(testEntity);
        }
    }

    private Result deleteEntityResult(Entity entity) {
        String uri = entity.getUri();
        entity.delete();
        return ok(ApiUtil.createResponse("Entity <" + uri + "> has been DELETED.", true));
    }

    public Result deleteEntity(String uri){
        if (uri == null || uri.equals("")) {
            return ok(ApiUtil.createResponse("No entity URI has been provided.", false));
        }
        Entity entity = Entity.find(uri);
        if (entity == null) {
            return ok(ApiUtil.createResponse("There is no entity with URI <" + uri + "> to be deleted.", false));
        } else {
            return deleteEntityResult(entity);
        }
    }

    public Result deleteEntityForTesting(){
        Entity test;
        test = Entity.find(TEST_ENTITY_URI);
        if (test == null) {
            return ok(ApiUtil.createResponse("There is no Test entity to be deleted.", false));
        } else {
            test.setNamedGraph(Constants.TEST_KB);
            return deleteEntityResult(test);
        }
    }

    public static Result getEntities(List<Entity> results){
        if (results == null) {
            return ok(ApiUtil.createResponse("No entity has been found", false));
        } else {
            ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,SIO.ENTITY);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }

}
