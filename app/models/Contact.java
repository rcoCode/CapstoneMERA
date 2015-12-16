package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.List;

/**
 * Created by rebeca on 12/2/2015.
 */
@Entity
public class Contact extends Model{
    @Id
    public Long id;

    @Constraints.Required
    public String fName;

    @Constraints.Required
    public String lName;

    @Constraints.Required
    public String email;

    public String phone;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    public List<Users> caredFor;

}
