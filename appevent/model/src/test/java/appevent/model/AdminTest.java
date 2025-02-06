package appevent.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class AdminTest {
    
    @Test
    void testConstructorAndGetters() {
        Admin admin = new Admin("testAdmin", "secret");
        assertEquals("testAdmin", admin.getBrukernavn());
        assertEquals("secret", admin.getPassord());
    }

    @Test
    void testDefaultConstructor() {
        Admin admin = new Admin();
        assertNull(admin.getId());
        assertNull(admin.getBrukernavn());
        assertNull(admin.getPassord());
    }

}
