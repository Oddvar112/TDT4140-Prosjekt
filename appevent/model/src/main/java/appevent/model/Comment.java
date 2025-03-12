package appevent.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entity representing a comment on an activity.
 */
@Entity
@Table(name = "activity_comments")
public final class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 1000, nullable = false)
    private String content;

    protected Comment() { }

    /**
     * Constructs a new Comment with the specified activity, user, and content.
     *
     * @param activity the activity this comment belongs to
     * @param user the user who created this comment
     * @param content the text content of the comment
     */
    public Comment(final Activity activity, final User user, final String content) {
        this.activity = activity;
        this.user = user;
        this.content = content;
    }

    /**
     * Gets the unique identifier of this comment.
     *
     * @return the UUID of the comment
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the activity this comment belongs to.
     *
     * @return the activity
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     * Sets the activity this comment belongs to.
     * Used for maintaining bidirectional relationships.
     *
     * @param activity the activity to set
     */
    public void setActivity(final Activity activity) {
        this.activity = activity;
    }

    /**
     * Gets the user who created this comment.
     *
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user who created this comment.
     *
     * @param user the user to set
     */
    public void setUser(final User user) {
        this.user = user;
    }

    /**
     * Gets the text content of this comment.
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the text content of this comment.
     *
     * @param content the content to set
     */
    public void setContent(final String content) {
        this.content = content;
    }
}
