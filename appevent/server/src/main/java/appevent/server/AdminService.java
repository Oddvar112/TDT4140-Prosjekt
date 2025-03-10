package appevent.server;
import java.util.UUID;
import org.springframework.stereotype.Service;

import appevent.model.ActivityImage;
import appevent.model.ActivityImageRepository;
import appevent.model.ActivityRepository;
import appevent.model.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class AdminService {

    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final ActivityImageRepository imageRepository;
    /**
     * Constructs an AdminService with the specified repositories.
     * @param activityRepository the activity repository
     * @param userRepository the user repository
     * @param imageRepository the image repository
     */
    public AdminService(final ActivityRepository activityRepository, final UserRepository userRepository, final ActivityImageRepository imageRepository) {
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
    }

    /**
     * Deletes an activity.
     *
     * @param activityId the activity ID
     */
    public void deleteActivity(final UUID activityId) {
        activityRepository.deleteById(activityId);
    }

    /**
     * Deletes a user.
     *
     * @param userId the user ID
     */
    public void deleteUser(final UUID userId) {
        userRepository.deleteById(userId);
    }

    /**
     * Deletes an image by its ID.
     * @param imageId the ID of the image to delete
     * @param userId the ID of the user attempting to delete the image
     * @throws IllegalArgumentException if the image is not found, or if the user
     *         is not authorized to delete the image
     */
    @Transactional
    public void deleteImage(final UUID imageId, final UUID userId) {
        ActivityImage image = imageRepository.findById(imageId)
            .orElseThrow(() -> new IllegalArgumentException("Bilde ikke funnet"));
        imageRepository.delete(image);
    }

}
