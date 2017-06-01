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
 * Controls the GET and POST requests for pages and actions related to medications
 */
public class Meds extends Controller{
    /*Controls the GET request for the medication form. It takes a user id as an argument and searches
    * for the logged user in the database. It The device and containers for that user are retrieved
    * and the medication form is rendered with access to that information for the user*/
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
    /*Controls the POST for the medication form. It takes the user id as an argument. The function
    * ensures that all the fields were received from the user and converts the date, time, and number fields to
     * the proper format. It is currently set up to receive the days of the week that the user wishes
      * the medication to be dispense but this a feature that would be added in the future on the device.
      * The information is stored and the medication is added. The container selected by the user is found
      * in the database and the medication, empty, and pill count attributes are updates. Both the medication
      * and container changes are stored and the user is redirected to their main page.*/
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
        DateTimeFormatter hourFormat = DateTimeFormat.forPattern("HH:mm");
        DateTime timeDaily = hourFormat.parseDateTime(dailyTime);
        Long nDose=Long.parseLong(dosage,10);
        Long hFreq=Long.parseLong(freq,10);
        Long pillCount=Long.parseLong(pills,10);
        Long c_id = Long.parseLong(contain,10);
        ArrayList<String> week = new ArrayList<>();
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
            models.Meds nMed = models.Meds.createNewMed(med_name, nDose, startDate,timeDaily,week,hFreq, holding);

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
            if(nMed.days.isEmpty()){
                flash("error","DAYS EMPTY");
                return redirect(routes.Users.index(u_id));
            }
            System.out.print("Days" + nMed.days + '\n');
            flash("success", "New Medication information stored");
            return redirect(routes.Users.index(u_id));
        }
        flash("error","Container not found");
        return redirect(routes.Users.index(u_id));
    }
    /*This function handle the GET request to display the medication information in
    * a particular container. It takes a medication id as an argument. The function makes sure that
    * the user is logged in and that the medication data is link to a container which the logged in
    * user owns. The medication retrieved is then rendered for access on the user page.*/
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
        if (display.days == null){
            flash("error","Days are a bitch");
            return redirect(routes.Users.index(u_id));
        }
        System.out.print("Days" + display.days + '\n');
        ArrayList<String> wkdays = display.days;
        return ok(views.html.Meds.show.render(display,wkdays));
    }

}