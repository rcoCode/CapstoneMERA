package models;

import com.avaje.ebean.Model;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints;


import javax.persistence.*;
import javax.validation.Constraint;
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

        Users User = new Users();
        User.username = username;
        User.password_hash = passwordHash;
        User.Fname= fName;
        User.Lname= lName;

        return User;
    }

    public  String Fname;

    public String Lname;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    public List<Contact> contacts;

    @OneToOne
    public Dispensor device;

}
