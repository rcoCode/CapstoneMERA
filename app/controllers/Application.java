package controllers;

import models.Dispensor;
import models.Users;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import play.*;
import play.data.DynamicForm;
import play.mvc.*;
import views.html.*;
import static play.data.Form.form;

//http://thawing-ravine-9396.herokuapp.com/

/* This class controlls the functions needed to access the index page, login, sign up,
   and log out of the website
*/
public class Application extends Controller {
    //Renders the index page, used by the GET route for the index page
    public Result index() {
        return ok(index.render("MERA Dispenser"));
    }
    //Login() function controlls the POST function for the user login it receives the username
    //and password entered by the user in the login form. It finds the user by the username and
    //calls authenticate(password) to check that the password is correct. It then redirects to the
    //User page for a successful login or returns tot the index page in the case of a login error
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
    //The
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
        DateTimeFormatter format = DateTimeFormat.forPattern("HH:mm");
        DateTime sTime = format.parseDateTime(startTime);
        DateTime eTime = format.parseDateTime(endTime);

        Users newUser = Users.createNewUser(username, password, fName, lName);

        if(newUser == null) {
            flash("error", "Invalid user");
            return redirect(routes.Application.index());
        }
        newUser.save();
        Long d_id = Long.parseLong(dID);
        Dispensor device = Dispensor.find.where().eq("dispenser",d_id).findUnique();
        if(device != null){
            if(device.owner != null){
                newUser.delete();
                flash("error","This device belongs to another user please double check your device id.");
                return redirect(routes.Application.index());
            }
        }
        if(device == null){
            device = Dispensor.createNewDispensor(newUser,sTime,eTime, Long.parseLong(dID));
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
