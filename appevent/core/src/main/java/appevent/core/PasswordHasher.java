package appevent.core;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Utility class for secure password hashing and verification using PBKDF2 with HMAC-SHA256.
 * This class supports salting and key stretching to enhance password security.
 */
public final class PasswordHasher {

    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private PasswordHasher() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Generates a cryptographically secure random salt.
     *
     * @return A byte array representing the securely generated salt.
     */
    private static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        SECURE_RANDOM.nextBytes(salt);
        return salt;
    }

    /**
     * Hashes a password using PBKDF2 with HMAC-SHA256, including a unique salt and a defined number of iterations.
     * The resulting salt and hash are concatenated and encoded for storage.
     *
     * @param password The plain-text password to be hashed.
     * @return A Base64-encoded string containing the salt and hash in the format "<salt>:<hash>".
     * @throws NoSuchAlgorithmException If the specified hash algorithm is not available.
     * @throws InvalidKeySpecException  If the specified key specification is invalid.
     */
    public static String hashPassword(final String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = generateSalt();
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    /**
     * Verifies a password by rehashing it with the stored salt and comparing the result to the stored hash.
     * A time-constant comparison is used to prevent timing attacks.
     *
     * @param inputPassword The plain-text password to verify.
     * @param storedValue   The stored Base64-encoded salt and hash, formatted as "<salt>:<hash>".
     * @return {@code true} if the password matches, otherwise {@code false}.
     * @throws NoSuchAlgorithmException If the specified hash algorithm is not available.
     * @throws InvalidKeySpecException  If the specified key specification is invalid.
     * @throws IllegalArgumentException If the stored value is not in the expected "<salt>:<hash>" format.
     */
    public static boolean verifyPassword(final String inputPassword, final String storedValue) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = storedValue.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Stored password must be in the format '<salt>:<hash>'");
        }
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] storedHash = Base64.getDecoder().decode(parts[1]);
        KeySpec spec = new PBEKeySpec(inputPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return MessageDigest.isEqual(hash, storedHash);
    }

}
