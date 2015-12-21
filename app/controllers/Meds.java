package controllers;

import models.*;
import models.Users;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import play.data.Form;
import play.mvc.*;
import views.html.*;

import java.util.*;

import static play.data.Form.form;

/**
 * Created by rebeca on 11/17/2015.
 */
public class Meds extends Controller{
    @Security.Authenticated(UserAuth.class)
    public Result index(Long id){
        Long u_id = Long.parseLong(session().get("user_id"));
        if (id != u_id){
            flash("error","You do not have access to this page!");
            return redirect(routes.Users.index(u_id));
        }
        Users logged = Users.find.byId(u_id);
        Dispensor device = logged.device;
        List<Containers> containers = logged.myMeds;
        List<String> days = Arrays.asList("Mon","Tues","Weds","Thurs","Fri","Sat","Sun");
        return ok(views.html.Meds.index.render(containers,days));
    }

    @Security.Authenticated(UserAuth.class)
    public Result createMed(Long id) {
        models.Users owns = Users.find.byId(id);
        Long u_id = Long.parseLong(session().get("user_id"));
        if (u_id != owns.id){
            flash("error","You cannot perform this action");
            return redirect(routes.Users.index(Long.parseLong(session().get("user_id"))));
        }

        Form<models.Meds> medsForm = form(models.Meds.class).bindFromRequest();
        String med_name=medsForm.data().get("med_name");
        String dosage=medsForm.data().get("dosage");
        String inTime=medsForm.data().get("startDate");
        String dailyTime=medsForm.data().get("timeDaily");
        String contain = medsForm.data().get("con");
        String freq=medsForm.data().get("freq");
        String pills=medsForm.data().get("pills");
        String mon=medsForm.data().get("mon");
        String tue=medsForm.data().get("tue");
        String wed=medsForm.data().get("wed");
        String thu=medsForm.data().get("thu");
        String fri=medsForm.data().get("fri");
        String sat=medsForm.data().get("sat");
        String sun=medsForm.data().get("sun");

        if(med_name == null || dosage == null || inTime == null || dailyTime == null || freq == null || pills == null){
            flash("error","All fields must be filled");
            return redirect(routes.Users.index(u_id));
        }

        //Convert formats
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime startDate = format.parseDateTime(inTime);
        DateTimeFormatter hourFormat = DateTimeFormat.forPattern("hh:mm");
        DateTime timeDaily = hourFormat.parseDateTime(dailyTime);
        Long nDose=Long.parseLong(dosage,10);
        Long hFreq=Long.parseLong(freq,10);
        Long pillCount=Long.parseLong(pills,10);
        Long c_id = Long.parseLong(contain,10);
        List<String> week = new ArrayList<>();
        if(mon.equalsIgnoreCase("y")){
            week.add("Mon");
        }
        if(tue.equalsIgnoreCase("y")){
            week.add("Tues");
        }
        if(wed.equalsIgnoreCase("y")){
            week.add("Weds");
        }
        if(thu.equalsIgnoreCase("y")){
            week.add("Thurs");
        }
        if(fri.equalsIgnoreCase("y")){
            week.add("Fri");
        }
        if(sat.equalsIgnoreCase("y")){
            week.add("Sat");
        }
        if(sun.equalsIgnoreCase("y")){
            week.add("Sun");
        }
        if (week.isEmpty()){
            flash("error","Days to dispense required");
            return redirect(routes.Users.index(Long.parseLong(session().get("user_id"))));
        }

        //*****//
        Containers holding = Containers.find.byId(c_id);

        if (u_id != holding.owner.id){
            flash("error","You cannot perform this action");
            return redirect(routes.Users.index(Long.parseLong(session().get("user_id"))));
        }

        if (holding != null) {
            holding.save();
            models.Meds nMed = models.Meds.createNewMed(med_name, nDose, startDate, timeDaily,week, hFreq, holding);
            nMed.save();
            holding.medication = nMed;
            holding.pillCount = pillCount;
            holding.empty = false;
            holding.save();
            if (nMed.storedIn == null) {
                flash("error", "No Container Could Be Found"); //Most likely error
                //flash("error","Invalid Medication or No Container Could Be Found");
                return redirect(routes.Users.index(u_id));
            }
            flash("success", "New Medication information stored");
            return redirect(routes.Users.index(u_id));
        }
        flash("error","Container not found");
        return redirect(routes.Users.index(u_id));
    }

    @Security.Authenticated(UserAuth.class)
    public Result show(Long id){
        Long u_id = Long.parseLong(session().get("user_id"));
        models.Meds display = models.Meds.find.byId(id);
        if (display == null){
            flash("error","Cannot complete request");
            return notFound("Not Found!");
        }
        if (display.storedIn == null){
            flash("error","Medication not linked to container");
            return redirect(routes.Users.index(u_id));
        }
        if (display.storedIn.device == null){
            flash("error","medication not linked to device");
            return redirect(routes.Users.index(u_id));
        }
        if(display.storedIn.device.owner == null){
            flash("error","This container has no id");
            return redirect(routes.Users.index(u_id));
        }
        if (u_id != display.storedIn.device.owner.id){
            flash("error","You do not have access to this page");
            return redirect(routes.Users.index(u_id));
        }
        return ok(views.html.Meds.show.render(display));
    }

}
