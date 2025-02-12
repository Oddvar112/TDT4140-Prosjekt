package appevent.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entity representing an Admin-user.
 */
@Entity
@Table(name = "adminuser")
public class Admin extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(unique = true, nullable = false, length = 50)
    private String brukernavn;

    @Column(nullable = false)
    private String passord;

    /**
     * Default constructor for JPA.
     */
    protected Admin() { }

    /**
     * Constructs a new Admin with the specified username and password.
     *
     * @param brukernavn the username of the admin
     * @param passord the password of the admin
     */
    public Admin(final String brukernavn, final String passord) {
        super(brukernavn, passord);
    }
}
