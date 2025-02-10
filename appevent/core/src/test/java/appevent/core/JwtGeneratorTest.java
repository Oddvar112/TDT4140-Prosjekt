package appevent.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

class JwtGeneratorTest {

    @Test
    void testGenerateTokenAndValidate() {
        String token = JwtGenerator.generateToken("testUser", UUID.randomUUID());
        assertTrue(JwtGenerator.validateToken(token));
    }

    @Test
    void testGenerateAdminTokenAndValidate() {
        String token = JwtGenerator.generateAdminToken("testAdmin", UUID.randomUUID());
        assertTrue(JwtGenerator.validateAdminToken(token));

        String userToken = JwtGenerator.generateToken("testNormalUser", UUID.randomUUID());
        assertFalse(JwtGenerator.validateAdminToken(userToken));
    }

    @Test
    void testInvalidToken() {
        assertFalse(JwtGenerator.validateToken("invalid.token.value"));
    }

    @Test
    void testGetAdminIdFromToken() {
        UUID id = UUID.randomUUID();
        String token = JwtGenerator.generateAdminToken("testAdmin", id);
        assertEquals(String.valueOf(id), JwtGenerator.getUserIdFromToken(token));
    }

    @Test
    void testGeIdFromInvalidToken() {
        assertThrows(Exception.class, () -> JwtGenerator.getUserIdFromToken("invalid.token.value"));
    }
}