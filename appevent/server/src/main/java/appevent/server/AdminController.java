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

    public AdminController(final AdminService adminService) {
        this.adminService = adminService;
    }

    @DeleteMapping("/admin/event")
    public ResponseEntity<?> deleteActivity(final @RequestHeader("Authorization") String token, final @RequestBody UUID activityId) {
        if (!JwtGenerator.validateAdminToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } try {
            adminService.deleteActivity(activityId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/admin/user")
    public ResponseEntity<?> deleteUser(final @RequestHeader("Authorization") String token, final @RequestBody UUID userId) {
        if (!JwtGenerator.validateAdminToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } try {
            adminService.deleteUser(userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
}
