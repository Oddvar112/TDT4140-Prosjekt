package appevent.server;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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
     * @param activityService the activity service
     */
    @Autowired
    public ActivityController(final ActivityService activityService) {
        this.activityService = activityService;
    }

    /**
     * Retrieves the list of upcoming activities.
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
        try {
            return ResponseEntity.ok(activityService.getUpcomingActivities());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Toggles the participation status for a given activity and user.
     *
     * @param token the authorization token
     * @return the response entity with the status of the operation
     * @param activityId the id of the activity
     */
    @Transactional
    @PostMapping("/toggleParticipation")
    public ResponseEntity<String> toggleRegistration(@RequestHeader("Authorization") final String token, final @RequestBody UUID activityId) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            String tokenUserId = JwtGenerator.getUserIdFromToken(token);
            activityService.toggleParticipation(activityId, UUID.fromString(tokenUserId));
            return ResponseEntity.ok().build();
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
            activityService.addActivity(activity);
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
    public ResponseEntity<Boolean> isParticipating(@RequestHeader("Authorization") final String token, @RequestBody final UUID activityId) {
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
    * Retrieves a specific activity by its ID.
    *
    * @param token the authorization token
    * @param activityId the ID of the activity to retrieve
    * @return the response entity with the activity or an error status
     */
    @Transactional
    @GetMapping("/activityinfo/{activityId}")
    public ResponseEntity<ActivityDTO> getActivity(@RequestHeader("Authorization") final String token, @PathVariable("activityId") final UUID activityId) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            ActivityDTO activity = activityService.getActivityById(activityId);
            return ResponseEntity.ok(activity);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
