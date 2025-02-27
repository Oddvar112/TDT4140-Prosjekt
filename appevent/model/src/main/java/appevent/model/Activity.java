package appevent.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "activity")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Column(nullable = false)
    private String location;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private boolean isPrivate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "activity_participants",
        joinColumns = @JoinColumn(name = "activity_id"),
        inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    private Set<User> participants = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "activity_invitees",
        joinColumns = @JoinColumn(name = "activity_id"),
        inverseJoinColumns = @JoinColumn(name = "invitee_id")
    )
    private Set<User> invitedUsers = new HashSet<>();

    @OneToMany(mappedBy = "activity", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();

    protected Activity() {}

    public Activity(String title, LocalDateTime dateTime, String location, String description, User owner, boolean isPrivate) {
        this.title = title;
        this.dateTime = dateTime;
        this.location = location;
        this.description = description;
        this.owner = owner;
        this.isPrivate = isPrivate;
        this.participants.add(owner);
    }

    public void addParticipant(User user) {
        if (user != null) {
            participants.add(user);
            invitedUsers.remove(user);
        }
    }

    public void removeParticipant(User user) {
        if (user != null && !user.equals(owner)) {
            participants.remove(user);
        }
    }

    public void addInvitation(User invitee) {
        if (invitee != null && !participants.contains(invitee)) {
            invitedUsers.add(invitee);
        }
    }

    public void removeInvitation(User invitee) {
        if (invitee != null) {
            invitedUsers.remove(invitee);
        }
    }

    public void addComment(Comment comment) {
        if (comment != null) {
            comments.add(comment);
            comment.setActivity(this);
        }
    }

    public void removeComment(Comment comment) {
        if (comment != null) {
            comments.remove(comment);
            comment.setActivity(null);
        }
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Set<User> getParticipants() {
        return participants;
    }

    public Set<User> getInvitedUsers() {
        return invitedUsers;
    }

    public Set<Comment> getComments() {
        return comments;
    }
}