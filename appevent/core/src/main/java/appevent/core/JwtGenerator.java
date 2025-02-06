package appevent.core;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

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
    private static final byte[] SECRET_KEY = "0123456789ABCDEF0123456789ABCDEF".getBytes(); //denne må vi endre etterhvert


    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always, as this is a utility class
     */
    private JwtGenerator() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
    * Generates a basic JWT token with an expiration time.
    * The token is generated with a subject and userId claim.
     * It contains the issued at time (iat) and expiration time (exp).
    *
    * @param brukernavn the username of the user
    * @param userId the unique identifier of the user
    * @return a String representing the JWT token
    */
    public static String generateToken(final String brukernavn, final UUID userId) {
        long currentTime = System.currentTimeMillis();
        return Jwts.builder()
            .setSubject(brukernavn)
            .claim("userId", userId.toString())
            .setIssuedAt(new Date(currentTime))
            .setExpiration(new Date(currentTime + TOKEN_DURATION))
            .signWith(Keys.hmacShaKeyFor(SECRET_KEY), SignatureAlgorithm.HS256)
            .compact();
    }

    /**
     * Generates a JWT token for an administrator user.
     * The token is generated with a subject, userId claim, and isAdmin claim.
     * It contains the issued at time (iat) and expiration time (exp).
     *
     * @param brukernavn the username of the user
     * @param userId the unique identifier of the user
     * @return a String representing the JWT token
     */
    public static String generateAdminToken(final String brukernavn, final UUID userId) {
        long currentTime = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(brukernavn)
                .claim("userId", userId.toString())
                .claim("isAdmin", true)
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(currentTime + TOKEN_DURATION))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates a JWT token by parsing it.
     *
     * @param token the token to validate
     * @return true if the token is valid, false otherwise
     */
    public static boolean validateToken(final String token) {
        try {
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY))
                    .build();
            parser.parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public static String getUserIdFromToken(final String token) {
        try {
            String actualToken = token;
            if (token.startsWith("Bearer ")) {
                actualToken = token.substring(7);
            }
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY))
                    .build();
            return parser.parseClaimsJws(actualToken)
                    .getBody()
                    .get("userId", String.class);
        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke hente bruker-ID fra token", e);
        }
}

}
