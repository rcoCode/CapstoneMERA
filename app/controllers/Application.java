package controllers;

import models.Users;
import play.*;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;

import views.html.*;

import static play.data.Form.form;

public class Application extends Controller {

    public Result index() {
        return ok(index.render("MERA Dispenser"));
    }

    public Result login() {
        DynamicForm userForm=form().bindFromRequest();
        String username=userForm.data().get("username");
        String password=userForm.data().get("password");

        Users users=Users.find.where().eq("username",username).findUnique();

        if(users != null && users.authenticate(password)) {
            session("user_id", users.id.toString());
            flash("success", "Welcome back " + users.username);
        } else {
            flash("error", "Invalid login. Check your username and password.");
        }

        return redirect(routes.Users.index());
    }

    public Result signup() {
        DynamicForm userForm = form().bindFromRequest();
        String username = userForm.data().get("username");
        String password = userForm.data().get("password");

        Users nUser = Users.createNewUser(username, password);

        if(nUser == null) {
            flash("error", "Invalid user");
            return redirect(routes.Application.index());
        }

        nUser.save();

        flash("success", "Welcome new user " + nUser.username);
        session("user_id", nUser.id.toString());
        return redirect(routes.Application.index());
    }

    public Result logout() {
        session().remove("user_id");
        return redirect(routes.Application.index());
    }

}
