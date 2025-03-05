package appevent.dto;

import java.time.LocalDateTime;

/**
 * Record representing search data transfer object (DTO).
 *
 * @param date the date of the search
 * @param type the type of the search
 * @param searchString the search string
 */
public record SearchDTO(
    LocalDateTime date,
    String type,
    String searchString
) { }
