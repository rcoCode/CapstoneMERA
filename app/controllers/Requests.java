package controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jdk.nashorn.internal.ir.LiteralNode;
import models.*;
import models.Users;
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



/**
 * Created by anahigarnelo on 12/12/15.
*/
public class Requests extends Controller {
    public Result dispenserData(Long dID) {
        ObjectNode dispenserInformation = Json.newObject();
        ArrayNode containerContent = dispenserInformation.arrayNode();
        if (dID!=0) { //dID == 0 is the default value therefore an error
            Dispensor device = Dispensor.find.byId(dID);
            if (device!=null) {
                dispenserInformation.put("Dispenser ID",device.id);
//                dispenserInformation.put("Raspberry Pi ID",device.dispenser);
                String sTime = device.startTime.toString("hh:mm aa");
                String eTime = device.endTime.toString("hh:mm aa");
                dispenserInformation.put("Operation Start Time",sTime);
                dispenserInformation.put("Operation End Time",eTime);
                if (device.owner!=null) {
                    List<Containers> containers;
                    containers = Containers.find.where().eq("device",device).eq("empty",false).findList();
                    if (containers!=null) {
                        for (Containers container: containers) {
                            ObjectNode containerInformation = Json.newObject();
                            containerInformation.put("Container ID", container.id);
                            containerInformation.put("Pill Count", container.pillCount);
                            if (container.medication!=null) {
                                ObjectNode medicine = Json.newObject();
//                                medicine.put("Name",container.medication.name);
                                medicine.put("Dose", container.medication.dose);
                                medicine.put("Frequency", container.medication.frequency);
                                containerInformation.put("Medication",medicine);
                            }
                            containerContent.add(containerInformation);
                        }
                    }
                }
            }
        }
        String recipient = "garnelo.anahi@gmail.com";
        String rFName = "Anahi";
        String rLName = "Garnelo";
        String pFName = "Rebeca";
        String pLName = "Otero";
        String statusType = "missed a medication dose";
        sendEmail(recipient,rFName,rLName,pFName,pLName,statusType);
        dispenserInformation.put("Containers",containerContent);
        return ok(dispenserInformation);
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