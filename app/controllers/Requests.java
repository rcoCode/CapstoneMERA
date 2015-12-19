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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import play.data.DynamicForm;


/**
 * Created by anahigarnelo on 12/12/15.
*/
public class Requests extends Controller {
    public Result dispenserData(Long dID) {
        ObjectNode dispenserInformation = Json.newObject();
        ArrayNode containerContent = dispenserInformation.arrayNode();
        Boolean validID = false;
        if (dID!=0) { //dID == 0 is the default value therefore an error
            Dispensor device = Dispensor.find.where().eq("dispenser",dID).findUnique();
            if (device!=null) {
                validID = true;
                dispenserInformation.put("Dispenser ID",device.dispenser);
                String sTime = device.startTime.toString("hh:mm aa");
                String eTime = device.endTime.toString("hh:mm aa");
                dispenserInformation.put("Operation Start Time",sTime);
                dispenserInformation.put("Operation End Time",eTime);
                if (device.owner!=null) {
                    List<Containers> containers;
                    containers = Containers.find.where().eq("device",device).eq("empty",false).findList();
                    if (containers!=null) {
                        for (Containers container: containers) {
//                            container.updated = false;
                            if (container.medication!=null && container.medication.updated==true) {
                                ObjectNode containerInformation = Json.newObject();
                                containerInformation.put("Container ID", container.container);
                                containerInformation.put("Pill Count", container.pillCount);
//                                containerInformation.put("Updated", container.medication.updated);
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

        else {
            //Here is the redirect if not valid id
        }

        String recipient = "garnelo.anahi@gmail.com";
        String rFName = "Anahi";
        String rLName = "Garnelo";
        String pFName = "Rebeca";
        String pLName = "Otero";
        String statusType = "missed a medication dose";
//        sendEmail(recipient,rFName,rLName,pFName,pLName,statusType);
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
//                        System.out.print("NEW CONTAINER\n");
                        Containers.createContainer(device,containerID);
                    }
                    else {
//                        System.out.print("containerID "+ containerID+'\n');
//                        System.out.print("UPDATE CONTAINER\n");
                        Containers.emptyContainer(device, containerID);
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


        }

        return redirect("/");
    }


    @Inject MailerClient mailerClient;
    public void sendEmail(String recipient, String rFName, String rLName, String pFName, String pLName, String statusType) {
        String notification = "MERA Pill Dispenser Notifications";
        Email email = new Email();
        email.setSubject(notification);
        email.setFrom("MERA Pill Dispenser Notifications <merapd11852@gmail.com>");
        email.addTo(rFName + " " + rLName + " TO <" + recipient + ">");
        email.setBodyHtml("<html><body><p><b>"+ "MERA Pill Dispenser Notifications"+ "</b></p><p>Hello "+rFName+" "+ rLName+",</p><p>"+"This is a notification regarding: <b>" + pFName + " " + pLName +
        "</b>, this person "+ statusType + " on " + Calendar.getInstance().getTime()+"<p>Kind Regards,<p>MERA: Mike | Emily | Rebeca | Anahi</p></p></body></html>");
        mailerClient.send(email);
    }
}

//https://github.com/playframework/play-mailer