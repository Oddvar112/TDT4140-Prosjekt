package appevent.core;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.UUID;

/**
 * Utility class for generating simple JWT (JSON Web Token) tokens.
 * This class provides a static method for generating basic JWT tokens
 * with a 30-minute expiration time.
 *
 * @author YourName
 * @version 1.0
 * @since 1.0
 */
public final class JwtGenerator {
    
    /**
     * The duration in milliseconds for which the token will be valid (30 minutes).
     */
    private static final long TOKEN_DURATION = 30 * 60 * 1000; 
    
    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always, as this is a utility class
     */
    private JwtGenerator() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * Generates a basic JWT token with a expiration time.
     * The token is generated without signature, claims, or subject.
     * It only contains the issued at time (iat) and expiration time (exp).
     * @return a String representing the JWT token
     */
    public static String generateToken(String brukernavn, UUID userId) {
        long currentTime = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(brukernavn)
                .claim("userId", userId.toString())
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(currentTime + TOKEN_DURATION))
                .compact();
    }

   public static boolean validateToken(String token) {
        try {
            JwtParser parser = Jwts.parserBuilder().build();
            parser.parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}