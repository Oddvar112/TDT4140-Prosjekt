package appevent.dto;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class UserDTOTest {

    @Test
    public void testUserDTO() {
        UUID id = UUID.randomUUID();
        String brukernavn = "testuser";

        UserDTO userDTO = new UserDTO(id, brukernavn);

        assertEquals(id, userDTO.id());
        assertEquals(brukernavn, userDTO.brukernavn());
    }
}
