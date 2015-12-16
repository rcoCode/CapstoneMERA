package controllers;

import play.data.Form;
import play.mvc.*;
import views.html.*;
import java.util.List;

/**
 * Created by rebeca on 11/17/2015.
 */
public class Meds extends Controller{
    public Result index(){
        return ok(views.html.Meds.index.render(""));
    }
}
