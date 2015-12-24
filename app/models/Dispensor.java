package models;

import com.avaje.ebean.Model;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.Date;
import java.util.List;

/**
 * Created by rebeca on 12/3/2015.
 */
/*
Dispensor class is a class that saves information for each dispenser.
The class name is a typo but it was done quite early and correcting this causes many errors.
    Long id:                     The dispenser's internal id as known by the database
    Long dispenser:              The dispenser's id as known by the Raspberry Pi
    Users owner:                 The owner of the device. Each dispenser can only belong to one user
                                    and each user has only one device.
    List<Containers> containers: A list of containers stored for the device
    DateTime startTime:          The start time of operation (when dispenser begins working each day)
    DateTime endTime:            The end time of operation (when dispenser stops working each day)
    Finder:                      Function to find dispenser using database internal dispenser id
 */
@Entity
public class Dispensor extends Model{
    @Id
    public Long id;

    public Long dispenser;

    public static Finder<Long, Dispensor> find=new Finder<Long, Dispensor>(Dispensor.class);
    /*
    Function to create a dispenser
    The input parameters are the user who this dispenser belongs to, the operation time (startTime & endTime)
        and the dispenser id the Raspberry Pi recognizes the dispenser by.
        If the user doesn't exist, a dispenser cannot be created so we return null.
        Else, we create a dispenser and set the owner, operation time, the id of dispenser as recognized by the
            Raspberry Pi. We save the dispenser's data and return the dispenser.
    We return dispenser created.
     */
    public static Dispensor createNewDispensor(Users user, DateTime operationStartTime, DateTime operationEndTime, Long dispenser) {
        if (user == null) {
            return null;
        }
        Dispensor device = new Dispensor();
        device.owner = user;
        device.startTime = operationStartTime;
        device.endTime = operationEndTime;
        device.dispenser = dispenser;
        device.save();
        return device;
    }

    @OneToOne
    public Users owner;

    @OneToMany(mappedBy = "device")
    public List<Containers> containers;

    public DateTime startTime;

    public DateTime endTime;

}
