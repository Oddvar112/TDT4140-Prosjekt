package appevent.dto;

import java.util.UUID;

/**
 * Data Transfer Object for comments.
 *
 * @param id the unique identifier of the comment
 * @param userId the unique identifier of the user who made the comment
 * 
 * @param username the username of the user who made the comment
 * @param content the content of the comment
 */
public record CommentDTO(
    UUID id,
    UUID userId,
    String username,
    String content
) { }
