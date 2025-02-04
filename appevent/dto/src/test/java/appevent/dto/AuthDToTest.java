package appevent.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuthDToTest {

    @Test
    public void testAuthDTOCreation() {
        AuthDTO authDTO = new AuthDTO("user", "pass", "pass");
        assertEquals("user", authDTO.username());
        assertEquals("pass", authDTO.password());
        assertEquals("pass", authDTO.confirmPassword());
    }

    @Test
    public void testAuthDTOEquality() {
        AuthDTO authDTO1 = new AuthDTO("user", "pass", "pass");
        AuthDTO authDTO2 = new AuthDTO("user", "pass", "pass");
        assertEquals(authDTO1, authDTO2);
    }

    @Test
    public void testAuthDTONotEqual() {
        AuthDTO authDTO1 = new AuthDTO("user1", "pass1", "pass1");
        AuthDTO authDTO2 = new AuthDTO("user2", "pass2", "pass2");
        assertNotEquals(authDTO1, authDTO2);
    }
}
