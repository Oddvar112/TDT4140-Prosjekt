package appevent.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

class JwtGeneratorTest {

    @Test
    void testGenerateTokenAndValidate() {
        UUID userId = UUID.randomUUID();
        String token = JwtGenerator.generateToken("testUser", userId);
        assertTrue(JwtGenerator.validateToken(token));
    }

    @Test
    void testInvalidToken() {
        assertFalse(JwtGenerator.validateToken("invalid.token.value"));
    }
}