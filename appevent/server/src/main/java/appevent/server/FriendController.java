package appevent.server;

import appevent.core.JwtGenerator;
import appevent.dto.FriendRequestDTO;
import appevent.dto.UserDTO;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/friends")
@CrossOrigin(origins = "http://localhost:3000")
public class FriendController {
    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @Transactional
    @GetMapping
    public ResponseEntity<List<UserDTO>> getFriends(@RequestHeader("Authorization") String token) {
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

    @GetMapping("/not-invited/{eventId}")
    public ResponseEntity<List<UserDTO>> getFriendsNotInvitedToEvent(@RequestHeader("Authorization") String token,@PathVariable("eventId") UUID eventId) {
        if (!JwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            String userId = JwtGenerator.getUserIdFromToken(token);
            return ResponseEntity.ok(friendService.getFriendsNotInvitedToEvent(UUID.fromString(userId), eventId)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/request/{friendId}")
    public ResponseEntity<?> sendFriendRequest(
            @RequestHeader("Authorization") String token,
            @PathVariable("friendId") UUID friendId) {
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

    @PostMapping("/accept/{requestId}")
    public ResponseEntity<?> acceptFriendRequest(
            @RequestHeader("Authorization") String token,
            @PathVariable("requestId") UUID requestId) {
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

    @PostMapping("/reject/{requestId}")
    public ResponseEntity<?> rejectFriendRequest(
            @RequestHeader("Authorization") String token,
            @PathVariable("requestId") UUID requestId) {
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

    @GetMapping("/requests/pending")
    public ResponseEntity<List<FriendRequestDTO>> getPendingRequests(
            @RequestHeader("Authorization") String token) {
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

    @DeleteMapping("/remove/{friendId}")
    public ResponseEntity<?> removeFriend(
        @RequestHeader("Authorization") String token,
        @PathVariable("friendId") UUID friendId) {
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