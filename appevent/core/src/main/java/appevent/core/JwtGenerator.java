package appevent.core;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.util.UUID;

/**
 * Utility class for generating and validating JWT (JSON Web Token) tokens.
 * This class provides methods for generating, validating, and parsing JWT tokens,
 * with support for Bearer token format.
 *
 * @author YourName
 * @version 1.1
 * @since 1.0
 */
public final class JwtGenerator {

    /**
     * The duration in milliseconds for which the token will be valid (30 minutes).
     */
    private static final long TOKEN_DURATION = 30 * 60 * 1000;
    private static final byte[] SECRET_KEY = "0123456789ABCDEF0123456789ABCDEF".getBytes();
    private static final String BEARER_PREFIX = "Bearer ";

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
        boolean admin = (brukernavn == "admin");
        return Jwts.builder()
            .setSubject(brukernavn)
            .claim("userId", userId.toString())
            .claim("isAdmin", admin)
            .setIssuedAt(new Date(currentTime))
            .setExpiration(new Date(currentTime + TOKEN_DURATION))
            .signWith(Keys.hmacShaKeyFor(SECRET_KEY), SignatureAlgorithm.HS256)
            .compact();
    }

    /**
     * Strips the Bearer prefix from a token if present.
     *
     * @param token the token that might contain the Bearer prefix
     * @return the token without the Bearer prefix
     */
    private static String stripBearerPrefix(final String token) {
        if (token != null && token.startsWith(BEARER_PREFIX)) {
            return token.substring(BEARER_PREFIX.length());
        }
        return token;
    }

    /**
     * Validates a JWT token, supporting both Bearer and raw token formats.
     *
     * @param token the token to validate, can include Bearer prefix
     * @return true if the token is valid, false otherwise
     */
    public static boolean validateToken(final String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        try {
            String actualToken = stripBearerPrefix(token);
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY))
                    .build();
            parser.parseClaimsJws(actualToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validates an admin JWT token.
     *
     * @param token the token to validate
     * @return true if the token is a valid admin token, false otherwise
     */
    public static boolean validateAdminToken(final String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        try {
            String actualToken = stripBearerPrefix(token);
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY))
                    .build();
            Jws<Claims> claims = parser.parseClaimsJws(actualToken);
            return claims.getBody().get("isAdmin", Boolean.class);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extracts the user ID from a JWT token. Works for Admin tokens too.
     *
     * @param token the token to extract the user ID from
     * @return the user ID as a String
     */
    public static String getUserIdFromToken(final String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new RuntimeException("Token cannot be null or empty");
        }
        try {
            String actualToken = stripBearerPrefix(token);
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
