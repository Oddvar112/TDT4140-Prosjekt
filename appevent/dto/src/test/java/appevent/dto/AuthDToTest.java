package appevent.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuthDTOTest {

    @Test
    public void testAuthDTOCreation() {
        AuthDTORegistration authDTO = new AuthDTORegistration("user", "pass", "pass");
        assertEquals("user", authDTO.username());
        assertEquals("pass", authDTO.password());
        assertEquals("pass", authDTO.confirmPassword());
    }

    @Test
    public void testAuthDTOEquality() {
        AuthDTORegistration authDTO1 = new AuthDTORegistration("user", "pass", "pass");
        AuthDTORegistration authDTO2 = new AuthDTORegistration("user", "pass", "pass");
        assertEquals(authDTO1, authDTO2);
    }

    @Test
    public void testAuthDTONotEqual() {
        AuthDTORegistration authDTO1 = new AuthDTORegistration("user1", "pass1", "pass1");
        AuthDTORegistration authDTO2 = new AuthDTORegistration("user2", "pass2", "pass2");
        assertNotEquals(authDTO1, authDTO2);
    }

    @Test
    public void testAuthDTO() {
        AuthDTO authDTO = new AuthDTO("user", "pass");
        assertEquals("user", authDTO.username());
        assertEquals("pass", authDTO.password());
    }

}
