package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;
import play.libs.Time;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
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

    public Date schedule;

    public Date dailyTime;

    public Long perWk;

    public Long perMnth;

    public Long perDay;


    @OneToOne
    public Containers storedIn;

    public static Meds createNewMed(String medName,Long dosage,Date sched, Date daily, Long week,Long month,Long day, Long pillCount, Dispensor device) {
        if(medName == null || dosage==null || (week == null && month == null && day == null)) {
            return null;
        }
        System.out.print("Creating Medication");

        Meds med = new Meds();
        med.name =medName;
        med.dose =dosage;
        med.schedule=sched;
        med.perWk =week;
        med.perMnth =month;
        med.perDay =day;
        med.dailyTime = daily;
        med.save();
        List<Containers> containers = Containers.find.where().eq("empty", true).eq("device",device).findList();
        Containers container = containers.get(0);
        container.containersMedication(med,pillCount,container.id);
        med.storedIn = container;
        med.save();
        return med;
    }

}
