package controllers;

import models.Containers;
import models.Dispensor;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.*;
import static play.data.Form.form;
import models.Meds;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by rebeca on 11/17/2015.
 */
public class Users extends Controller{
    public Result index() {
        List<Meds> meds= Meds.find.all();
        return ok(views.html.Users.index.render("Profile"));
    }

    public Result createMed() {
        Form<Meds> medsForm = form(Meds.class).bindFromRequest();
        String med_name=medsForm.data().get("med_name");
        String dosage=medsForm.data().get("dosage");
        String inTime=medsForm.data().get("time")+" "+medsForm.data().get("am");
        String dailyTime=medsForm.data().get("timeDaily")+" "+medsForm.data().get("tc");
        String week=medsForm.data().get("week");
        String month=medsForm.data().get("month");
        String day=medsForm.data().get("day");
        String pills=medsForm.data().get("pills");
        Long nDose=Long.parseLong(dosage,10);
        Date mTime= null;
        Date dTime = null;
        DateFormat inFormat= new SimpleDateFormat("hh:mm aa");
        try {
            mTime = inFormat.parse(inTime);
        }
        catch (ParseException e){
            e.printStackTrace();
        }
        DateFormat inDailyFormat = new SimpleDateFormat("hh:mm aa");
        try {
            dTime = inFormat.parse(dailyTime);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        Long nDay=Long.parseLong(day,10);
        Long nWeek=Long.parseLong(week,10);
        Long nMonth=Long.parseLong(month,10);
        Long pillCount=Long.parseLong(pills,10);

        Long userID=Long.parseLong(session().get("user_id"));
        models.Users user = models.Users.find.byId(userID);

        Dispensor device = Dispensor.find.where().eq("owner",user).findUnique();

        Meds nMed = Meds.createNewMed(med_name,nDose,mTime,dTime,nWeek,nMonth,nDay, pillCount,device);

        if(nMed==null){
            flash("error","No Container Could Be Found"); //Most likely error
            //flash("error","Invalid Medication or No Container Could Be Found");
        }

        return redirect(routes.Users.index());
    }
}
