package appevent.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Data Transfer Object for Activity.
 *
 * @param id the unique identifier of the activity
 * @param title the title of the activity
 * @param dateTime the date and time of the activity
 * @param location the location of the activity
 * @param description the description of the activity
 * @param participants the set of participants in the activity
 */
public record ActivityDTO(
    UUID id,
    String title,
    LocalDateTime dateTime,
    String location,
    String description,
    Set<UserDTO> participants
) { }

