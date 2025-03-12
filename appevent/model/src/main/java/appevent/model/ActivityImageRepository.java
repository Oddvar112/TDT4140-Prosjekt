package appevent.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing ActivityImage entities.
 */
@Repository
public interface ActivityImageRepository extends JpaRepository<ActivityImage, UUID> {

    /**
     * Finds all images for a specific activity.
     *
     * @param activityId the ID of the activity
     * @return list of images for the activity
     */
    List<ActivityImage> findByActivityId(UUID activityId);

    /**
     * Checks if an image with the specified ID exists and belongs to the specified activity.
     *
     * @param id the ID of the image
     * @param activityId the ID of the activity
     * @return true if such an image exists, false otherwise
     */
    boolean existsByIdAndActivityId(UUID id, UUID activityId);
}
