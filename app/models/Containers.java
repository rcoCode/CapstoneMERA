package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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

    @Constraints.Required
    @Column(unique = true)
    public Long container;

    @OneToMany(mappedBy = "container")
    public List<Log> myLog;

    public static Finder<Long,Containers> find=new Finder<Long, Containers>(Containers.class);

    public static void containersMedication ( Meds medication, Long pillCount, Long containerID) {
        Containers container = Containers.find.byId(containerID);
        container.medication = medication;
        container.empty = false;
        container.pillCount = pillCount;
        container.save();
    }

    public static Containers createContainer(Dispensor device, Long storedIn) {
//        System.out.print("Creating Container\n");
        Containers container = new Containers();
        container.device = device;
        container.owner = device.owner;
        container.empty=true;
        container.container = storedIn;
//        System.out.print(container.container);
        container.save();
        return container;
    }

    public static void emptyContainer (Dispensor device, Long storedIn) {
        Containers container = Containers.find.where().eq("device",device).eq("container",storedIn).findUnique();
        System.out.print("Emptying Container: " + container.container + "\n");
        System.out.print(container.empty + "\n");
        container.empty = true;
        //Remove Medication Function Needs to be CALLED
//        container.medication = null;
        container.pillCount = (long) 0;
        container.save();
    }

    public static void showAll(Dispensor device) {
        List<Containers> containers = Containers.find.where().eq("device",device).findList();
        for (int i=0; i<containers.size();i++) {
            System.out.print("\n"+i+" "+containers.get(i).container+"\n");
            System.out.print(containers.get(i).empty+"\n");
            System.out.print(containers.get(i).id+"\n\n");
        }

    }
}
