package appevent.server;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import appevent.core.JwtGenerator;
import appevent.dto.ActivityDTO;
import appevent.dto.CommentDTO;
import appevent.dto.CommentRequestDTO;
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
    public ResponseEntity<List<ActivityDTO>> getUpcomingActivities(@RequestHeader("Authorization") String token) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            String userId = JwtGenerator.getUserIdFromToken(token);
            return ResponseEntity.ok(activityService.getUpcomingActivities(UUID.fromString(userId)));
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
    public ResponseEntity<String> toggleRegistration(@RequestHeader("Authorization") String token, @RequestBody UUID activityId) {
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
    public ResponseEntity<Void> addActivity(@RequestHeader("Authorization") String token, @RequestBody ActivityDTO activity) {
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
    public ResponseEntity<List<ActivityDTO>> getMyActivities(@RequestHeader("Authorization") String token) {
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
    public ResponseEntity<Boolean> isParticipating(@RequestHeader("Authorization") String token, @RequestBody UUID activityId) {
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
    public ResponseEntity<ActivityDTO> getActivity(@RequestHeader("Authorization") String token, @PathVariable("activityId") UUID activityId) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            String userId = JwtGenerator.getUserIdFromToken(token);
            ActivityDTO activity = activityService.getActivityById(activityId, UUID.fromString(userId));
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
    public ResponseEntity<String> inviteParticipant(@RequestHeader("Authorization") String token,@PathVariable("activityId") UUID activityId, @PathVariable("inviteeId") UUID inviteeId) {
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
    public ResponseEntity<List<ActivityDTO>> getOwnedActivities(@RequestHeader("Authorization") String token) {
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
     * @param commentContent the content of the comment
     * @return the response entity with the added comment or an error status
     */
    @Transactional
    @PostMapping("/{activityId}/comment")
    public ResponseEntity<CommentDTO> addComment(@RequestHeader("Authorization") String token,@PathVariable("activityId") UUID activityId,@RequestBody CommentRequestDTO request) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            String userId = JwtGenerator.getUserIdFromToken(token);
            CommentDTO addedComment = activityService.addComment(activityId,UUID.fromString(userId),request.content());
            return ResponseEntity.status(HttpStatus.CREATED).body(addedComment);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch(IllegalAccessError e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}