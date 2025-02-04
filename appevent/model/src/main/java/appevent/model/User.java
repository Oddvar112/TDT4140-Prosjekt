package appevent.model;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * Entity class representing a user in the system.
 * Contains basic user authentication information.
 */
@Entity
@Table(name = "AppUser")
public class User {
   
   @Id 
   @GeneratedValue(strategy = GenerationType.UUID)
   private UUID id;
   
   @Column(unique = true, nullable = false, length = 50)
   private String brukernavn;
   
   @Column(nullable = false)
   private String passord;

   /**
    * Default constructor for JPA.
    */
   protected User() {}

   /**
    * Constructs a new User with the specified username and password.
    *
    * @param brukernavn the username of the user
    * @param passord the password of the user
    */
   public User(String brukernavn, String passord) {
       this.brukernavn = brukernavn;
       this.passord = passord;
   }

   /**
    * Returns the unique identifier of the user.
    *
    * @return the UUID of the user
    */
   public UUID getId() {
       return id;
   }

   /**
    * Returns the username of the user.
    *
    * @return the username of the user
    */
   public String getBrukernavn() {
       return brukernavn;
   }

   /**
    * Returns the password of the user.
    *
    * @return the password of the user
    */
   public String getPassord() {
       return passord;
   }
}