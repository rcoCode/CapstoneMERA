package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by rebeca on 12/3/2015.
 */
/*
Containers class stores the information for each container belonging to a device.
    Long id:            The id of the container saved internally
    Dispensor device:   The device the container belongs to
    Boolean empty:      Whether the container is empty or not (available for adding medication)
    Meds med:           The medication stored in the container
    Users owner:        The owner of the container & device
    Long pillCount:     The number of pills initially loaded to the container
    Long container:     The id of the container as known by the raspberry pi
    List<Log> myLog:    List of logs for each container: this contains errors, warnings and successes
    Finder:             A finder function to find container using id (internal database id)
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

    @OneToMany(mappedBy = "regards")
    public List<Log> myLog;

    public static Finder<Long,Containers> find=new Finder<Long, Containers>(Containers.class);
    /*
    This function is used to stored the medication for a container that already exists.
        Function is mainly used to populate empty containers.
    The input parameters are a medication, the number of pills loaded to the container and the id of the container.
        We find the container since it already exists.
        We save to the container: medication, set empty to false since a medication has been stored and the pill count.
        We also save the container or the changes won't be set.
     */
    public static void containersMedication ( Meds medication, Long pillCount, Long containerID) {
        Containers container = Containers.find.byId(containerID);
        container.medication = medication;
        container.empty = false;
        container.pillCount = pillCount;
        container.save();
    }
    /*
    This function is used to create an empty container.
    The input parameters are the device and the container id.
        We create a new container and set the device, container id and empty to true since
         we are only creating a container that we will later populate with a medication.
    We return the container created.
     */
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
    /*
   This function is used to empty the content of an existing container.
     There are cases when a container will become available so we must empty its content.
   The input parameters are the container, the device the container belongs to.
       We set the container to empty, save the container's device and owner.
           The reason we must save the container's device and user even if it's there is because
           the container doesn't automatically link to the owner and device, there were errors caused by this.
   We save the container upon applying these changes.
    */
    public static void emptyContainer (Containers container, Dispensor device) {
        container.empty = true;
/*        Meds gone = container.medication;
        if(gone != null) {
            container.medication = null;
            gone.delete();
        }
        */
        container.device = device;
        container.owner = device.owner;
        container.pillCount = (long) 0;
        container.save();
    }
}
