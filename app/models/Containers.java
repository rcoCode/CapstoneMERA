package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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

    @OneToOne
    public Meds medication;

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
        container.empty=true;
        container.save();
        return container;
    }
}
