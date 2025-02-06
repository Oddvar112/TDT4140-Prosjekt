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
        assertTrue(JwtGenerator.validateToken(token));
    }

    @Test
    void testInvalidToken() {
        assertFalse(JwtGenerator.validateToken("invalid.token.value"));
    }

    @Test
    void testGetAdminIdFromToken() {
        UUID id = UUID.randomUUID();
        String token = JwtGenerator.generateAdminToken("testAdmin", id);
        assertEquals(String.valueOf(id), JwtGenerator.getAdminIdFromToken(token));
    }

    @Test
    void testGeIdFromInvalidToken() {
        assertThrows(Exception.class, () -> JwtGenerator.getAdminIdFromToken("invalid.token.value"));
    }
}