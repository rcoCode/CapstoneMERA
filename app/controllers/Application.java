package controllers;

import models.Users;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import play.*;
import play.data.DynamicForm;
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

        return redirect(routes.Users.index(users.id));
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
        String startTime=userForm.data().get("oST");
        String endTime=userForm.data().get("oET");

        //Date Time
        DateTimeFormatter format = DateTimeFormat.forPattern("hh:mm aa");
        DateTime sTime = format.parseDateTime(startTime);
        DateTime eTime = format.parseDateTime(endTime);


        Users newUser = Users.createNewUser(username, password, fName, lName, Long.valueOf(dID), sTime, eTime);

        if(newUser == null) {
            flash("error", "Invalid user");
            return redirect(routes.Application.index());
        }

        flash("success", "Welcome new user " + newUser.Fname);
        session("user_id", newUser.id.toString());
        return redirect(routes.Users.index(newUser.id));
    }

    public Result logout() {
        session().remove("user_id");
        return redirect(routes.Application.index());
    }



}
