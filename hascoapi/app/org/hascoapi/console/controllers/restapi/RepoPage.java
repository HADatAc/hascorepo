package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.hascoapi.RepositoryInstance;
import org.hascoapi.console.controllers.ontologies.LoadOnt;
import org.hascoapi.entity.pojo.NameSpace;
import org.hascoapi.entity.pojo.Repository;
import org.hascoapi.entity.pojo.Table;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.NameSpaces;
import play.mvc.Controller;
import play.mvc.Result;
import com.typesafe.config.ConfigFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RepoPage extends Controller {

    public Result getRepository() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // get the list of variables in that study
            // serialize the Study object first as ObjectNode
            //   as JsonNode is immutable and meant to be read-only
            ObjectNode obj = mapper.convertValue(RepositoryInstance.getInstance(), ObjectNode.class);
            JsonNode jsonObject = mapper.convertValue(obj, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        } catch (Exception e) {
            e.printStackTrace();
            return badRequest(ApiUtil.createResponse("Error parsing class " + Repository.className, false));
        }
    }

    public Result updateLabel(String label){
        if (label == null || label.equals("")) {
            return ok(ApiUtil.createResponse("No (name) has been provided.", false));
        }
        RepositoryInstance.getInstance().setLabel(label);
        RepositoryInstance.getInstance().save();
        return ok(ApiUtil.createResponse("Repository's (name) has been UPDATED.", true));
    }

    public Result updateTitle(String title){
        if (title == null || title.equals("")) {
            return ok(ApiUtil.createResponse("No (title) has been provided.", false));
        }
        RepositoryInstance.getInstance().setTitle(title);
        RepositoryInstance.getInstance().save();
        return ok(ApiUtil.createResponse("Repository's (title) has been UPDATED.", true));
    }

    public Result updateDescription(String description){
        if (description == null || description.equals("")) {
            return ok(ApiUtil.createResponse("No (description) has been provided.", false));
        }
        RepositoryInstance.getInstance().setComment(description);
        RepositoryInstance.getInstance().save();
        return ok(ApiUtil.createResponse("Repository's (description) has been UPDATED.", true));
    }

    public Result updateDefaultNamespace(String abbreviation, String url){
        if (abbreviation == null || abbreviation.equals("")) {
            return ok(ApiUtil.createResponse("No (abbreviation) has been provided.", false));
        }
        if (url == null || url.equals("")) {
            return ok(ApiUtil.createResponse("No (url) has been provided.", false));
        }
        RepositoryInstance.getInstance().setHasDefaultNamespaceAbbreviation(abbreviation);
        RepositoryInstance.getInstance().setHasDefaultNamespaceURL(url);
        RepositoryInstance.getInstance().save();
        NameSpaces.getInstance().updateLocalNamespace();
        return ok(ApiUtil.createResponse("Repository's local namespace has been UPDATED.", true));
    }

    public Result updateNamespace(String abbreviation, String url){
        if (abbreviation == null || abbreviation.equals("")) {
            return ok(ApiUtil.createResponse("No (abbreviation) has been provided.", false));
        }
        if (url == null || url.equals("")) {
            return ok(ApiUtil.createResponse("No (url) has been provided.", false));
        }
        RepositoryInstance.getInstance().setHasNamespaceAbbreviation(abbreviation);
        RepositoryInstance.getInstance().setHasNamespaceURL(url);
        RepositoryInstance.getInstance().save();
        NameSpaces.getInstance().resetNameSpaces();;
        return ok(ApiUtil.createResponse("Repository's local namespace has been UPDATED.", true));
    }

    public Result createNamespace(String json){
        if (json == null || json.equals("")) {
            return ok(ApiUtil.createResponse("No JSON has been provided.", false));
        }
        if (RepositoryInstance.getInstance().newNamespace(json)) {
            RepositoryInstance.getInstance().save();
            NameSpaces.getInstance().resetNameSpaces();
            return ok(ApiUtil.createResponse("New namespace has been added to the repository.", true));
        } else {
            return ok(ApiUtil.createResponse("Failed to add new namespace into the repository.", false));
        }
    }

    public Result resetNamespaces(){
        if (RepositoryInstance.getInstance().resetNamespaces()) {
            NameSpaces.getInstance().resetNameSpaces();;
            return ok(ApiUtil.createResponse("Namespaces have been reset.", true));
        } else {
            return ok(ApiUtil.createResponse("Failed to reset namespaces.", false));
        }
    }

    public Result deleteSelectedNamespace(String abbreviation){
        if (abbreviation == null || abbreviation.equals("")) {
            return ok(ApiUtil.createResponse("No Namespace's ABBREVIATION has been provided.", false));
        }
        String response = NameSpace.deleteNamespace(abbreviation);
        NameSpaces.getInstance().resetNameSpaces();;
        if (response.isEmpty()) {
            return ok(ApiUtil.createResponse("Namespace [" + abbreviation + "] has been DELETED.", true));
        } else {
            return ok(ApiUtil.createResponse("Namespace [" + abbreviation + "] has NOT been DELETED. Reason: " + response, false));
        }
    }

    public Result deleteNamespace(){
        String abbrev = RepositoryInstance.getInstance().getHasDefaultNamespaceAbbreviation();
        String url = RepositoryInstance.getInstance().getHasDefaultNamespaceURL();
        if (abbrev == null || abbrev.equals("") || url == null || url.equals("")) {
            return ok(ApiUtil.createResponse("There is no default namespace to be deleted.", false));
        }
        RepositoryInstance.getInstance().setHasDefaultNamespaceAbbreviation("");
        RepositoryInstance.getInstance().setHasDefaultNamespaceAbbreviation("");
        RepositoryInstance.getInstance().save();
        NameSpaces.getInstance().deleteLocalNamespace();
        return ok(ApiUtil.createResponse("Repository's local namespace has been DELETED.", true));
    }


    private Long manageTriples(String oper, String kb) {
        LoadOnt.playLoadOntologiesAsync(oper, kb);
        return 0L;
    }
    
    public Result loadOntologies(){
        String kb = ConfigFactory.load().getString("hascoapi.repository.triplestore");
        CompletableFuture<Long> completableFuture = CompletableFuture.supplyAsync(() -> manageTriples("load", kb));
        //while (!completableFuture.isDone()) {
        //    System.out.println("CompletableFuture is not finished yet...");
        //}
        //long result = completableFuture.get();
        return ok(ApiUtil.createResponse("Repository's ontologies has been requested to be LOADED.", true));
    }

    public Result deleteOntologies(){
        String kb = ConfigFactory.load().getString("hascoapi.repository.triplestore");
        CompletableFuture<Long> completableFuture = CompletableFuture.supplyAsync(() -> manageTriples("delete", kb));
        //while (!completableFuture.isDone()) {
        //    System.out.println("CompletableFuture is not finished yet...");
        //}
        //long result = completableFuture.get();
        return ok(ApiUtil.createResponse("Repository's ontologies have been requested to be DELETED.", true));
    }

    public Result getLanguages() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // get the list of variables in that study
            // serialize the Study object first as ObjectNode
            //   as JsonNode is immutable and meant to be read-only
            //List<Table> table = Table.findLanguage();
            //for (Table entry: table) {
            //    System.out.println(entry.getCode());
            // }
            //System.out.println("inside getLanguages");
            ArrayNode array = mapper.convertValue(Table.findLanguage(), ArrayNode.class);
            JsonNode jsonObject = mapper.convertValue(array, JsonNode.class);
            //System.out.println("inside getLanguages [" + jsonObject + "]");
            return ok(ApiUtil.createResponse(jsonObject, true));
        } catch (Exception e) {
            e.printStackTrace();
            return badRequest(ApiUtil.createResponse("Error retrieving languages", false));
        }
    }

    public Result getGenerationActivities() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // get the list of variables in that study
            // serialize the Study object first as ObjectNode
            //   as JsonNode is immutable and meant to be read-only
            //List<Table> table = Table.find();
            //for (Table entry: table) {
            //    System.out.println(entry.getCode());
            //}
            ArrayNode array = mapper.convertValue(Table.findGenerationActivity(), ArrayNode.class);
            JsonNode jsonObject = mapper.convertValue(array, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        } catch (Exception e) {
            e.printStackTrace();
            return badRequest(ApiUtil.createResponse("Error retrieving generation activities", false));
        }
    }

    public Result getInformants() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // get the list of variables in that study
            // serialize the Study object first as ObjectNode
            //   as JsonNode is immutable and meant to be read-only
            //List<Table> table = Table.find();
            //for (Table entry: table) {
            //    System.out.println(entry.getCode());
            //}
            ArrayNode array = mapper.convertValue(Table.findInformant(), ArrayNode.class);
            JsonNode jsonObject = mapper.convertValue(array, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        } catch (Exception e) {
            e.printStackTrace();
            return badRequest(ApiUtil.createResponse("Error retrieving informants", false));
        }
    }

    public Result getNamespaces() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ArrayNode array = mapper.convertValue(NameSpaces.getInstance().getOrderedNamespacesAsList(), ArrayNode.class);
            JsonNode jsonObject = mapper.convertValue(array, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        } catch (Exception e) {
            e.printStackTrace();
            return badRequest(ApiUtil.createResponse("Error retrieving namespaces", false));
        }
    }

    public Result getInstrumentPositions() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ArrayNode array = mapper.convertValue(Table.findInstrumentPosition(), ArrayNode.class);
            JsonNode jsonObject = mapper.convertValue(array, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        } catch (Exception e) {
            e.printStackTrace();
            return badRequest(ApiUtil.createResponse("Error retrieving Instrument Positions", false));
        }
    }

    public Result getSubcontainerPositions() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ArrayNode array = mapper.convertValue(Table.findSubcontainerPosition(), ArrayNode.class);
            JsonNode jsonObject = mapper.convertValue(array, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        } catch (Exception e) {
            e.printStackTrace();
            return badRequest(ApiUtil.createResponse("Error retrieving Subcontainer Positions", false));
        }
    }

}
