package org.hascoapi.console.controllers;

import org.hascoapi.entity.pojo.OntologyTripleStore;
import org.hascoapi.utils.ConfigProp;
import org.hascoapi.utils.NameSpaces;
import play.mvc.Controller;
import play.mvc.Result;

//import org.hascoapi.console.controllers.ontologies.LoadOnt;
import org.hascoapi.console.views.html.version;

public class Version extends Controller {

    public Result index() {
        String code_version = "0.0.1";
        String base_ontology = "";

        String loaded_base_ontology = NameSpaces.getInstance().getNameByAbbreviation(base_ontology);
        String loaded_base_ontology_version = OntologyTripleStore.getVersionFromAbbreviation(base_ontology);
        //String propfile = LoadOnt.getNameLastLoadedNamespace();
        //return ok(version.render(code_version, base_ontology, loaded_base_ontology, loaded_base_ontology_version, propfile));
        return ok(version.render(code_version, base_ontology, loaded_base_ontology, loaded_base_ontology_version, null));
    }

    public Result postIndex() {
        return index();
    }
}
