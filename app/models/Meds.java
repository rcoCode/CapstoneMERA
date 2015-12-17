package models;

import com.avaje.ebean.Model;
import org.joda.time.DateTime;
import play.data.validation.Constraints;
import play.libs.Time;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by rebeca on 11/17/2015.
 */
@Entity
public class Meds extends Model{
    @Id
    public Long id;

    @Constraints.Required
    public String name;

    public static Finder<Long, Meds> find=new Finder<Long, Meds>(Meds.class);

    @Constraints.Required
    public Long dose;

    public DateTime schedule;

    public DateTime dailyTime;

    public Long perWk;

    public Long perMnth;

    public Long frequency;

    @OneToOne
    public Containers storedIn;

    public static Meds createNewMed(String medName,Long dosage,DateTime sched, DateTime daily,Long freq,Long week,Long month,Containers stored) {
        if(medName == null || dosage==null || (week == null && month == null)) {
            return null;
        }
        System.out.print("Creating Medication");

        Meds med = new Meds();
        med.name =medName;
        med.dose =dosage;
        med.frequency =freq;
        med.schedule=sched;
        med.perWk =week;
        med.perMnth =month;
        med.dailyTime = daily;
        med.storedIn = stored;
        return med;
    }

    public String niceDate(Long m_id){
        Meds med = Meds.find.byId(m_id);
        String time = med.dailyTime.toString("hh:mm aa");
        return time;
    }

}
