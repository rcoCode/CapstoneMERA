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
 POST      


*/
public class Requests extends Controller {
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

    //https://github.com/playframework/play-mailer
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

