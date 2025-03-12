
package appevent.model;

import java.util.UUID;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entity representing a friend request.
 */
@Entity
@Table(name = "friend_request")
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(nullable = false)
    private boolean accepted;

    /**
     * Default constructor for JPA.
     */
    protected FriendRequest() {
        this.accepted = false;
    }

    /**
     * Constructs a new FriendRequest.
     *
     * @param sender the user sending the friend request
     * @param receiver the user receiving the friend request
     */
    public FriendRequest(final User sender, final User receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.accepted = false;
    }

    /**
     * Gets the unique identifier of the friend request.
     *
     * @return the unique identifier of the friend request
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the sender of the friend request.
     *
     * @return the sender of the friend request
     */
    public User getSender() {
        return sender;
    }

    /**
     * Sets the sender of the friend request.
     *
     * @param sender the sender of the friend request
     */
    public void setSender(final User sender) {
        this.sender = sender;
    }

    /**
     * Gets the receiver of the friend request.
     *
     * @return the receiver of the friend request
     */
    public User getReceiver() {
        return receiver;
    }

    /**
     * Sets the receiver of the friend request.
     *
     * @param receiver the receiver of the friend request
     */
    public void setReceiver(final User receiver) {
        this.receiver = receiver;
    }

    /**
     * Checks if the friend request is accepted.
     *
     * @return true if the friend request is accepted, false otherwise
     */
    public boolean isAccepted() {
        return accepted;
    }

    /**
     * Sets the acceptance status of the friend request.
     *
     * @param accepted the acceptance status of the friend request
     */
    public void setAccepted(final boolean accepted) {
        this.accepted = accepted;
    }
}
