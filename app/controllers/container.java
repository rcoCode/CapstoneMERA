package controllers;

import models.*;
import models.Meds;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static play.data.Form.form;

/**
 * Created by rebeca on 12/17/2015.
 */
public class container extends Controller{
    @Security.Authenticated(UserAuth.class)
    public Result index(Long id){
        Long u_id = Long.parseLong(session().get("user_id"));
        Containers editing = Containers.find.byId(id);
        if (editing == null){
            flash("error","Error displaying page");
            return notFound("Not Found!");
        }
        if (u_id != editing.owner.id){
            flash("error","You do not have access to this page");
            return redirect(routes.Users.index(u_id));
        }
        return ok(views.html.containers.index.render(editing));
    }

    @Security.Authenticated(UserAuth.class)
    public Result edit(Long id){
        Long u_id = Long.parseLong(session().get("user_id"));
        Containers editing = Containers.find.byId(id);
        if (editing == null){
            flash("error","Error displaying page");
            return notFound("Not Found!");
        }
        if (u_id != editing.owner.id){
            flash("error","You do not have access to this page");
            return redirect(routes.Users.index(u_id));
        }

        Form<models.Meds> medsForm = form(models.Meds.class).bindFromRequest();
        String med_name=medsForm.data().get("med_name");
        String dosage=medsForm.data().get("dosage");
        String inTime=medsForm.data().get("startDate");
        String dailyTime=medsForm.data().get("timeDaily");
        String freq=medsForm.data().get("freq");
        String pills=medsForm.data().get("pills");
        if(med_name == null || dosage == null || inTime == null || dailyTime == null || freq == null|| pills == null){
            flash("error","All fields must be filled");
            return redirect(routes.Users.index(Long.parseLong(session().get("user_id"))));
        }

        String mon=medsForm.data().get("mon");
        String tue=medsForm.data().get("tue");
        String wed=medsForm.data().get("wed");
        String thu=medsForm.data().get("thu");
        String fri=medsForm.data().get("fri");
        String sat=medsForm.data().get("sat");
        String sun=medsForm.data().get("sun");
        if(med_name == null || dosage == null || inTime == null || dailyTime == null || freq == null ||  pills == null){
            flash("error","All fields must be filled");
            return redirect(routes.Users.index(u_id));
        }
        List<String> week= new ArrayList<>();
        if(mon.equalsIgnoreCase("y")){
            week.add("mon");
        }
        if(tue.equalsIgnoreCase("y")){
            week.add("tue");
        }
        if(wed.equalsIgnoreCase("y")){
            week.add("wed");
        }
        if(thu.equalsIgnoreCase("y")){
            week.add("thu");
        }
        if(fri.equalsIgnoreCase("y")){
            week.add("fri");
        }
        if(sat.equalsIgnoreCase("y")){
            week.add("sat");
        }
        if(sun.equalsIgnoreCase("y")){
            week.add("sun");
        }
        if(week.isEmpty()){
            flash("error","Days to dispense are required");
            return redirect(routes.Users.index(u_id));
        }

        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime startDate = format.parseDateTime(inTime);
        DateTimeFormatter hourFormat = DateTimeFormat.forPattern("hh:mm");
        DateTime timeDaily = hourFormat.parseDateTime(dailyTime);

        Long nDose=Long.parseLong(dosage,10);
        Long hFreq=Long.parseLong(freq,10);
        Long pillCount=Long.parseLong(pills,10);

        Long userID=Long.parseLong(session().get("user_id"));
        models.Users user = models.Users.find.byId(userID);
        editing.pillCount =pillCount;
        editing.save();

        if (editing.medication.name != med_name){
            Meds dMed = editing.medication;
            dMed.delete();
            models.Meds nMed = models.Meds.createNewMed(med_name,nDose,startDate,timeDaily,week,hFreq,editing);
            nMed.save();
            editing.medication =nMed;
            editing.save();
            flash("success","Saved Changes to container");
            return redirect(routes.Users.index(u_id));
        }
        else{
            Meds changed = editing.medication;
            changed.dose = nDose;
            changed.dailyTime = timeDaily;
            changed.schedule = startDate;
            changed.frequency = hFreq;
            changed.days = week;
            changed.storedIn = editing;
            changed.save();
            flash("success","Saves Changes to container");
            return redirect(routes.Users.index(u_id));
        }
    }

    @Security.Authenticated(UserAuth.class)
    public Result removeMed(Long id){
        Long u_id = Long.parseLong(session().get("user_id"));
        Containers edit = Containers.find.byId(id);
        if (u_id != edit.owner.id){
            flash("error","You cannot perform this action!");
            return redirect(routes.Users.index(u_id));
        }
        Meds removing = edit.medication;
        edit.medication = null;
        removing.delete();
        edit.empty = true;
        edit.save();
        flash("success","Medications has been removed");
        return redirect(routes.Users.index(u_id));
    }

}
