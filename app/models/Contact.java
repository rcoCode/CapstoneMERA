package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.List;

/**
 * Created by rebeca on 12/2/2015.
 */
/*
Contact class stores information for each contact of the device user's.
    Long id:              There is an internal id for the database.
    String fName:         The first name of the contact.
    String lName:         The last name of the contact.
    String email:         The contact's email address. This must be a unique email.
    String phone:         The contact's phone number was supposed to be stored for
                             future implementation of sending text messages upon
                             errors and warnings.
    Many to Many          Each user might have many contacts and each contact might
    Relationship:            be a contact for multiple users.
    List<Users> caredFor: The list of users a contact is a caretaker (contact) for.
    Finder:               This a function to search for contacts using the contact's id
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
    @Column(unique = true)
    public String email;

    public String phone;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    public List<Users> caredFor;

    public static Finder<Long,Contact> find = new Finder<Long, Contact>(Contact.class);

}
