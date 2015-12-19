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
@Entity
public class Dispensor extends Model{
    @Id
    public Long id;

    public Long dispenser;

    public static Finder<Long, Dispensor> find=new Finder<Long, Dispensor>(Dispensor.class);

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
