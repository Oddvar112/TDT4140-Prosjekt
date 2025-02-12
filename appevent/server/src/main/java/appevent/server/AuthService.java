package appevent.server;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.springframework.stereotype.Service;
import appevent.dto.AuthDTO;
import appevent.dto.AuthDTORegistration;
import appevent.core.JwtGenerator;
import appevent.core.PasswordHasher;
import appevent.core.Validator;
import appevent.model.Admin;
import appevent.model.User;
import appevent.model.UserRepository;


/**
 * Service that handles user authentication and registration within the FlightApp.
 * This service validates user credentials, interacts with the {@link UserRepository}
 * for user data persistence, and manages session creation via {@link Session}.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Authenticates a user based on the provided credentials.
     *
     * @param authDTO the data transfer object containing authentication information
     * @return a JWT token if authentication is successful
     * @throws NoSuchAlgorithmException if the hashing algorithm is not available
     * @throws InvalidKeySpecException if the key specification is invalid
     * @throws IllegalArgumentException if the user is not found or the password is incorrect
     */
    public String login(final AuthDTO authDTO) throws NoSuchAlgorithmException, InvalidKeySpecException {
        validateCredentials(authDTO.username(), authDTO.password());

        User user = userRepository.findByBrukernavn(authDTO.username())
            .orElseThrow(() -> new IllegalArgumentException("Bruker ikke funnet"));

        if (PasswordHasher.verifyPassword(authDTO.password(), user.getPassord())) {
            if (user instanceof Admin) {
                return JwtGenerator.generateToken(user.getBrukernavn(), user.getId());
            } else {
                return JwtGenerator.generateToken(user.getBrukernavn(), user.getId());
            }
        }

        throw new IllegalArgumentException("Feil passord");
    }

    /**
     * Registers a new user with the provided credentials.
     *
     * @param authDTORegistration the data transfer object containing registration information
     * @return a JWT token if registration is successful
     * @throws NoSuchAlgorithmException if the hashing algorithm is not available
     * @throws InvalidKeySpecException if the key specification is invalid
     * @throws IOException if an I/O error occurs
     * @throws IllegalArgumentException if the username is already taken or validation fails
     */
    public String register(final AuthDTORegistration authDTORegistration) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        validateCredentials(authDTORegistration.username(), authDTORegistration.password());
        validateRegistration(authDTORegistration);

        if (userRepository.findByBrukernavn(authDTORegistration.username()).isPresent()) {
            throw new IllegalArgumentException("Brukernavn er allerede tatt");
        }

        String hashedPassword = PasswordHasher.hashPassword(authDTORegistration.password());
        User newUser = new User(authDTORegistration.username(), hashedPassword);
        userRepository.save(newUser);

        return JwtGenerator.generateToken(newUser.getBrukernavn(), newUser.getId());
    }

    /**
     * Validates the format of the username and password.
     *
     * @param username the username to validate
     * @param password the password to validate
     * @throws IllegalArgumentException if the username or password is in an invalid format
     */
    private void validateCredentials(final String username, final String password) {
        StringBuilder validationErrors = new StringBuilder();
        if (!Validator.validateUsername(username)) {
            validationErrors.append("Username requirements not met:\n")
            .append("- Must be between 5 and 20 characters long\n")
            .append("- Can only contain letters and numbers (a-z, A-Z, 0-9)\n")
            .append("- Cannot be empty\n");
        }
        if (!Validator.validatePassword(password)) {
            validationErrors.append("Password requirements not met:\n")
            .append("- Must be at least 8 characters long\n")
            .append("- Must contain at least one number\n")
            .append("- Must contain at least one uppercase letter\n")
            .append("- Must contain at least one lowercase letter\n")
            .append("- Must contain at least one special character (!@#$%^&*)\n")
            .append("- Cannot be empty");
        }
        if (validationErrors.length() > 0) {
            throw new IllegalArgumentException(validationErrors.toString().trim());
        }
    }

    /**
     * Validates the registration data including password confirmation.
     *
     * @param authDTORegistration the data transfer object containing registration information
     * @throws IllegalArgumentException if passwords do not match or required fields are blank
     */
    private void validateRegistration(final AuthDTORegistration authDTORegistration) {
        if (authDTORegistration.username() == null || authDTORegistration.password() == null || authDTORegistration.confirmPassword() == null) {
            throw new IllegalArgumentException("All fields are required for registration.");
        }
        if (!authDTORegistration.password().equals(authDTORegistration.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }
    }

}
