package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by rebeca on 11/17/2015.
 */
@Entity
public class Meds extends Model{
    @Id
    public Long id;

    public String name;

    public static Finder<Long, Meds> find=new Finder<Long, Meds>(Meds.class);

    public Long dose;

    public Long perWk;

    public Long perMnth;

    public Long perDay;

    public static Meds createNewMed(String medName,Long dosage,Long week,Long month,Long day) {
        if(medName == null || dosage==null || (week == null && month == null && day == null)) {
            return null;
        }

        Meds med = new Meds();
        med.name =medName;
        med.dose =dosage;
        med.perWk =week;
        med.perMnth =month;
        med.perDay =day;

        med.save();

        return med;
    }
}
