package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by rebeca on 11/17/2015.
 */
@Table(name="Medication")
@Entity
public class Medication extends Model {
    @Id
    public Long id;

    public String name;

    public Long perWk;

    public Long perMnth;

    public Long perDay;

    public static Medication createNewMed(String medName,Long week,Long month,Long day) {
        if(medName == null || (week == null && month == null && day == null)) {
            return null;
        }

        Medication med = new Medication();
        med.name =medName;
        med.perWk =week;
        med.perMnth =month;
        med.perDay =day;

        return med;
    }
}
