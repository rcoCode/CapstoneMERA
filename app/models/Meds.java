package models;

import com.avaje.ebean.Model;
import org.joda.time.DateTime;
import play.data.validation.Constraints;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rebeca on 11/17/2015.
 */
/*
Meds class is a class that saves information for each medication.
    Long id:                The id of the medication as stored by the database
    String name:            The name of the medication. This is a constraint, a medication name must be given
    Long dose:              The dose of the medication. This is also constraint, a dose to medication must be given
    DateTime schedule:      The first day medication will be dispensed
    DateTime dailyTime:     The first time medication will de dispensed daily
    Long frequency:         The frequency of doses in hours
    Boolean updated:        Whether the medication information has been updated
    Containers storedIn:    The container a medication is stored in
    ArrayList<String> days: The days which a medication is dispensed if it's not a medication taken daily.
                                This is feature that which was not yet implemented by the Raspberry Pi but the website
                                does save and store this information.
    Finder:                 Function to find Medication by id
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

    public Long frequency;

    public Boolean updated;

    public ArrayList<String> days = new ArrayList<>();

    @OneToOne
    public Containers storedIn;
    /*
    This function is used to know how many days a medication is taken.
        The function take the parameter of medication id.
    We return the number of days medication is taken.
     */
    public Integer daySize(Long m_id){
        Meds med = Meds.find.byId(m_id);
        Integer required = med.days.size();
        return required;
    }
    /*
   This function creates a new medication.
   The input parameters are the medication name, the dose, the scheduled day of first dispensing,
       the time of the first daily dispensing, a list of days the medication is take, the frequency in hours medication
       is taken and the container in which medication is stored.
       We create a new medication and save the input parameters and save the new medication.
       We also set updated to true since medication information has changed.
   We return the medication created.
    */
    public static Meds createNewMed(String medName,Long dosage,DateTime sched, DateTime daily,ArrayList<String> week,Long freq,Containers stored) {
        if(medName == null || dosage==null) {
            return null;
        }
        System.out.print("Creating Medication");

        Meds med = new Meds();
        med.name =medName;
        med.dose =dosage;
        med.frequency =freq;
        med.schedule=sched;
        med.dailyTime = daily;
        med.days = week;
        med.storedIn = stored;
        med.updated = true;
        return med;
    }
    /*
    This function is used to display daily time in a nice format rather than the internal manner in which java saves time.
    The input parameter is the medication id.
          We find medication by id and save time as a string formatted as example: 09:00 AM
    We return a string of time of daily medication
     */
    public String niceDate(Long m_id){
        Meds med = Meds.find.byId(m_id);
        String time = med.dailyTime.toString("hh:mm aa");
        return time;
    }

}
