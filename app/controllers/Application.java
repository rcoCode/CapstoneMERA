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
    /*Login() function controlls the POST function for the user login it receives the username
    and password entered by the user in the login form. It finds the user by the username and
    calls authenticate(password) to check that the password is correct. It then stores session
    information ad redirects to the User page for a successful login or returns tot the index page
    in the case of a login error*/
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
    //Renders the sign up page for the sing up GET route
    public Result signup() {
        return ok(views.html.signup.render(""));
    }
    /*Controls the POST function for the user sign up page. It receives the username, password, first name,
    last name, device id, and device start and end times for the for the device. It then converts the time
    and number inputs to the correct format to be stored in the database. The createNewUser() function is
    called to store the user input in a new user and returns the user created. If the returned user is not
    null then the user input was correct and the device is searched for. Currently a new device is created
    if one cannot be found in the database. If the device is in the system there is a check to make sure
    that the device is not already attached to a user. If all the input is correct the user and device are
    saved, session information is stored, and the user is redirected to their main page. Invalid inputs
    cause a redirect to the index page */
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

//    public void demo(){
//        flash("success", "Welcome demo ");
//    }

    //logout() is the function that controls the logout POST. it removes the session id and returns
    // the user to the index page
    public Result logout() {
        session().remove("user_id");
        return redirect(routes.Application.index());
    }

}
