package appevent.dto;

import java.util.UUID;

/**
 * Data Transfer Object for friend requests.
 */
public class FriendRequestDTO {
    private final UUID id;
    private final UserDTO sender;

    /**
     * Constructs a new FriendRequestDTO.
     * @param id the unique identifier of the friend request
     * @param sender the sender of the friend request
     */
    public FriendRequestDTO(final UUID id, final UserDTO sender) {
        this.id = id;
        this.sender = sender;
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
    public UserDTO getSender() {
        return sender;
    }
}
