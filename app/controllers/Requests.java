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

import java.util.List;



/**
 * Created by anahigarnelo on 12/12/15.
 */
public class Requests extends Controller {
    //http://localhost:9000/dispenser?id=1
    public Result dispenserData(Long dID) {
        ObjectNode dispenserInformation = Json.newObject();
        ArrayNode containerContent = dispenserInformation.arrayNode();
        if (dID!=0) {
            Dispensor device = Dispensor.find.byId(dID);
            if (device!=null) {
                dispenserInformation.put("Dispenser ID",device.id);
//              NEED TO INITIALIZE START TIME AND END TIME!!!
//                dispenserInformation.put("Start Time",device.startTime);
//                dispenserInformation.put("End Time",device.endTime);
                if (device.owner!=null) {
                    List<Containers> containers;
                    containers = Containers.find.where().eq("device",device).eq("empty",false).findList();
                    if (containers!=null) {
                        ObjectNode containerInformation = Json.newObject();
                        for (Containers container: containers) {
                            containerInformation.put("Container ID", container.id);
                            containerInformation.put("PillCount", container.pillCount);
                            ObjectNode medicine = Json.newObject();
                            if (container.medication!=null) {
                                medicine.put("Medication Name",container.medication.name);
                                medicine.put("Dose",container.medication.dose);
                                medicine.put("Per Day",container.medication.perDay);
                                //medicine.put("Schedule",container.medication.schedule.toString());
                                containerInformation.put("Medication", medicine);
                            }
                            containerContent.add(containerInformation);
                        }
                    }
                }


            }
            else {
                //Error or Success, message
                flash("error","Cannot View This");
                //Routes directions
                return redirect(routes.Application.index());
            }
        }
        dispenserInformation.put("Containers",containerContent);
        //return ok(views.html.Meds.index.render(""));
        return ok(dispenserInformation);
    }
}
