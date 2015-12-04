package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

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

    public static Finder<Long,Containers> find=new Finder<Long, Containers>(Containers.class);
}
