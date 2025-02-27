package appevent.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "appuser")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(unique = true, nullable = false, length = 50)
    private String brukernavn;

    @Column(nullable = false)
    private String passord;

    @ManyToMany(mappedBy = "participants")
    private Set<Activity> activities = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "user_friends",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<User> friends = new HashSet<>();

    protected User() { }

    public User(final String brukernavn, final String passord) {
        this.brukernavn = brukernavn;
        this.passord = passord;
    }

    public UUID getId() {
        return id;
    }

    public String getBrukernavn() {
        return brukernavn;
    }

    public String getPassord() {
        return passord;
    }

    public Set<User> getFriends() {
        return friends;
    }

    public void addFriend(User friend) {
        friends.add(friend);
        friend.getFriends().add(this);
    }

    public void removeFriend(User friend) {
        friends.remove(friend);
        friend.getFriends().remove(this);
    }
}