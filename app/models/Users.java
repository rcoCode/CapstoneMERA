package models;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Model;
import com.avaje.ebean.config.JsonConfig;
import org.joda.time.DateTime;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints;


import javax.jws.soap.SOAPBinding;
import javax.persistence.*;
import javax.validation.Constraint;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by rebeca on 11/17/2015.
 */
@Table(name="Users")
@Entity
public class Users extends Model{
    @Id
    public Long id;

    @Constraints.Required
    @Column(unique=true)
    public String username;

    public String password_hash;

    public static Finder<Long, Users> find=new Finder<Long, Users>(Users.class);

    public boolean authenticate(String password) {
        return BCrypt.checkpw(password, this.password_hash);
    }

    public static Users createNewUser(String username, String password, String fName, String lName) {
        if(password == null || username == null || password.length() < 8) {
            return null;
        }

        // Create a password hash
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());

        Users User = Users.find.query().where().eq("username", username).findUnique();

        if (User != null){
            return null;
        } else {
            User = new Users();
        }

        User.username = username;
        User.password_hash = passwordHash;
        User.Fname= fName;
        User.Lname= lName;
        User.save();

        //CONTACT FOR TESTING
        Contact contact = new Contact();
        contact.email = "garnelo.anahi@gmail.com";
        contact.fName = "Rebeca";
        contact.lName = "Otero";
        contact.save();
        User.contacts.add(contact);
        User.save();
        return User;
    }

    public  String Fname;

    public String Lname;

    @ManyToMany(mappedBy = "caredFor")
    public List<Contact> contacts;

    @OneToOne(mappedBy = "owner")
    public Dispensor device;

    @OneToMany(mappedBy = "owner")
    public List<Containers> myMeds;

    @OneToMany(mappedBy = "own")
    public List<Log> myLogs;

}
