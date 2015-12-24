package controllers;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.net.HttpHeaders;
import jdk.nashorn.internal.ir.LiteralNode;
import models.*;
import models.Users;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import play.api.libs.json.JsValue;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import com.fasterxml.jackson.databind.JsonNode;
import controllers.Meds;
import scala.util.parsing.json.JSONObject$;
import org.apache.commons.mail.EmailAttachment;
import play.Play;
import play.api.libs.mailer.MailerClient;
import play.libs.mailer.Email;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import play.data.DynamicForm;


/**
 * Created by anahigarnelo on 12/12/15.
*/

/*
Requests Classes: Main purpose is to handle HTTP requests
 GET       Dispenser Information
 POST      Available Containers
 POST      Logs for Device Events
*/
public class Requests extends Controller {

    /*
    dispenserData is a function meant to respond to a HTTP Get and deliver information about a device's containers.
    The parameter passed to the function is the dispenser id of the device.
        The function will find the Dispenser with the id passed by the HTTP get request. This id isn't the internal id
        of the dispenser so we must query to retrieve the proper dispenser.
        We check that the device is a valid device and add to an object node: Dispenser id, startTime, endTime of the
        dispenser. Then we retrieve the owner of the device and query to obtain the containers of the device by checking
        if they belong to the device. Time must be formatted before storing it in the object node.
        Then, we iterate through all the containers found and check if the container has medication information
        stored in it and if the medication information has been updated. Then, we create a json object and save
        the container's id, pill count and set the container to not updated since we have retrieved the data.
        We then also store the container's medication data which include dose and frequency in another object node.
        Although a new object node wasn't strictly necessary it simplifies processing on the sqlite3 database of the
        Raspberry Pi since the data will be saved into two different tables. Each container is pushed into an array of
        containers.
        After doing this, we check that in fact we retrieved updated containers and if we have updated containers return
        the updated containers. If not, we simply return the dispenser's information as a base case.
     The return value of this function is the information of updated container's for the device requested.
     */
    public Result dispenserData(Long dID) {
        ObjectNode dispenserInformation = Json.newObject();
        ArrayNode containerContent = dispenserInformation.arrayNode();
        if (dID!=0) {
            Dispensor device = Dispensor.find.where().eq("dispenser",dID).findUnique();
            if (device!=null) {
                dispenserInformation.put("Dispenser ID",device.dispenser);
                String sTime = device.startTime.toString("hh:mm aa");
                String eTime = device.endTime.toString("hh:mm aa");
                dispenserInformation.put("Operation Start Time",sTime);
                dispenserInformation.put("Operation End Time",eTime);
                if (device.owner!=null) {
                    List<Containers> containers;
                    containers = Containers.find.where().eq("device",device).eq("empty",false).findList();
                    if (containers.isEmpty()) {
                        for (Containers container: containers) {
                            if (container.medication!=null && container.medication.updated==true) {
                                ObjectNode containerInformation = Json.newObject();
                                containerInformation.put("Container ID", container.container);
                                containerInformation.put("Pill Count", container.pillCount);
                                container.medication.updated = false;
                                container.medication.save();
                                ObjectNode medicine = Json.newObject();
                                medicine.put("Dose", container.medication.dose);
                                medicine.put("Frequency", container.medication.frequency);
                                containerInformation.put("Medication",medicine);
                                containerContent.add(containerInformation);
                            }
                        }
                    }
                }
            }
        }

        if (containerContent.size() != 0) {
            dispenserInformation.put("Updated", true);
            dispenserInformation.put("Containers", containerContent);
        }
        else {
            dispenserInformation.put("Updated", false);
        }
        return ok(dispenserInformation);
    }

