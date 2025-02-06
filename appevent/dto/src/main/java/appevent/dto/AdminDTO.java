package appevent.dto;

import java.util.UUID;

/**
 * Data Transfer Object for Admin.
 *
 * @param id the unique identifier of the admin
 * @param brukernavn the username of the admin
 */
public record AdminDTO(
    UUID id,
    String brukernavn
) { }