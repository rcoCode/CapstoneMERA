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
 */
public class contacts extends Controller{
    @Security.Authenticated(UserAuth.class)
    public Result index(){
        return ok(views.html.Contact.index.render(""));
    }

    @Security.Authenticated(UserAuth.class)
    public Result create(Long id){
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

        Contact carer = new Contact();
        carer.fName = fname;
        carer.lName = lname;
        carer.email = email;
        carer.caredFor.add(care);
        carer.save();
        flash("success","New contact added");
        return redirect(routes.Users.index(u_id));
    }

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
            flash("success","Contact removed");
            return redirect(routes.Users.index(u_id));
        }
        flash("error","Could not remove contact, please try again.");
        return redirect(routes.Users.index(u_id));
    }
}
