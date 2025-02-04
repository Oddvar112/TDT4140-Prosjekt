package appevent.dto;

/**
 * Data Transfer Object for authentication.
 */
public record AuthDTO(
    /**
     * The username of the user.
     */
    String username,

    /**
     * The password of the user.
     */
    String password,

    /**
     * The confirmation of the password.
     */
    String confirmPassword
) {}
