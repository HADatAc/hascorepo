package org.hascoapi.console.controllers.documentation;

import org.hascoapi.console.views.html.documentation.*;
import play.mvc.Controller;
import play.mvc.Result;

public class RepositoryMethodList extends Controller {

    public Result index() {
        return ok(repositorymethodlist.render());
    }

}