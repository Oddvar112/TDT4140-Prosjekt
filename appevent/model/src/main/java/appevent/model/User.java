package appevent.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

/**
 * Entity representing a user in the system.
 */
@Entity
@Table(name = "appuser")
public final class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(unique = true, nullable = false, length = 50)
    private String brukernavn;

    @Column(nullable = false)
    private String passord;

    @ManyToMany(mappedBy = "participants")
    private Set<Activity> activities = new HashSet<Activity>();

    @ManyToMany
    @JoinTable(
        name = "user_friends",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<User> friends = new HashSet<User>();

    protected User() { }

    /**
     * Constructs a new User with the specified username and password.
     *
     * @param brukernavn the username of the user
     * @param passord the password of the user
     */
    public User(final String brukernavn, final String passord) {
        this.brukernavn = brukernavn;
        this.passord = passord;
    }

    /**
     * Gets the unique identifier of the user.
     *
     * @return the UUID of the user
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the username of the user.
     *
     * @return the username of the user
     */
    public String getBrukernavn() {
        return brukernavn;
    }

    /**
     * Gets the password of the user.
     *
     * @return the password of the user
     */
    public String getPassord() {
        return passord;
    }

    /**
     * Gets the friends of this user.
     *
     * @return the set of friends
     */
    public Set<User> getFriends() {
        return friends;
    }

    /**
     * Adds a friend to this user and establishes the bidirectional relationship.
     *
     * @param friend the user to add as a friend
     */
    public void addFriend(final User friend) {
        friends.add(friend);
        friend.getFriends().add(this);
    }

    /**
     * Removes a friend from this user and breaks the bidirectional relationship.
     *
     * @param friend the user to remove from friends
     */
    public void removeFriend(final User friend) {
        friends.remove(friend);
        friend.getFriends().remove(this);
    }
}
