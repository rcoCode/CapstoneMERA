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

import java.text.SimpleDateFormat;
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
                SimpleDateFormat time = new SimpleDateFormat("hh:mm aa");
                dispenserInformation.put("Operation Start Time",time.format(device.startTime));
                dispenserInformation.put("Operation End Time",time.format(device.endTime));
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
                                medicine.put("Name",container.medication.name);
                                medicine.put("Dose", container.medication.dose);
                                medicine.put("Frequency (Changes Needed)", container.medication.perDay);
                                containerInformation.put("Medication",medicine);
                            }
                            containerContent.add(containerInformation);
                        }
                    }
                }
            }
        }
        dispenserInformation.put("Containers",containerContent);

        return ok(dispenserInformation);
    }
}
