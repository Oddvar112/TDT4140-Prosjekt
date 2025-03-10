package appevent.server;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import appevent.core.JwtGenerator;
import jakarta.transaction.Transactional;
/**
 * REST controller for admin operations.
 */
@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    private final AdminService adminService;

    /**
     * Constructs an AdminController with the specified AdminService.
     * @param adminService the admin service
     */
    public AdminController(final AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Deletes an activity.
     * @param token the authorization token
     * @param activityId the id of the activity to delete
     * @return a response entity with the status of the deletion
     */
    @Transactional
    @DeleteMapping("/admin/event/{activityId}")
    public ResponseEntity<?> deleteActivity(final @RequestHeader("Authorization") String token, final @PathVariable("activityId") UUID activityId) {
        if (!JwtGenerator.validateAdminToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            adminService.deleteActivity(activityId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Deletes a user.
     * @param token the authorization token
     * @param userId the id of the user to delete
     * @return a response entity with the status of the deletion
     */
    @Transactional
    @DeleteMapping("/admin/user/{userId}")
    public ResponseEntity<?> deleteUser(final @RequestHeader("Authorization") String token, final @PathVariable("userId") UUID userId) {
        if (!JwtGenerator.validateAdminToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            adminService.deleteUser(userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

        /**
     * Deletes an image by its ID.
     * @param token the JWT token of the authenticated user
     * @param imageId the ID of the image to delete
     * @return a ResponseEntity with the status of the deletion
     */
    @DeleteMapping("/admin/image/{imageId}")
    @Transactional
    public ResponseEntity<?> deleteImage(final @RequestHeader("Authorization") String token, final @PathVariable("imageId") UUID imageId) {
        if (!JwtGenerator.validateAdminToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            String userId = JwtGenerator.getUserIdFromToken(token);
            adminService.deleteImage(imageId, UUID.fromString(userId));
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("En feil oppstod under sletting av bildet");
        }
    }
}