    /*
    containerInfo is a function that will handle an HTTP Post of available containers.
    There is no input parameter for this function but the we must bind to the request and verify that a json
    string was given to the Post request.
        We begin by retrieving the dispenser id and find the corresponding device with a query.
        Then, we retrieve an array list of containers and for each check if the container is available.
        If the container is available then it means that either it is a new container added to the device
        or that it is a container that has become available, perhaps the patient no longer needs to take this medication.
        If the container doesn't exist, we will create a new container with the container id provided.
        Else, we will empty the container that already exists.
     We return a redirect to the home page upon completion.
     */
    @BodyParser.Of(BodyParser.TolerantJson.class)
    public Result containerInfo() {
        JsonNode json = request().body().asJson();
        if(json == null) {
            return badRequest("Expecting Json data");
        }
        else {
//            System.out.print("POST RECEIVED\n\n");
            String dID = json.get("Dispenser ID").toString();
            Dispensor device = Dispensor.find.where().eq("dispenser", Long.parseLong(dID)).findUnique();
            for (int i=0; i<json.get("Containers").size();i++) {
                Long containerID = Long.parseLong(json.get("Containers").get(i).get("Container ID").toString());
                Boolean availability = Boolean.valueOf(json.get("Containers").get(i).get("Available").toString());
                if (availability == true) {
                    models.Containers container = Containers.find.where().eq("container", containerID).findUnique();
                    if (container==null) {
                        Containers.createContainer(device,containerID);
                    }
                    else {
                        Containers.emptyContainer(container,device);
                    }
                }
            }
        }
        return redirect("/");
    }

    /*
    logActions is a function that handle Post requests for logs.
    There is no input parameter for this function but we must bind the request data and verify that it a json string.
        We begin by retrieving the dispenser and the time of the log action. Time is formatted in the json string
        so we must convert time into a data type java can store.
        We have three types of return types so we get each type and then check to see if they are null.
        We can have a combination of all three types or only one type.
        All three types are handled exactly the same way with logs stored.
        We retrieve the container id, message, the scheduled time the event was supposed to happen and the actual time
        the status was logged. Then, we create a new log with this data and the status type.
        For errors and warnings, we have an additional task of sending emails. This could only be done through localhost.
        The website deployed to heroku will only display the logs.
    We return a redirect to home page upon completion.
     */
    @BodyParser.Of(BodyParser.TolerantJson.class)
    public Result logActions () {
        JsonNode json = request().body().asJson();
        if (json==null){
            return badRequest("Expecting Json Data");
        }
        else {
            DateTimeFormatter format = DateTimeFormat.forPattern("MM/dd/yyyy hh:mm aa");
            Long dID = Long.parseLong(json.get("Dispenser ID").toString());
            Dispensor device = Dispensor.find.where().eq("dispenser",dID).findUnique();
            Long uID = device.owner.id;
            models.Users user = Users.find.byId(uID);
            JsonNode warnings = json.get("Warning");
            JsonNode successes = json.get("Success");
            JsonNode errors = json.get("Error");
            String date = json.get("Time Stamp").textValue();
            DateTime today = format.parseDateTime(date);
            if (warnings !=null) {
                /*if (warnings.size()!=0) {
                    sendEmail("Warnings",user,warnings,date,device);
                }
                */
                for (int i=0; i<warnings.size();i++) {
                    Long containerID = Long.parseLong(warnings.get(i).get("Container ID").toString());
                    Containers container = Containers.find.where().eq("device",device).eq("container",containerID).findUnique();
                    String message = warnings.get(i).get("Message").toString();
                    String sTime = warnings.get(i).get("Scheduled Time").textValue();
                    String eTime = warnings.get(i).get("Logged Time").textValue();
                    DateTime scheduledTime = format.parseDateTime(sTime);
                    DateTime eventTime = format.parseDateTime(eTime);
                    Log.createNewLog(scheduledTime, eventTime, message,container, user, "Warning");
                }
            }
            if (errors !=null) {
                /*if (errors.size()!=0) {
                    sendEmail("Errors",user,errors,date,device);
                }
                */


                for (int i=0; i<errors.size();i++) {
                    Long containerID = Long.parseLong(errors.get(i).get("Container ID").toString());
                    Containers container = Containers.find.where().eq("device",device).eq("container",containerID).findUnique();
                    String message = errors.get(i).get("Message").toString();
                    String sTime = errors.get(i).get("Scheduled Time").textValue();
                    String eTime = errors.get(i).get("Logged Time").textValue();
                    DateTime scheduledTime = format.parseDateTime(sTime);
                    DateTime eventTime = format.parseDateTime(eTime);
                    Log.createNewLog(scheduledTime, eventTime, message,container, user, "Error");
                }
            }
            if (successes !=null) {
                for (int i=0; i<successes.size();i++) {
                    Long containerID = Long.parseLong(successes.get(i).get("Container ID").toString());
                    Containers container = Containers.find.where().eq("device",device).eq("container",containerID).findUnique();
                    String message = successes.get(i).get("Message").toString();
                    String sTime = successes.get(i).get("Scheduled Time").textValue();
                    String eTime = successes.get(i).get("Logged Time").textValue();
                    DateTime scheduledTime = format.parseDateTime(sTime);
                    DateTime eventTime = format.parseDateTime(eTime);
                    Log.createNewLog(scheduledTime, eventTime, message,container, user, "Success");
                }
            }
        }
        return redirect("/");
    }

