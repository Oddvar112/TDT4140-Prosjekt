package appevent.dto;

/**
 * Record representing authentication data transfer object (DTO).
 *
 * @param username the username of the user
 * @param password the password of the user
 * @param confirmPassword the confirmation of the password
 */
public record AuthDTO(
    String username,
    String password,
    String confirmPassword
) { }
