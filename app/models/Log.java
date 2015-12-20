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

    public String niceDate(Long id){
        Log present = Log.find.byId(id);
        String time = present.scheduleTime.toString("hh:mm aa MM/dd/yyyy");
        return time;
    }

    public static Log createNewLog(DateTime scheduleTime, DateTime timeStamp, String message, Containers container, Users user, String statusType) {
        Log log = new Log();
        log.message = message;
        log.own = user;
        log.scheduleTime = scheduleTime;
        log.loggedTime = timeStamp;
        log.regards = container;
        log.statusType = statusType;
        log.save();
        return log;
    }
}
