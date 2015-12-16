package models;

import com.avaje.ebean.Model;

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


    public static Finder<Long, Dispensor> find=new Finder<Long, Dispensor>(Dispensor.class);

    public static Dispensor createNewDispensor(Users user, Date operationStartTime, Date operationEndTime) {

        if (user == null) {
            return null;
        }
        Dispensor device = new Dispensor();
        device.owner = user;
        device.startTime = operationStartTime;
        device.endTime = operationEndTime;
        device.save();

        Containers container1 = Containers.createContainer(device);
        Containers container2 = Containers.createContainer(device);
        Containers container3 = Containers.createContainer(device);

        return device;
    }

    @OneToOne
    public Users owner;

    @OneToMany
    public List<Containers> containers;

    public Date startTime;

    public Date endTime;

}
