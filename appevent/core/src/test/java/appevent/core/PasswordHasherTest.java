package appevent.core;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Unit tests for PasswordHasher class.
 */
public class PasswordHasherTest {

    /**
     * Tests that a valid hash is generated for a password.
     */
    @Test
    public void testHashPassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String password = "ValidPassword123!";
        String hashedPassword = PasswordHasher.hashPassword(password);
        assertNotNull(hashedPassword);
        assertFalse(hashedPassword.isEmpty());
        assertTrue(hashedPassword.contains(":"));  // Verify that salt and hash are combined
    }

    /**
     * Tests that hashing the same password results in different outputs due to unique salts.
     */
    @Test
    public void testHashPasswordUniqueness() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String password = "ValidPassword123!";
        String hashedPassword1 = PasswordHasher.hashPassword(password);
        String hashedPassword2 = PasswordHasher.hashPassword(password);
        assertNotEquals(hashedPassword1, hashedPassword2);  // Should be different due to unique salts
    }

    /**
     * Tests that verifyPassword returns true for correct password.
     */
    @Test
    public void testVerifyPasswordCorrect() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String password = "ValidPassword123!";
        String hashedPassword = PasswordHasher.hashPassword(password);
        assertTrue(PasswordHasher.verifyPassword(password, hashedPassword));
    }

    /**
     * Tests that verifyPassword returns false for incorrect password.
     */
    @Test
    public void testVerifyPasswordIncorrect() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String password = "ValidPassword123!";
        String incorrectPassword = "WrongPassword!";
        String hashedPassword = PasswordHasher.hashPassword(password);
        assertFalse(PasswordHasher.verifyPassword(incorrectPassword, hashedPassword));
    }
    
}
