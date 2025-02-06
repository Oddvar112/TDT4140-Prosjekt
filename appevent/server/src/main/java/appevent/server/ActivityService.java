package appevent.server;

import appevent.dto.ActivityDTO;
import appevent.dto.UserDTO;
import appevent.model.Activity;
import appevent.model.ActivityRepository;
import appevent.model.User;
import appevent.model.UserRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for managing activities.
 */
@Service
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    /**
     * Constructs an ActivityService with the specified repositories.
     *
     * @param activityRepository the activity repository
     * @param userRepository the user repository
     */
    public ActivityService(final ActivityRepository activityRepository, final UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves the list of upcoming activities.
     *
     * @return the list of upcoming activities
     */
    public List<ActivityDTO> getUpcomingActivities() {
    return activityRepository.findByDateTimeAfterOrderByDateTimeAsc(LocalDateTime.now())
        .stream()
        .map(this::convertToActivityDTO)
        .collect(Collectors.toList());
    }

    /**
     * Converts an Activity entity to an ActivityDTO.
     *
     * @param activity the activity entity
     * @return the activity DTO
     */
    private ActivityDTO convertToActivityDTO(final Activity activity) {
    Set<UserDTO> participantDTOs = activity.getParticipants().stream()
        .map(user -> new UserDTO(user.getId(), user.getBrukernavn()))
        .collect(Collectors.toSet());
    return new ActivityDTO(activity.getId(), activity.getTitle(), activity.getDateTime(), activity.getLocation(), activity.getDescription(), participantDTOs);
    }


    /**
     * Toggles the participation status for a given activity and user.
     *
     * @param activityId the activity ID
     * @param userId the user ID
     */
    public void toggleParticipation(final UUID activityId, final UUID userId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (activity.getParticipants().contains(user)) {
            activity.removeParticipant(user);
        } else {
            activity.addParticipant(user);
        }

        activityRepository.save(activity);
    }

    /**
     * Adds a new activity.
     *
     * @param activityDTO the activity details
     */
    public void addActivity(final ActivityDTO activityDTO) {
        Activity activity = new Activity(activityDTO.title(), activityDTO.dateTime(), activityDTO.location(), activityDTO.description());
        activityRepository.save(activity);
    }

    /**
    * Retrieves the list of activities a user is participating in.
    *
    * @param userId the user's ID
    * @return the list of activities the user is participating in
    */
    public List<ActivityDTO> getActivitiesByUserId(final UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return activityRepository.findByParticipantsContaining(user)
            .stream()
            .map(this::convertToActivityDTO)
            .collect(Collectors.toList());
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

