package appevent.dto;

import java.util.UUID;

/**
 * Data Transfer Object for User.
 *
 * @param id the unique identifier of the user
 * @param brukernavn the username of the user
 */
public record UserDTO(
    UUID id,
    String brukernavn
) { }

