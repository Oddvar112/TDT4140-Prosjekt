package appevent.server;

import appevent.core.JwtGenerator;
import appevent.dto.FriendRequestDTO;
import appevent.dto.UserDTO;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing friend-related operations.
 */
@RestController
@RequestMapping("/api/friends")
@CrossOrigin(origins = "http://localhost:3000")
public class FriendController {
    private final FriendService friendService;

    public FriendController(final FriendService friendService) {
        this.friendService = friendService;
    }

    /**
     * Retrieves the list of friends for the authenticated user.
     *
     * @param token the JWT token of the authenticated user
     * @return a ResponseEntity containing the list of friends
     */
    @Transactional
    @GetMapping
    public ResponseEntity<List<UserDTO>> getFriends(final @RequestHeader("Authorization") String token) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            String userId = JwtGenerator.getUserIdFromToken(token);
            return ResponseEntity.ok(friendService.getFriends(UUID.fromString(userId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves the list of friends who are not invited to a specific event.
     *
     * @param token the JWT token of the authenticated user
     * @param eventId the ID of the event
     * @return a ResponseEntity containing the list of friends not invited to the event
     */
    @GetMapping("/not-invited/{eventId}")
    public ResponseEntity<List<UserDTO>> getFriendsNotInvitedToEvent(final @RequestHeader("Authorization") String token, final @PathVariable("eventId") UUID eventId) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            String userId = JwtGenerator.getUserIdFromToken(token);
            return ResponseEntity.ok(friendService.getFriendsNotInvitedToEvent(UUID.fromString(userId), eventId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Sends a friend request to another user.
     *
     * @param token the JWT token of the authenticated user
     * @param friendId the ID of the user to send the friend request to
     * @return a ResponseEntity indicating the result of the operation
     */
    @PostMapping("/request/{friendId}")
    public ResponseEntity<?> sendFriendRequest(final @RequestHeader("Authorization") String token, final @PathVariable("friendId") UUID friendId) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            String userId = JwtGenerator.getUserIdFromToken(token);
            friendService.sendFriendRequest(UUID.fromString(userId), friendId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Accepts a friend request.
     *
     * @param token the JWT token of the authenticated user
     * @param requestId the ID of the friend request to accept
     * @return a ResponseEntity indicating the result of the operation
     */
    @PostMapping("/accept/{requestId}")
    public ResponseEntity<?> acceptFriendRequest(final @RequestHeader("Authorization") String token, final @PathVariable("requestId") UUID requestId) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            String userId = JwtGenerator.getUserIdFromToken(token);
            friendService.acceptFriendRequest(requestId, UUID.fromString(userId));
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Rejects a friend request.
     *
     * @param token the JWT token of the authenticated user
     * @param requestId the ID of the friend request to reject
     * @return a ResponseEntity indicating the result of the operation
     */
    @PostMapping("/reject/{requestId}")
    public ResponseEntity<?> rejectFriendRequest(final @RequestHeader("Authorization") String token, final @PathVariable("requestId") UUID requestId) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            String userId = JwtGenerator.getUserIdFromToken(token);
            friendService.rejectFriendRequest(requestId, UUID.fromString(userId));
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves the list of pending friend requests for the authenticated user.
     *
     * @param token the JWT token of the authenticated user
     * @return a ResponseEntity containing the list of pending friend requests
     */
    @GetMapping("/requests/pending")
    public ResponseEntity<List<FriendRequestDTO>> getPendingRequests(final @RequestHeader("Authorization") String token) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            String userId = JwtGenerator.getUserIdFromToken(token);
            return ResponseEntity.ok(friendService.getPendingRequests(UUID.fromString(userId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Removes a friend from the authenticated user's friend list.
     *
     * @param token the JWT token of the authenticated user
     * @param friendId the ID of the friend to remove
     * @return a ResponseEntity indicating the result of the operation
     */
    @DeleteMapping("/remove/{friendId}")
    public ResponseEntity<?> removeFriend(final @RequestHeader("Authorization") String token, final @PathVariable("friendId") UUID friendId) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            String userId = JwtGenerator.getUserIdFromToken(token);
            friendService.removeFriend(UUID.fromString(userId), friendId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
