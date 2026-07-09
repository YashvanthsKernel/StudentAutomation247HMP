package com.studentautomation.security;

import com.studentautomation.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Service class for JWT token operations.
 *
 * Purpose:
 * This class generates and reads JWT access tokens.
 * The token will store the logged-in user's email and role.
 *
 * @author Yashvanth
 */
@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    /**
     * Generates JWT access token for logged-in user.
     *
     * Purpose:
     * This method creates a signed JWT token using user's email and role.
     *
     * @param user logged-in user entity
     * @return generated JWT access token
     */
    public String generateAccessToken(User user) {

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Extracts email from JWT token.
     *
     * Purpose:
     * This method reads the subject value from token.
     * In our project, subject means user's email.
     *
     * @param token JWT token
     * @return email stored inside token
     */
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Reads all claims from JWT token.
     *
     * Purpose:
     * Claims are data stored inside JWT like email, role, issued time, expiry time.
     *
     * @param token JWT token
     * @return Claims object containing token data
     */
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Creates signing key from secret.
     *
     * Purpose:
     * This key is used to sign and verify JWT tokens.
     *
     * @return SecretKey used for JWT signing
     */
    private SecretKey getSigningKey() {

        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    /**
     * Validates JWT token.
     *
     * Purpose:
     * This method checks whether the token belongs to the same user
     * and whether the token is not expired.
     *
     * @param token JWT access token
     * @param userDetails logged-in user details loaded from database
     * @return true if token is valid, otherwise false
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {

        String email = extractEmail(token);

        return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Checks whether JWT token is expired.
     *
     * Purpose:
     * If token expiry time is before current time, token is expired.
     *
     * @param token JWT access token
     * @return true if token is expired, otherwise false
     */
    private boolean isTokenExpired(String token) {

        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }
}