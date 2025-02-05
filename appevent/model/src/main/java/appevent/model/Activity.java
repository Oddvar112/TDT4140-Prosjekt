package appevent.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entity representing an activity.
 */
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

    @ManyToMany
    @JoinTable(
    name = "activity_participants",
    joinColumns = @JoinColumn(name = "activity_id"),
    inverseJoinColumns = @JoinColumn(name = "participant_id")  // Endret fra user_id
    )
    private Set<User> participants = new HashSet<>();

    /**
     * Default constructor for JPA.
     */
    protected Activity() { }

    /**
     * Constructs an Activity with the specified details.
     *
     * @param title the title of the activity
     * @param dateTime the date and time of the activity
     * @param location the location of the activity
     * @param description the description of the activity
     */
    public Activity(final String title, final LocalDateTime dateTime, final String location, final String description) {
        this.title = title;
        this.dateTime = dateTime;
        this.location = location;
        this.description = description;
    }

    /**
     * Adds a participant to the activity.
     *
     * @param user the user to add
     */
    public void addParticipant(final User user) {
        participants.add(user);
    }

    /**
     * Removes a participant from the activity.
     *
     * @param user the user to remove
     */
    public void removeParticipant(final User user) {
        participants.remove(user);
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
     * Gets the participants of the activity.
     *
     * @return the set of participants
     */
    public Set<User> getParticipants() {
        return participants;
    }
}
