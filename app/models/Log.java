package models;

import com.avaje.ebean.Model;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created by rebeca on 12/19/2015.
 */
public class Log extends Model{
    @Id
    public Long id;

    public DateTime scheduleTime;

    public String message;

    @ManyToOne
    public Containers container;

    @ManyToOne
    public Users owner;

    public static Finder<Long,Log> find= new Finder<Long, Log>(Log.class);

    public String niceDate(Long id){
        Log present = Log.find.byId(id);
        String time = present.scheduleTime.toString("hh:mm aa MM/dd/yyyy");
        return time;
    }
}
