package appevent.dto;

import java.util.UUID;

/**
 * Data Transfer Object for image upload requests.
 *
 * @param activityId the unique identifier of the activity
 * @param fileName the original file name of the image
 * @param base64ImageData the Base64-encoded image data
 */
public record ImageUploadDTO(
    UUID activityId,
    String fileName,
    String base64ImageData
) { }
