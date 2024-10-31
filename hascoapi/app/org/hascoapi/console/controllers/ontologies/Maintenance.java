package org.hascoapi.console.controllers.ontologies;

import org.hascoapi.console.views.html.ontologies.maintenance;
import org.hascoapi.utils.NameSpaces;
import play.mvc.Controller;
import play.mvc.Result;

public class Maintenance extends Controller {

    public Result index() {
        return ok(maintenance.render(NameSpaces.getInstance().getOrderedNamespacesAsList()));
    }

    public Result postIndex() {
        return index();
    }

}