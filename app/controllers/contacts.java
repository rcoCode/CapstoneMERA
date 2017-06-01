package controllers;

import models.*;
import models.Users;
import play.api.data.Form;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created by rebeca on 12/19/2015.
 * Controller to handle the GET and POST requests for the contacts
 */
public class contacts extends Controller{
    /*Handles the GET request to access the create contact form. It takes the user id as argument.
    * The UserAuth ensures that a user is logged in before rendering the page and that the session id
    * matches the user id given. The session matches, the page is rendered.*/
    @Security.Authenticated(UserAuth.class)
    public Result index(Long id){
        Long u_id = Long.parseLong(session().get("user_id"));
        if(u_id != id){
            flash("error","You do not have access to this page");
            return redirect(routes.Users.index(u_id));
        }
        return ok(views.html.Contact.index.render(""));
    }
    /*Handles the POST for the create contact form. It takes the User id as an argument. The function
    * checks that all of the fields from the Contacts form were received and that the user was logged
    * in. If the form was submitted correctly the database is searched for a contact with the same email
    * as the contact entered. If one exists the contact is stored for the user. If not a new contact is
    * created with the give information. The user and contact are saved and the user is redirected to
    * their user page.*/
    @Security.Authenticated(UserAuth.class)
    public Result createCont(Long id){
        Long u_id = Long.parseLong(session().get("user_id"));
        if(u_id != id){
            flash("error","You do not have access to this page");
            return redirect(routes.Users.index(u_id));
        }
        DynamicForm contactForm = play.data.Form.form().bindFromRequest();
        String fname = contactForm.data().get("fname");
        String lname = contactForm.data().get("lname");
        String email = contactForm.data().get("email");
        Users care = Users.find.byId(u_id);
        if (fname.isEmpty() || lname.isEmpty() || email.isEmpty()){
            flash("error","All fields are required");
            return redirect(routes.Users.index(u_id));
        }
        Contact check = Contact.find.where().eq("email",email).findUnique();
        if(check != null){
            check.caredFor.add(care);
            check.save();
            flash("success","New contact added");
            return redirect(routes.Users.index(u_id));
        }

        Contact carer = new Contact();
        carer.fName = fname;
        carer.lName = lname;
        carer.email = email;
        carer.caredFor.add(care);
        carer.save();
        flash("success","New contact added");
        return redirect(routes.Users.index(u_id));
    }
    /*Controls the POST for the remove contact functionality. The function takes the contact id as
    * an argument. It searched the database for the contact and removes the contact from the list of
    * contacts and the user from the contact's list of users. The changes are saved to the database and
     * the user is redirected to their main page.*/
    @Security.Authenticated(UserAuth.class)
    public Result removeCont(Long id){
        Long u_id = Long.parseLong(session().get("user_id"));
        Contact contact = Contact.find.byId(id);
        if (contact == null){
            flash("error","Contact could not be found");
            return redirect(routes.Users.index(u_id));
        }
        Users current = Users.find.byId(u_id);
        if(current.contacts.remove(contact)){
            current.save();
            flash("success","Contact removed");
            return redirect(routes.Users.index(u_id));
        }
        flash("error","Could not remove contact, please try again.");
        return redirect(routes.Users.index(u_id));
    }
}
