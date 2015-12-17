package controllers;

import models.*;
import models.Users;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import play.data.Form;
import play.mvc.*;
import views.html.*;
import java.util.List;

import static play.data.Form.form;

/**
 * Created by rebeca on 11/17/2015.
 */
public class Meds extends Controller{
    @Security.Authenticated(UserAuth.class)
    public Result index(){
        return ok(views.html.Meds.index.render(""));
    }

    @Security.Authenticated(UserAuth.class)
    public Result createMed(Long id) {
        models.Users owns = Users.find.byId(id);
        if (Long.parseLong(session().get("user_id")) != owns.id){
            flash("error","You cannot perform this action");
            return redirect(routes.Users.index(Long.parseLong(session().get("user_id"))));
        }

        Form<models.Meds> medsForm = form(models.Meds.class).bindFromRequest();
        String med_name=medsForm.data().get("med_name");
        String dosage=medsForm.data().get("dosage");
        String inTime=medsForm.data().get("startDate");
        String dailyTime=medsForm.data().get("timeDaily");
        String freq=medsForm.data().get("freq");
        String week=medsForm.data().get("week");
        String month=medsForm.data().get("month");
        String pills=medsForm.data().get("pills");
        Long nDose=Long.parseLong(dosage,10);

        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime startDate = format.parseDateTime(inTime);
        DateTimeFormatter hourFormat = DateTimeFormat.forPattern("hh:mm");
        DateTime timeDaily = hourFormat.parseDateTime(dailyTime);

        Long hFreq=Long.parseLong(freq,10);
        Long nWeek=Long.parseLong(week,10);
        Long nMonth=Long.parseLong(month,10);
        Long pillCount=Long.parseLong(pills,10);

        Long userID=Long.parseLong(session().get("user_id"));
        models.Users user = models.Users.find.byId(userID);

        Dispensor device = Dispensor.find.where().eq("owner",user).findUnique();

        models.Meds nMed = models.Meds.createNewMed(med_name,nDose,startDate,timeDaily,hFreq,nWeek,nMonth,pillCount,device);

        if(nMed==null){
            flash("error","No Container Could Be Found"); //Most likely error
            //flash("error","Invalid Medication or No Container Could Be Found");
        }

        return redirect(routes.Users.index(user.id));
    }
}
