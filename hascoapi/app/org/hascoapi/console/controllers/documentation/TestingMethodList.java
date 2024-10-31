package org.hascoapi.console.controllers.documentation;

import play.mvc.Result;
import play.mvc.Controller;
import org.hascoapi.console.views.html.documentation.*;

public class TestingMethodList extends Controller {

    public Result index() {
        return ok(testingmethodlist.render());
    }

}