package appevent.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Entity representing an activity.
 */
@Entity
@Table(name = "activity")
public final class Activity {
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
    private String type;

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
    private Set<User> participants = new HashSet<User>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "activity_invitees",
        joinColumns = @JoinColumn(name = "activity_id"),
        inverseJoinColumns = @JoinColumn(name = "invitee_id")
    )
    private Set<User> invitedUsers = new HashSet<User>();

    @OneToMany(mappedBy = "activity", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<Comment>();

    protected Activity() { }

    /**
     * Constructs an Activity with the specified details.
     *
     * @param title the title of the activity
     * @param dateTime the date and time of the activity
     * @param location the location of the activity
     * @param description the description of the activity
     * @param type the type of the activity
     * @param owner the user who owns this activity
     * @param isPrivate whether this activity is private
     */
    public Activity(final String title, final LocalDateTime dateTime, final String location,
                    final String description, final String type, final User owner, final boolean isPrivate) {
        this.title = title;
        this.dateTime = dateTime;
        this.location = location;
        this.description = description;
        this.type = type;
        this.owner = owner;
        this.isPrivate = isPrivate;
        this.participants.add(owner);
    }

    /**
     * Adds a participant to the activity.
     * If the user is already invited, removes them from invitees.
     *
     * @param user the user to add as a participant
     */
    public void addParticipant(final User user) {
        if (user != null) {
            participants.add(user);
            invitedUsers.remove(user);
        }
    }

    /**
     * Removes a participant from the activity.
     * The owner cannot be removed as a participant.
     *
     * @param user the user to remove
     */
    public void removeParticipant(final User user) {
        if (user != null && !user.equals(owner)) {
            participants.remove(user);
        }
    }

    /**
     * Adds an invitation to a user for this activity.
     * Does not invite users who are already participants.
     *
     * @param invitee the user to invite
     */
    public void addInvitation(final User invitee) {
        if (invitee != null && !participants.contains(invitee)) {
            invitedUsers.add(invitee);
        }
    }

    /**
     * Removes an invitation to a user for this activity.
     *
     * @param invitee the user whose invitation to remove
     */
    public void removeInvitation(final User invitee) {
        if (invitee != null) {
            invitedUsers.remove(invitee);
        }
    }

    /**
     * Adds a comment to this activity.
     * Also sets the bidirectional relationship.
     *
     * @param comment the comment to add
     */
    public void addComment(final Comment comment) {
        if (comment != null) {
            comments.add(comment);
            comment.setActivity(this);
        }
    }

    /**
     * Removes a comment from this activity.
     * Also clears the bidirectional relationship.
     *
     * @param comment the comment to remove
     */
    public void removeComment(final Comment comment) {
        if (comment != null) {
            comments.remove(comment);
            comment.setActivity(null);
        }
    }

    /**
     * Gets the ID of the activity.
     *
     * @return the activity ID
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the title of the activity.
     *
     * @return the activity title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the activity.
     *
     * @param title the new title
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Gets the date and time of the activity.
     *
     * @return the activity date and time
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * Sets the date and time of the activity.
     *
     * @param dateTime the new date and time
     */
    public void setDateTime(final LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * Gets the location of the activity.
     *
     * @return the activity location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location of the activity.
     *
     * @param location the new location
     */
    public void setLocation(final String location) {
        this.location = location;
    }

    /**
     * Gets the description of the activity.
     *
     * @return the activity description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the activity.
     *
     * @param description the new description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Checks if the activity is private.
     *
     * @return true if the activity is private, false otherwise
     */
    public boolean isPrivate() {
        return isPrivate;
    }

    /**
     * Sets whether the activity is private.
     *
     * @param isPrivate true to make the activity private, false otherwise
     */
    public void setPrivate(final boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    /**
     * Gets the owner of the activity.
     *
     * @return the user who owns this activity
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Sets the owner of the activity.
     *
     * @param owner the new owner
     */
    public void setOwner(final User owner) {
        this.owner = owner;
    }

    /**
     * Gets the participants of the activity.
     *
     * @return the set of participants
     */
    public Set<User> getParticipants() {
        return participants;
    }

    /**
     * Gets the type of the activity.
     *
     * @return the activity type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the activity.
     *
     * @param type the new type
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * Gets the invited users for this activity.
     *
     * @return the set of invited users
     */
    public Set<User> getInvitedUsers() {
        return invitedUsers;
    }

    /**
     * Gets the comments for this activity.
     *
     * @return the set of comments
     */
    public Set<Comment> getComments() {
        return comments;
    }
}
