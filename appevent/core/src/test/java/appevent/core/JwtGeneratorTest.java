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
    void testInvalidToken() {
        assertFalse(JwtGenerator.validateToken("invalid.token.value"));
    }
}