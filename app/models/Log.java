package models;

import com.avaje.ebean.Model;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created by rebeca on 12/19/2015.
 */
/*
Log class saves the information for each log created.
    Long id:                The internal id of the log.
    DateTime scheduledTime: The dispenser the log belongs to.
    DateTime loggedTim      The time the status was logged
    String statusType:      The status type: success, error, warning
    String message:         The message according to status type
                                As an example for success status type: "Medication successfully dispensed"
    Containers regards:     The container a log corresponds to
    Users own:              The user the log is about
    Finder:                 Function to find log by id
 */
@Entity
public class Log extends Model{
    @Id
    public Long id;

    public DateTime scheduleTime;

    public DateTime loggedTime;

    public String statusType;

    public String message;

    @ManyToOne
    public Containers regards;

    @ManyToOne
    public Users own;

    public static Finder<Long,Log> find= new Finder<Long, Log>(Log.class);
    /*
    Function to display schedule time nicely
    The input parameter is the id of the log
        We find the log by id and format the scheduled time as a string
    We return the time as a string
     */
    public String niceDate(Long id){
        Log present = Log.find.byId(id);
        String time = present.scheduleTime.toString("hh:mm aa MM/dd/yyyy");
        return time;
    }
    /*
    Function to create log
    The input parameters are scheduled time, the time stamp (time event was logged), the message,
    the container log corresponds to, the user log is about and the status type.
        We create a new log and save the parameters before saving the log.
    We return the log created.
     */

    public static Log createNewLog(DateTime scheduleTime, DateTime timeStamp, String message, Containers container, Users user, String statusType) {
        Log log = new Log();
        log.message = message;
        log.own = user;
        log.scheduleTime = scheduleTime;
        log.loggedTime = timeStamp;
        log.regards = container;
        log.regards.medication = container.medication;

        System.out.println(log.regards);
        System.out.println(log.regards.medication.name);

        log.statusType = statusType;
        log.save();
        return log;
    }
}