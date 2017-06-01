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
/*
Class to save users information
    Long id:                     The id used by the database to identify user
    String username:             The username for each user and this must be unique
    String password_hash:        The password for the user account
    Boolean authenticate:        A boolean to check password
    String Fname:                The first name of the user
    String Lname:                The last name of the user
    Dispensor device:            The device related to the user, there is only one device per user account
    List<Contact> contacts:      A list of contacts for the user
    List<Containers> myMeds:     A list of containers for the user
    List<Log> myLogs:            A list of logs associated with the user
    Finder:                      Function to find user by internal database id
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

    public static Model.Finder<Long, Users> find=new Model.Finder<Long, Users>(Users.class);


    public boolean authenticate(String password) {
        return BCrypt.checkpw(password, this.password_hash);
    }
    /*
    Function to create new user.
    The input parameters are username, password, first and last name of the user
        We check to see if the username and password are empty and
            if the length of the password is less than 8 characters
        The password is hashed using BCrypt
        We check if the username is not yet taken and if it is not then we create a user.
        Then, we save the parameters.
     We return the user created.
     This code is based on the code written by Professor Molina in CUNY Tech Prep
     */
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




