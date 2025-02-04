package appevent.core;

/**
 * Validator class responsible for validating user input, such as username and password.
 */
public final class Validator {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Validator() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Validates the given username.
     * A valid username should be between 5 and 20 characters, contain only alphanumeric characters,
     * and should not be null or empty.
     *
     * @param username The username to be validated.
     * @return true if the username is valid, false otherwise.
     */
    public static boolean validateUsername(final String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        return username.length() >= 5 && username.length() <= 20 && username.matches("^[a-zA-Z0-9]+$");
    }

    /**
     * Validates the given password.
     * A valid password should be at least 8 characters long and must contain at least one digit,
     * one uppercase letter, one lowercase letter, and one special character (!@#$%^&*).
     *
     * @param password The password to be validated.
     * @return true if the password is valid, false otherwise.
     */
    public static boolean validatePassword(final String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        if (password.length() < 8) {
            return false;
        }
        return password.matches(".*\\d.*")
               && password.matches(".*[A-Z].*")
               && password.matches(".*[a-z].*")
               && password.matches(".*[!@#$%^&*].*");
    }

}
