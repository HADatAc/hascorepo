package org.hascoapi.console.controllers.documentation;

import org.hascoapi.console.views.html.documentation.*;
import org.hascoapi.console.views.html.documentation.examples.*;
import play.mvc.Controller;
import play.mvc.Result;

public class RetrievalMethodList extends Controller {

    public Result index() {
        return ok(retrievalmethodlist.render());
    }

    public Result queryURIExample() {
        return ok(queryURIExample.render());
    }

    public Result queryElementsExample() {
        return ok(queryElementsExample.render());
    }

    public Result queryElementsExampleWithKeyword() {
        return ok(queryElementsExampleWithKeyword.render());
    }

    public Result queryElementsTotalExampleWithKeyword() {
        return ok(queryElementsTotalExampleWithKeyword.render());
    }

    public Result instrumentRenderingExample() {
        return ok(instrumentRenderingExample.render());
    }

}