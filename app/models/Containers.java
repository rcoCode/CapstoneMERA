package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by rebeca on 12/3/2015.
 */
@Entity
public class Containers extends Model{
    @Id
    public Long id;

    @ManyToOne
    public Dispensor device;

    public Boolean empty;

    @OneToOne(mappedBy = "storedIn")
    public Meds medication;

    @ManyToOne
    public Users owner;

    public Long pillCount;


    public static Finder<Long,Containers> find=new Finder<Long, Containers>(Containers.class);

    public static void containersMedication ( Meds medication, Long pillCount, Long containerID) {
        Containers container = Containers.find.byId(containerID);
        container.medication = medication;
        container.empty = false;
        container.pillCount = pillCount;
        container.save();
    }

    public static Containers createContainer(Dispensor device) {
        Containers container = new Containers();
        container.device = device;
        container.owner = device.owner;
        container.empty=true;
        return container;
    }
}
