package appevent.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record ActivityDTO(
    UUID id,
    String title,
    LocalDateTime dateTime,
    String location,
    String description,
    boolean isPrivate,
    UUID ownerId,
    String ownerUsername,
    Set<UserDTO> participants,
    List<CommentDTO> comments
) { }