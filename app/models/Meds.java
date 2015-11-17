package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by rebec on 11/17/2015.
 */
@Table(name="Meds")
@Entity
public class Meds {
    @Id
    public Long id;

    public String name;

    public Long perWk;

    public Long perMnth;

    public Long perDay;

    public static Model.Finder<Long, Meds> find = new Model.Finder<Long, Meds>(Meds.class);

    public static Meds createNewMed(String medName,Long week,Long month,Long day) {
        if(medName == null || (week == null && month == null && day == null)) {
            return null;
        }

        Meds med = new Meds();
        med.name =medName;
        med.perWk =week;
        med.perMnth =month;
        med.perDay =day;

        return med;
    }
}
