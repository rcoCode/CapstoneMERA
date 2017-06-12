package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

/**
 * Created by agarnelo on 6/3/17.
 */


public class Information extends Controller {
    //Renders the index page, used by the GET route for the index page
    public Result demo() {
        return ok(views.html.Information.demo.render(""));
    }

    public Result projectInformation() {return ok(views.html.Information.projInfo.render(""));}

}