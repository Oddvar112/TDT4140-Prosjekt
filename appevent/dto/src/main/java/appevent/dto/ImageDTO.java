package appevent.dto;

import java.util.UUID;

/**
 * Data Transfer Object for activity images.
 *
 * @param id the unique identifier of the image
 * @param activityId the unique identifier of the activity this image belongs to
 * @param uploadedById the unique identifier of the user who uploaded this image
 * @param uploaderUsername the username of the user who uploaded this image
 * @param fileName the original file name of the image
 */
public record ImageDTO(
    UUID id,
    UUID activityId,
    UUID uploadedById,
    String uploaderUsername,
    String fileName
) { }