    /*
    sendEmail is a function that sends an email to the contacts of the users saved.
    The input parameters are the status type (errors or warnings), the user the device belongs to,
    the json node containing the information for the error or warning, the string of the day the json post was
    retrieved (it was part of the post but it's not within the error or warning json node) and the device the
    warning is about.
        We retrieve the device user's name and contacts.
        The email will be sent using html to format so setup a table. Writing the email on one line so we save
        the html code as smaller strings (logMessage).
        The json node's data won't change for each email so we append the data into a string called log message.
        Log message is a table of all the logs. Each row will have a container id, a medication name,
        warning/error message, scheduled time and logged time.
        Then, we will iterate through contacts to send an email to each of these contacts.
        For each contact, we retrieve the contact's name and email address and format the email (.setBodyHtml) and
        send the email.
        The email address from which we send this email is saved in the application.conf file.
     Sending emails was done as described at: https://github.com/playframework/play-mailer
     */
    @Inject MailerClient mailerClient;
    public void sendEmail(String statusType, Users user, JsonNode statusMessage, String today, Dispensor device) {
        if (statusType.isEmpty() || user==null || statusMessage==null || today.isEmpty() || device==null){
            System.out.print("Parameter Errors\n");
        }
        String notification = "MERA Pill Dispenser";
        String pFName = user.Fname;
        String pLName = user.Lname;
        List<Contact> recipients = user.contacts;
        if (recipients.isEmpty())
            System.out.print("No recipients to send to\n");
        DateTimeFormatter format = DateTimeFormat.forPattern("MM/dd/yyyy hh:mm aa");
        String logMessage = "<table style=\"width:100%\">";
        logMessage+="<caption>"+statusType+"</caption>";
        logMessage+="<tr><td>Container ID</td>";
        logMessage+="<td>Medication</td>";
        logMessage+="<td>Message</td>";
        logMessage+="<td>Scheduled Time</td>";
        logMessage+="<td>Logged Event Time</td></tr>";
        for (int j = 0; j < statusMessage.size(); j++) {
            logMessage+="<tr>";
            Long containerID = Long.parseLong(statusMessage.get(j).get("Container ID").toString());
            Containers container = Containers.find.where().eq("device",device).eq("container",containerID).findUnique();
            String message = statusMessage.get(j).get("Message").textValue();
            String sTime = statusMessage.get(j).get("Scheduled Time").textValue();
            String eTime = statusMessage.get(j).get("Logged Time").textValue();
            if (containerID==null || container==null || message.isEmpty() || sTime.isEmpty() || eTime.isEmpty()) {
                System.out.print("Something error\n");
            }
            logMessage += "<td>" + containerID + "</td>";
            logMessage += "<td>" + container.medication.name + "</td>";
            logMessage += "<td>" + message + "</td>";
            logMessage += "<td>" + sTime + "</td>";
            logMessage += "<td>" + eTime + "</td>";
            logMessage+="</tr>";
        }
        logMessage += "</table>";
        String bodyMessage = "<br>This is a notification regarding: <b>";
        String person = pFName + " " + pLName +"</b>.<br>";
        String following = "The following ";
        String encountered = " were encountered on ";
        String closing = "<p>Kind Regards,<br>MERA: Mike | Emily | Rebeca | Anahi</p>";
        for (int i = 0; i < recipients.size(); i++) {
            String rFName = recipients.get(i).fName;
            String rLName = recipients.get(i).lName;
            String rEmail = recipients.get(i).email;
            String greeting = "<p>Hello " +  rFName + " " + rLName + ",";
            Email email = new Email();
            email.setSubject(notification);
            email.setFrom("MERA Pill Dispenser <merapd11852@gmail.com>");
            email.addTo(rFName + " " + rLName + " TO <" + rEmail + ">");
            email.setBodyHtml("<html><body><p><b>" + notification + " " + statusType + "</b></p>" + greeting + bodyMessage + person + following + statusType.toLowerCase() + encountered + today + ".<br></p>" + logMessage + closing + "</body></html>");
            mailerClient.send(email);
        }
    }
}

