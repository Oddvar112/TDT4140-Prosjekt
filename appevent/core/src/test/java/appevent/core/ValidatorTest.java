package appevent.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Unit tests for the Validator class, testing username and password validation.
 */
public class ValidatorTest {

    /**
     * Tests if a valid username passes the validation.
     */
    @Test
    public void testValidUsername() {
        boolean result = Validator.validateUsername("User123");
        assertTrue(result);
    }

    /**
     * Tests if a username that is too short fails the validation.
     */
    @Test
    public void testShortUsername() {
        boolean result = Validator.validateUsername("usr");
        assertFalse(result);
    }

    /**
     * Tests if a username that is too long fails the validation.
     */
    @Test
    public void testLongUsername() {
        boolean result = Validator.validateUsername("ThisIsWayTooLongForAUsername123");
        assertFalse(result);
    }

    /**
     * Tests if a username containing special characters fails the validation.
     */
    @Test
    public void testUsernameWithSpecialChars() {
        boolean result = Validator.validateUsername("User@123");
        assertFalse(result);
    }

    /**
     * Tests if a null username fails the validation.
     */
    @Test
    public void testNullUsername() {
        boolean result = Validator.validateUsername(null);
        assertFalse(result);
    }

    /**
     * Tests if an empty username fails the validation.
     */
    @Test
    public void testEmptyUsername() {
        boolean result = Validator.validateUsername("");
        assertFalse(result);
    }

    /**
     * Tests if a valid password passes the validation.
     */
    @Test
    public void testValidPassword() {
        boolean result = Validator.validatePassword("Valid123!");
        assertTrue(result);
    }

    /**
     * Tests if a password missing an uppercase letter fails the validation.
     */
    @Test
    public void testPasswordMissingUppercase() {
        boolean result = Validator.validatePassword("valid123!");
        assertFalse(result);
    }

    /**
     * Tests if a password missing a digit fails the validation.
     */
    @Test
    public void testPasswordMissingDigit() {
        boolean result = Validator.validatePassword("ValidPass!");
        assertFalse(result);
    }

    /**
     * Tests if a password missing a special character fails the validation.
     */
    @Test
    public void testPasswordMissingSpecialChar() {
        boolean result = Validator.validatePassword("Valid123");
        assertFalse(result);
    }

    /**
     * Tests if a password that is too short fails the validation.
     */
    @Test
    public void testShortPassword() {
        boolean result = Validator.validatePassword("V12!");
        assertFalse(result);
    }

    /**
     * Tests if a null password fails the validation.
     */
    @Test
    public void testNullPassword() {
        boolean result = Validator.validatePassword(null);
        assertFalse(result);
    }

    /**
     * Tests if an empty password fails the validation.
     */
    @Test
    public void testEmptyPassword() {
        boolean result = Validator.validatePassword("");
        assertFalse(result);
    }
    
}
