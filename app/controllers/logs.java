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
 */
public class logs extends Controller{
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
