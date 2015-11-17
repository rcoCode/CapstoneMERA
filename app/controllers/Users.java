package controllers;

import play.mvc.Controller;
import play.mvc.*;

/**
 * Created by rebec on 11/17/2015.
 */
public class Users extends Controller{
    public Result index() {
        return ok(views.html.Users.index.render("Users"));
    }
}
