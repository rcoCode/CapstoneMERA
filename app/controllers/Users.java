package controllers;

import models.Containers;
import play.mvc.Controller;
import play.mvc.*;
import models.Meds;



import java.util.List;

/**
 * Created by rebeca on 11/17/2015.
 */
public class Users extends Controller{
    @Security.Authenticated(UserAuth.class)
    public Result index(Long id) {
        Long u_id = Long.parseLong(session().get("user_id"));
        if (id != u_id){
            flash("error","You don't have access to this page!");
            return redirect(routes.Application.index());
        }
        models.Users logged = models.Users.find.byId(u_id);
        List<Containers> mymeds = logged.myMeds;
        return ok(views.html.Users.index.render(mymeds));
    }

}
