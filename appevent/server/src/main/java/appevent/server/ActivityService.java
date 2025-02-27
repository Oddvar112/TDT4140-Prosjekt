package appevent.server;

import appevent.dto.ActivityDTO;
import appevent.dto.CommentDTO;
import appevent.dto.UserDTO;
import appevent.model.Activity;
import appevent.model.ActivityRepository;
import appevent.model.Comment;
import appevent.model.User;
import appevent.model.UserRepository;
import jakarta.transaction.Transactional;

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
     * Retrieves the list of upcoming activities visible to the user.
     *
     * @param userId ID of the user requesting activities
     * @return the list of visible upcoming activities
     */
    @Transactional
    public List<ActivityDTO> getUpcomingActivities(final UUID userId) {
        return activityRepository
            .findByDateTimeAfterOrderByDateTimeAsc(LocalDateTime.now())
            .stream()
            .filter(activity -> isActivityVisibleToUser(activity, userId))
            .map(this::convertToActivityDTO)
            .collect(Collectors.toList());
    }

    /**
     * Checks if an activity should be visible to a user.
     *
     * @param activity the activity to check
     * @param userId ID of the user
     * @return true if the activity should be visible
     */
    private boolean isActivityVisibleToUser(final Activity activity, final UUID userId) {
        return !activity.isPrivate()
        ||
        activity.getOwner().getId().equals(userId)
        ||
        activity.getParticipants().stream()
                   .anyMatch(participant -> participant.getId().equals(userId))
                   ||
               activity.getInvitedUsers().stream()
                   .anyMatch(invitee -> invitee.getId().equals(userId));
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

        List<CommentDTO> commentDTOs = activity.getComments().stream()
        .map(comment -> new CommentDTO(
            comment.getId(),
            comment.getUser().getId(),
            comment.getUser().getBrukernavn(),
            comment.getContent()
        ))
        .collect(Collectors.toList());

        return new ActivityDTO(
            activity.getId(),
            activity.getTitle(),
            activity.getDateTime(),
            activity.getLocation(),
            activity.getDescription(),
            activity.isPrivate(),
            activity.getOwner().getId(),
            activity.getOwner().getBrukernavn(),
            participantDTOs,
            commentDTOs
        );
    }

    /**
     * Toggles the participation status for a given activity and user.
     *
     * @param activityId the activity ID
     * @param userId the user ID
     * @throws IllegalArgumentException if the activity is private and user is not invited
     */
    @Transactional
    public void toggleParticipation(final UUID activityId, final UUID userId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (activity.isPrivate()
            &&
            !activity.getOwner().getId().equals(userId)
            &&
            !activity.getParticipants().contains(user)
            &&
            !activity.getInvitedUsers().contains(user)) {
            throw new IllegalArgumentException("Dette er et privat arrangement");
            }


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
     * @param ownerId the ID of the user creating the activity
     */
    @Transactional
    public void addActivity(final ActivityDTO activityDTO, final UUID ownerId) {
        User owner = userRepository.findById(ownerId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Activity activity = new Activity(
            activityDTO.title(),
            activityDTO.dateTime(),
            activityDTO.location(),
            activityDTO.description(),
            owner,
            activityDTO.isPrivate()
        );

        activity.addParticipant(owner);
        activityRepository.save(activity);
    }

    /**
     * Invites a user to a private activity.
     * Only the owner of the activity can send invitations.
     * The invitation is stored in the activity_invitees join table.
     *
     * @param activityId the ID of the activity to invite to
     * @param ownerId the ID of the user who owns the activity (authorization check)
     * @param inviteeId the ID of the user to invite
     * @throws IllegalArgumentException if the activity is not found, the user is not the owner,
     *         the invitee does not exist, or the invitee is already invited/participating
     */
    @Transactional
    public void inviteParticipant(final UUID activityId, final UUID ownerId, final UUID inviteeId) {
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new IllegalArgumentException("Aktivitet ikke funnet"));

        if (!activity.getOwner().getId().equals(ownerId)) {
            throw new IllegalArgumentException("Bare eieren kan invitere deltakere");
        }

        User invitee = userRepository.findById(inviteeId)
            .orElseThrow(() -> new IllegalArgumentException("Bruker ikke funnet"));

        if (activity.getInvitedUsers().contains(invitee)
            ||
            activity.getParticipants().contains(invitee)) {
            throw new IllegalArgumentException("Brukeren er allerede invitert eller deltar");
        }

        activity.addInvitation(invitee);
        activityRepository.save(activity);
    }

    /**
     * Gets all activities where the user is participating.
     *
     * @param userId the user's ID
     * @return list of activities
     */
    public List<ActivityDTO> getActivitiesByUserId(final UUID userId) {
        return activityRepository.findByParticipantId(userId)
            .stream()
            .filter(activity -> !activity.getOwner().getId().equals(userId))
            .map(this::convertToActivityDTO)
            .collect(Collectors.toList());
    }

    /**
     * Gets a specific activity if the user has access to it.
     *
     * @param activityId the activity ID
     * @param userId the user ID
     * @return the activity DTO
     * @throws IllegalArgumentException if the activity is private and user has no access
     */
    public ActivityDTO getActivityById(final UUID activityId, final UUID userId) {
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new RuntimeException("Activity not found"));

        if (!isActivityVisibleToUser(activity, userId)) {
            throw new IllegalArgumentException("Du har ikke tilgang til dette arrangementet");
        }

        return convertToActivityDTO(activity);
    }

    /**
     * Checks if a specific user is participating in a given activity.
     * @param activityId the ID of the activity to check
     * @param userId the ID of the user to check
     * @return true if the user is participating in the activity, false otherwise
     * @throws RuntimeException if the user is not found
     */
    public boolean isUserParticipating(final UUID activityId, final UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        List<Activity> activities = activityRepository.findByParticipantsContaining(user);
        return activities.stream().anyMatch(activity -> activity.getId().equals(activityId));
    }
    /**
     * Retrieves all activities owned by a specific user.
     * This method returns activities where the specified user is the creator/owner.
     *
     * @param ownerId the ID of the owner to find activities for
     * @return a list of activities owned by the specified user
     * @throws RuntimeException if there is a problem retrieving the activities
     */
    @Transactional
    public List<ActivityDTO> getActivitiesByOwnerId(final UUID ownerId) {
        List<Activity> activities = activityRepository.findByOwnerId(ownerId);
        return activities.stream()
                .map(this::convertToActivityDTO)
                .collect(Collectors.toList());
    }
    /**
     * Adds a comment to an activity.
     *
     * @param activityId the ID of the activity
     * @param userId the ID of the user adding the comment
     * @param commentContent the content of the comment
     * @return the added comment DTO
     */
    public CommentDTO addComment(final UUID activityId, final UUID userId, final String commentContent) {
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new RuntimeException("Activity not found"));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!isActivityVisibleToUser(activity, userId)) {
            throw new IllegalAccessError("Du har ikke tilgang til å kommentere på dette arrangementet");
        }

        Comment comment = new Comment(activity, user, commentContent);
        activity.addComment(comment);

        activityRepository.save(activity);

        return new CommentDTO(comment.getId(), user.getId(), user.getBrukernavn(), comment.getContent());
    }
}
