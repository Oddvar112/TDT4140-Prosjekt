package appevent.server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import appevent.dto.AuthDTO;
import appevent.dto.AuthDTORegistration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * REST Controller that handles authentication requests, including login and registration.
 * This controller delegates authentication business logic to the {@link AuthService}.
 * It provides endpoints for user login and registration and returns appropriate HTTP statuses
 * and responses based on the outcome.
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Constructs an AuthController with the specified AuthService.
     *
     * @param authService the authentication service to be used by this controller
     */
    @Autowired
    public AuthController(final AuthService authService) {
        this.authService = authService;
    }

    /**
     * Handles user login requests.
     *
     * @param authDTO the authentication data transfer object containing login credentials
     * @return a ResponseEntity containing the authentication token if login is successful,
     *         or an error message if login fails
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody final AuthDTO authDTO) {
        try {
            String token = authService.login(authDTO);
            return ResponseEntity.ok(token);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("En feil oppstod under innlogging");
        }
    }

    /**
     * Handles user registration requests.
     *
     * @param authDTORegistration the authentication data transfer object containing registration details
     * @return a ResponseEntity containing the authentication token if registration is successful,
     *         or an error message if registration fails
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody final AuthDTORegistration authDTORegistration) {
        try {
            String token = authService.register(authDTORegistration);
            return ResponseEntity.status(HttpStatus.CREATED).body(token);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("En feil oppstod under registrering");
        }
    }
}
