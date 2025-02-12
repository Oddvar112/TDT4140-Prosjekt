package appevent.server;

import java.util.UUID;

import appevent.model.ActivityRepository;
import appevent.model.UserRepository;

public class AdminService {

    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    /**
     * Constructs an AdminService with the specified repositories.
     * @param activityRepository the activity repository
     * @param userRepository the user repository
     */
    public AdminService(final ActivityRepository activityRepository, final UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
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
}
