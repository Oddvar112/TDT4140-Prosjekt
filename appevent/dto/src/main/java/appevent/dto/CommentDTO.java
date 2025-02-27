package appevent.dto;

import java.util.UUID;

public record CommentDTO(
    UUID id,
    UUID userId,
    String username,
    String content
) {}