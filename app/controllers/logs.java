package controllers;

import models.*;
import models.Users;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rebeca on 12/19/2015.
 * Controls the GET request related to the Logs
 */
public class logs extends Controller{
    /*Controls the GET request for the medications log page. It takes a user id as argument
    * It makes sure that a user is logged in and that the requested logs belong to the logged user.
    * It then retrieves the user's stored logs and renders them for the views page to access.*/
    @Security.Authenticated(UserAuth.class)
    public Result index(Long id){
        Long u_id = Long.parseLong(session().get("user_id"));
        if(id != u_id){
            flash("error","You do not have access to this page.");
            return redirect(routes.Users.index(u_id));
        }
        models.Users current = Users.find.byId(u_id);
        List<Log> mylogs = current.myLogs;
        if (mylogs == null){
            mylogs = new ArrayList<>();
            return ok(views.html.Log.index.render(mylogs));
        }
        return ok(views.html.Log.index.render(mylogs));
    }
}
