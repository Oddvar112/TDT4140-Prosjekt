package appevent.server;

import appevent.dto.ActivityDTO;
import appevent.dto.CommentDTO;
import appevent.dto.ImageDTO;
import appevent.dto.ImageUploadDTO;
import appevent.dto.UserDTO;
import appevent.model.Activity;
import appevent.model.ActivityImage;
import appevent.model.ActivityImageRepository;
import appevent.model.ActivityRepository;
import appevent.model.Comment;
import appevent.model.User;
import appevent.model.UserRepository;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Base64;
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
    private final ActivityImageRepository imageRepository;

    /**
     * Constructs an ActivityService with the specified repositories.
     * @param imageRepository the image repository
     * @param activityRepository the activity repository
     * @param userRepository the user repository
     */
    public ActivityService(final ActivityRepository activityRepository, final UserRepository userRepository, final ActivityImageRepository imageRepository) {
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;

    }

    /**
     * Retrieves the list of upcoming activities visible to the user.
     * @param isAdmin true if the user is an admin, false otherwise
     * @param userId ID of the user requesting activities
     * @return the list of visible upcoming activities
     */
    @Transactional
    public List<ActivityDTO> getUpcomingActivities(final UUID userId, final boolean isAdmin) {
        return activityRepository
            .findByDateTimeAfterOrderByDateTimeAsc(LocalDateTime.now())
            .stream()
            .filter(activity -> isActivityVisibleToUser(activity, userId, isAdmin))
            .map(this::convertToActivityDTO)
            .collect(Collectors.toList());
    }

    /**
     * Checks if an activity should be visible to a user.
     * @param isAdmin true if the user is an admin, false otherwise
     * @param activity the activity to check
     * @param userId ID of the user
     * @return true if the activity should be visible
     */
    private boolean isActivityVisibleToUser(final Activity activity, final UUID userId, final boolean isAdmin) {
        return !activity.isPrivate()
        || isAdmin
        || activity.getOwner().getId().equals(userId)
        || activity.getParticipants().stream()
                   .anyMatch(participant -> participant.getId().equals(userId))
                   || activity.getInvitedUsers().stream()
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
     * @param isAdmin true if the user is an admin, false otherwise
     * @param activityId the activity ID
     * @param userId the user ID
     * @return the activity DTO
     * @throws IllegalArgumentException if the activity is private and user has no access
     */
    public ActivityDTO getActivityById(final UUID activityId, final UUID userId, final boolean isAdmin) {
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new RuntimeException("Activity not found"));

        if (!isActivityVisibleToUser(activity, userId, isAdmin)) {
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
        List<Activity> activities = activityRepository.findByDateTimeAfterOrderByDateTimeAsc(LocalDateTime.now());
        return activities.stream()
            .filter(activity -> activity.getOwner().getId().equals(ownerId))
            .map(this::convertToActivityDTO)
            .collect(Collectors.toList());
    }
    /**
     * Adds a comment to an activity.
     * @param isAdmin true if the user is an admin, false otherwise
     * @param activityId the ID of the activity
     * @param userId the ID of the user adding the comment
     * @param commentContent the content of the comment
     * @return the added comment DTO
     */
    public CommentDTO addComment(final UUID activityId, final UUID userId, final String commentContent, final boolean isAdmin) {
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new RuntimeException("Activity not found"));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!isActivityVisibleToUser(activity, userId, isAdmin)) {
            throw new IllegalAccessError("Du har ikke tilgang til å kommentere på dette arrangementet");
        }

        Comment comment = new Comment(activity, user, commentContent);
        activity.addComment(comment);

        activityRepository.save(activity);

        return new CommentDTO(comment.getId(), user.getId(), user.getBrukernavn(), comment.getContent());
    }

    /**
     * Uploads a new image for an activity.
     *
     * @param uploadDTO the image upload details
     * @param userId the ID of the user uploading the image
     * @return the uploaded image DTO
     * @throws IllegalArgumentException if the activity is not found, the user is not found,
     *         or the user is not a participant in the activity
     */
    @Transactional
    public ImageDTO uploadImage(final ImageUploadDTO uploadDTO, final UUID userId) {
        Activity activity = activityRepository.findById(uploadDTO.activityId())
            .orElseThrow(() -> new IllegalArgumentException("Aktivitet ikke funnet"));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Bruker ikke funnet"));

        // Check if the activity has ended (current time is after the activity date)
        if (activity.getDateTime().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Kan ikke laste opp bilder før arrangementet er avsluttet");
        }

        // Check if the user is a participant
        if (!activity.getParticipants().contains(user)) {
            throw new IllegalArgumentException("Bare deltakere kan laste opp bilder");
        }

        // Decode base64 image data
        byte[] imageData = Base64.getDecoder().decode(uploadDTO.base64ImageData());

        ActivityImage image = new ActivityImage(
            activity,
            user,
            imageData,
            uploadDTO.fileName()
        );

        ActivityImage savedImage = imageRepository.save(image);

        return convertToDTO(savedImage);
    }

    /**
     * Gets all images for a specific activity.
     *
     * @param activityId the ID of the activity
     * @return list of image DTOs for the activity
     */
    @Transactional
    public List<ImageDTO> getImagesForActivity(final UUID activityId) {
        List<ActivityImage> images = imageRepository.findByActivityId(activityId);
        return images.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Gets an image by its ID.
     *
     * @param imageId the ID of the image
     * @return the image data
     * @throws IllegalArgumentException if the image is not found
     */
    @Transactional
    public ActivityImage getImageById(final UUID imageId) {
        return imageRepository.findById(imageId)
            .orElseThrow(() -> new IllegalArgumentException("Bilde ikke funnet"));
    }

    /**
     * Converts an ActivityImage entity to an ImageDTO.
     *
     * @param image the image entity
     * @return the image DTO
     */
    private ImageDTO convertToDTO(final ActivityImage image) {
        return new ImageDTO(
            image.getId(),
            image.getActivity().getId(),
            image.getUploadedBy().getId(),
            image.getUploadedBy().getBrukernavn(),
            image.getFileName()
        );
    }

    /**
     * Retrieves all past activities where the user participated or was the owner.
     *
     * @param userId the ID of the user
     * @return list of activities that have already taken place
     */
    @Transactional
    public List<ActivityDTO> getPastActivities(final UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        LocalDateTime now = LocalDateTime.now();
        List<Activity> userActivities = activityRepository.findByParticipantsContaining(user);
        return userActivities.stream()
            .filter(activity -> activity.getDateTime().isBefore(now))
            .map(this::convertToActivityDTO)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves the completed (past) activities owned by a specific user.
     *
     * @param ownerId the ID of the owner
     * @return list of completed activities owned by the user
     */
    @Transactional
    public List<ActivityDTO> getCompletedActivitiesByOwnerId(final UUID ownerId) {
        List<Activity> activities = activityRepository.findByOwnerId(ownerId);
        LocalDateTime now = LocalDateTime.now();

        return activities.stream()
            .filter(activity -> activity.getDateTime().isBefore(now))
            .map(this::convertToActivityDTO)
            .collect(Collectors.toList());
    }

    /**
     * Checks if an activity has ended (current time is after the activity date and time).
     *
     * @param activityId the ID of the activity to check
     * @return true if the activity has ended, false otherwise
     * @throws IllegalArgumentException if the activity is not found
     */
    public boolean isActivityCompleted(final UUID activityId) {
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new IllegalArgumentException("Activity not found"));

        return activity.getDateTime().isBefore(LocalDateTime.now());
    }

}
