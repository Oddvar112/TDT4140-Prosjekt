package appevent.server;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import appevent.core.JwtGenerator;
import appevent.dto.ActivityDTO;
import appevent.dto.SearchDTO;
import appevent.dto.CommentDTO;
import appevent.dto.CommentRequestDTO;
import appevent.dto.ImageDTO;
import appevent.dto.ImageUploadDTO;
import appevent.model.ActivityImage;
import jakarta.transaction.Transactional;

/**
 * REST controller for managing activities.
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    private final ActivityService activityService;

    /**
     * Constructs an ActivityController with the specified ActivityService.
     *
     * @param activityService the activity servic
     */
    @Autowired
    public ActivityController(final ActivityService activityService) {
        this.activityService = activityService;
    }

    /**
     * Retrieves the list of upcoming activities visible to the user.
     *
     * @param token the authorization token
     * @return the response entity with the list of upcoming activities or an error status
     */
    @Transactional
    @GetMapping("/upcoming")
    public ResponseEntity<List<ActivityDTO>> getUpcomingActivities(final @RequestHeader("Authorization") String token) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean isAdmin = JwtGenerator.validateAdminToken(token);
        try {
            String userId = JwtGenerator.getUserIdFromToken(token);
            return ResponseEntity.ok(activityService.getUpcomingActivities(UUID.fromString(userId), isAdmin));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Toggles the participation status for a given activity and user.
     *
     * @param token the authorization token
     * @param activityId the id of the activity
     * @return the response entity with the status of the operation
     */
    @PostMapping("/toggleParticipation")
    public ResponseEntity<String> toggleRegistration(final @RequestHeader("Authorization") String token, final @RequestBody UUID activityId) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (activityService.getActivitiesByOwnerId(UUID.fromString(JwtGenerator.getUserIdFromToken(token))).stream().anyMatch(activity -> activity.id().equals(activityId))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Du kan ikke melde deg av din egen aktivitet");
        }
        try {
            String tokenUserId = JwtGenerator.getUserIdFromToken(token);
            activityService.toggleParticipation(activityId, UUID.fromString(tokenUserId));
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Det oppstod en feil ved registrering av deltakelse");
        }
    }

    /**
     * Adds a new activity.
     *
     * @param token the authorization token
     * @param activity the activity details
     * @return the response entity with the status of the operation
     */
    @Transactional
    @PostMapping("/add")
    public ResponseEntity<Void> addActivity(final @RequestHeader("Authorization") String token, final @RequestBody ActivityDTO activity) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            String userId = JwtGenerator.getUserIdFromToken(token);
            activityService.addActivity(activity, UUID.fromString(userId));
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Retrieves the list of activities the authenticated user is participating in.
     *
     * @param token the authorization token
     * @return the response entity with the list of user's activities or an error status
     */
    @Transactional
    @GetMapping("/myactivities")
    public ResponseEntity<List<ActivityDTO>> getMyActivities(final @RequestHeader("Authorization") String token) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            String userId = JwtGenerator.getUserIdFromToken(token);
            return ResponseEntity.ok(activityService.getActivitiesByUserId(UUID.fromString(userId)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Checks if the authenticated user is participating in a specific activity.
     *
     * @param token the authorization token
     * @param activityId the id of the activity to check
     * @return the response entity with boolean indicating participation status
     */
    @PostMapping("/isParticipating")
    public ResponseEntity<Boolean> isParticipating(final @RequestHeader("Authorization") String token, final @RequestBody UUID activityId) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            String userId = JwtGenerator.getUserIdFromToken(token);
            return ResponseEntity.ok(
                activityService.isUserParticipating(activityId, UUID.fromString(userId))
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves a specific activity by its ID if the user has access.
     *
     * @param token the authorization token
     * @param activityId the ID of the activity to retrieve
     * @return the response entity with the activity or an error status
     */
    @Transactional
    @GetMapping("/activityinfo/{activityId}")
    public ResponseEntity<ActivityDTO> getActivity(final @RequestHeader("Authorization") String token, final @PathVariable("activityId") UUID activityId) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            String userId = JwtGenerator.getUserIdFromToken(token);
            ActivityDTO activity = activityService.getActivityById(activityId, UUID.fromString(userId), JwtGenerator.validateAdminToken(token));
            return ResponseEntity.ok(activity);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Invites a user to a private activity.
     *
     * @param token the authorization token
     * @param activityId the ID of the activity
     * @param inviteeId the ID of the user to invite
     * @return the response entity with the status of the operation
     */
    @PostMapping("/{activityId}/invite/{inviteeId}")
    public ResponseEntity<String> inviteParticipant(final @RequestHeader("Authorization") String token, final @PathVariable("activityId") UUID activityId, final @PathVariable("inviteeId") UUID inviteeId) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            String ownerId = JwtGenerator.getUserIdFromToken(token);
            activityService.inviteParticipant(activityId, UUID.fromString(ownerId), inviteeId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Det oppstod en feil ved invitasjon av deltaker");
        }
    }

    /**
    * Retrieves the list of activities owned by the authenticated user.
    *
    * @param token the authorization token
    * @return the response entity with the list of activities owned by the user
    */
    @Transactional
    @GetMapping("/owned")
    public ResponseEntity<List<ActivityDTO>> getOwnedActivities(final @RequestHeader("Authorization") String token) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            String userId = JwtGenerator.getUserIdFromToken(token);
            return ResponseEntity.ok(activityService.getActivitiesByOwnerId(UUID.fromString(userId)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

        /**
     * Adds a comment to an activity.
     *
     * @param token the authorization token
     * @param activityId the ID of the activity
     * @param request the comment request
     * @return the response entity with the added comment or an error status
     */
    @Transactional
    @PostMapping("/{activityId}/comment")
    public ResponseEntity<CommentDTO> addComment(final @RequestHeader("Authorization") String token, final @PathVariable("activityId") UUID activityId, final @RequestBody CommentRequestDTO request) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            String userId = JwtGenerator.getUserIdFromToken(token);
            CommentDTO addedComment = activityService.addComment(activityId, UUID.fromString(userId), request.content(), JwtGenerator.validateAdminToken(token));
            return ResponseEntity.status(HttpStatus.CREATED).body(addedComment);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalAccessError e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Uploads a new image for an activity.
     *
     * @param token the JWT token of the authenticated user
     * @param uploadDTO the image upload details
     * @return a ResponseEntity containing the uploaded image DTO
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(final @RequestHeader("Authorization") String token, final @RequestBody ImageUploadDTO uploadDTO) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String userId = JwtGenerator.getUserIdFromToken(token);
            ImageDTO uploadedImage = activityService.uploadImage(uploadDTO, UUID.fromString(userId));
            return ResponseEntity.status(HttpStatus.CREATED).body(uploadedImage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("En feil oppstod under opplasting av bildet");
        }
    }

    /**
     * Gets all images for a specific activity.
     *
     * @param token the JWT token of the authenticated user
     * @param activityId the ID of the activity
     * @return a ResponseEntity containing the list of image DTOs
     */
    @GetMapping("/activity/{activityId}")
    public ResponseEntity<List<ImageDTO>> getImagesForActivity(final @RequestHeader("Authorization") String token, final @PathVariable("activityId") UUID activityId) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<ImageDTO> images = activityService.getImagesForActivity(activityId);
            return ResponseEntity.ok(images);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Gets an image by its ID.
     *
     * @param token the JWT token of the authenticated user
     * @param imageId the ID of the image
     * @return a ResponseEntity containing the image data
     */
    @GetMapping("/{imageId}")
    public ResponseEntity<?> getImage(final @RequestHeader("Authorization") String token, final @PathVariable("imageId") UUID imageId) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            ActivityImage image = activityService.getImageById(imageId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("filename", image.getFileName());
            return new ResponseEntity<>(image.getImageData(), headers, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("En feil oppstod under henting av bildet");
        }
    }


    /**
     * Retrieves the list of past activities the authenticated user participated in.
     *
     * @param token the authorization token
     * @return the response entity with the list of past activities
     */
    @Transactional
    @GetMapping("/past")
    public ResponseEntity<List<ActivityDTO>> getPastActivities(final @RequestHeader("Authorization") String token) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            String userId = JwtGenerator.getUserIdFromToken(token);
            return ResponseEntity.ok(activityService.getPastActivities(UUID.fromString(userId)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves the list of completed (past) activities owned by the authenticated user.
     *
     * @param token the authorization token
     * @return the response entity with the list of completed activities
     */
    @Transactional
    @GetMapping("/completed")
    public ResponseEntity<List<ActivityDTO>> getCompletedOwnedActivities(final @RequestHeader("Authorization") String token) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            String userId = JwtGenerator.getUserIdFromToken(token);
            return ResponseEntity.ok(activityService.getCompletedActivitiesByOwnerId(UUID.fromString(userId)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Searches for activities based on a search containing a time, type, and optional search string.
     * @param token the authorization token
     * @param search the search details
     * @return the response entity with the list of activities or an error status
     */
    @Transactional
    @PostMapping("/search")
    public ResponseEntity<List<ActivityDTO>> searchActivities(@RequestHeader("Authorization") final String token, @RequestBody final SearchDTO search) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            String userId = JwtGenerator.getUserIdFromToken(token);
        boolean isAdmin = JwtGenerator.validateAdminToken(token);
        List<ActivityDTO> activities = activityService.searchActivities(search, UUID.fromString(userId), isAdmin);
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    /**
     * Checks if an activity has been completed (past the activity date and time).
     *
     * @param token the authorization token
     * @param activityId the ID of the activity to check
     * @return the response entity with a boolean indicating if the activity is completed
     */
    @GetMapping("/isCompleted/{activityId}")
    public ResponseEntity<Boolean> isActivityCompleted(final @RequestHeader("Authorization") String token, final @PathVariable("activityId") UUID activityId) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            return ResponseEntity.ok(activityService.isActivityCompleted(activityId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
