package org.hascoapi.console.controllers;

import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import org.hascoapi.console.views.html.portal;

public class Portal extends Controller {

    public Result index(Http.Request request) {
        return ok(portal.render("User"));
    }

    public Result postIndex(Http.Request request){
        return ok(portal.render("User"));
    }

}