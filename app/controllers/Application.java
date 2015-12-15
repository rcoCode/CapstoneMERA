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
            flash("success", "Welcome back " + users.Fname);
        } else {
            flash("error", "Invalid login. Check your username and password.");
            return redirect(routes.Application.index());
        }

        return redirect(routes.Users.index());
    }

    public Result signup() {
        return ok(views.html.signup.render(""));
    }

    public Result newUser() {
        DynamicForm userForm = form().bindFromRequest();
        String username = userForm.data().get("username");
        String password = userForm.data().get("password");
        String fName = userForm.data().get("fName");
        String lName = userForm.data().get("lName");
        String dID = userForm.data().get("dID");

        if (username.isEmpty() || password.isEmpty()) {
            //flash and redirect
        }

        //Users nUser = Users.createNewUser(username, password, fName, lName);
        Users nUser = Users.createNewUser(username, password, fName, lName, Long.valueOf(dID));

        if(nUser == null) {
            flash("error", "Invalid user");
            return redirect(routes.Application.index());
        }

        //nUser.save();

        flash("success", "Welcome new user " + nUser.username);
        session("user_id", nUser.id.toString());
        return redirect(routes.Users.index());
    }

    public Result logout() {
        session().remove("user_id");
        return redirect(routes.Application.index());
    }



}
