package appevent.server;

import appevent.core.JwtGenerator;
import appevent.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for handling user-related operations.
 * Provides endpoints for user search, profile management, and user information retrieval.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserService userService;

    /**
     * Constructs a UserController with the specified UserService.
     *
     * @param userService the service handling user operations
     */
    @Autowired
    public UserController(final UserService userService) {
        this.userService = userService;
    }

    /**
     * Search for users by username.
     *
     * @param token authorization token
     * @param term search term
     * @return list of matching users
     */
    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(final @RequestHeader("Authorization") String token, final @RequestParam(name = "term", required = true) String term) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            String userId = JwtGenerator.getUserIdFromToken(token);
            return ResponseEntity.ok(userService.searchUsers(term, UUID.fromString(userId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get current user's profile information.
     *
     * @param token authorization token
     * @return user profile information
     */
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfile(final @RequestHeader("Authorization") String token) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            String userId = JwtGenerator.getUserIdFromToken(token);
            return ResponseEntity.ok(userService.getUserProfile(UUID.fromString(userId)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get a specific user's public profile information.
     *
     * @param token authorization token
     * @param userId ID of the user to get information about
     * @return user profile information
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(final @RequestHeader("Authorization") String token, final @PathVariable UUID userId) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            return ResponseEntity.ok(userService.getUserById(userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Check if a username is available.
     *
     * @param username the username to check
     * @return true if username is available, false otherwise
     */
    @GetMapping("/check-username/{username}")
    public ResponseEntity<Boolean> isUsernameAvailable(final @PathVariable String username) {
        try {
            return ResponseEntity.ok(userService.isUsernameAvailable(username));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get list of users registered for a specific activity.
     *
     * @param token authorization token
     * @param activityId ID of the activity
     * @return list of users registered for the activity
     */
    @GetMapping("/activity/{activityId}")
    public ResponseEntity<List<UserDTO>> getUsersByActivity(final @RequestHeader("Authorization") String token, final @PathVariable UUID activityId) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            return ResponseEntity.ok(userService.getUsersByActivity(activityId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
