package controllers;

import models.Users;
import play.*;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;
import views.html.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        String startTime=userForm.data().get("oST")+" "+userForm.data().get("stc");
        String endTime=userForm.data().get("oET")+" "+userForm.data().get("etc");

        //Date Time
        Date sTime = null;
        DateFormat inFormat= new SimpleDateFormat("hh:mm aa");
        try {
            sTime = inFormat.parse(startTime);
        }
        catch (ParseException e){
            e.printStackTrace();
        }
        Date eTime = null;
        try {
            eTime = inFormat.parse(endTime);
        }
        catch (ParseException e){
            e.printStackTrace();
        }

        Users newUser = Users.createNewUser(username, password, fName, lName, Long.valueOf(dID), sTime, eTime);

        if(newUser == null) {
            flash("error", "Invalid user");
            return redirect(routes.Application.index());
        }

        flash("success", "Welcome new user " + newUser.username);
        session("user_id", newUser.id.toString());
        return redirect(routes.Users.index());
    }

    public Result logout() {
        session().remove("user_id");
        return redirect(routes.Application.index());
    }



}
