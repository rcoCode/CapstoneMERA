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

    public Date startTime;

    public Date endTime;

    @OneToOne
    public Users owner;

    @OneToMany
    public List<Containers> containers;
}
