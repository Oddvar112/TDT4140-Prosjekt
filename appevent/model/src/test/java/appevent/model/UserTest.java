package appevent.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class UserTest {

    @Test
    void testConstructorAndGetters() {
        User user = new User("testUser", "secret");
        assertEquals("testUser", user.getBrukernavn());
        assertEquals("secret", user.getPassord());
        // ID er som regel null før persistering
        assertNull(user.getId());
    }

    @Test
    void testDefaultConstructor() {
        // For JPA; instansieres normalt via reflection
        User user = new User();
        assertNull(user.getId());
        assertNull(user.getBrukernavn());
        assertNull(user.getPassord());
    }
}