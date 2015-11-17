package controllers;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.*;
import static play.data.Form.form;
import models.Meds;

import java.util.List;

/**
 * Created by rebeca on 11/17/2015.
 */
public class Users extends Controller{
    public Result index() {
        List<Meds> meds= Meds.find.all();
        return ok(views.html.Users.index.render("Users"));
    }

    public Result createMed() {
        Form<Meds> medsForm = form(Meds.class).bindFromRequest();
        String med_name=medsForm.data().get("med_name");
        String dosage=medsForm.data().get("dosage");
        String week=medsForm.data().get("week");
        String month=medsForm.data().get("month");
        String day=medsForm.data().get("day");


        Long nDose=Long.parseLong(dosage,10);
        Long nDay=Long.parseLong(day,10);
        Long nWeek=Long.parseLong(week,10);
        Long nMonth=Long.parseLong(month,10);



        Meds nMed = Meds.createNewMed(med_name,nDose,nWeek,nMonth,nDay);

        if(nMed==null){
            flash("error","Invalid Medication");
        }

        return redirect(routes.Users.index());
    }
}
