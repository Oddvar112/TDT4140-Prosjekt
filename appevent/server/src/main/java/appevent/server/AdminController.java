package appevent.server;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import appevent.core.JwtGenerator;

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
    @DeleteMapping("/admin/event")
    public ResponseEntity<?> deleteActivity(final @RequestHeader("Authorization") String token, final @RequestBody UUID activityId) {
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
    @DeleteMapping("/admin/user")
    public ResponseEntity<?> deleteUser(final @RequestHeader("Authorization") String token, final @RequestBody UUID userId) {
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
}
