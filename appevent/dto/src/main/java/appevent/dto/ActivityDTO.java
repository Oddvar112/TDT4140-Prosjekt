package appevent.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Data Transfer Object for activities.
 *
 * @param id the unique identifier of the activity
 * @param title the title of the activity
 * @param dateTime the date and time of the activity
 * @param location the location of the activity
 * @param description the description of the activity
 * @param isPrivate whether the activity is private
 * @param ownerId the unique identifier of the owner of the activity
 * @param ownerUsername the username of the owner of the activity
 * @param participants the set of participants in the activity
 * @param comments the list of comments on the activity
 */
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
