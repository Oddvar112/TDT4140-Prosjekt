package appevent.dto;

import java.util.UUID;

public class FriendRequestDTO {
    private final UUID id;
    private final UserDTO sender;

    public FriendRequestDTO(UUID id, UserDTO sender) {
        this.id = id;
        this.sender = sender;
    }

    public UUID getId() {
        return id;
    }

    public UserDTO getSender() {
        return sender;
    }
}