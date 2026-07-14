package com.studentautomation.security;

import com.studentautomation.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Service class for JWT token operations.
 *
 * Purpose:
 * This class generates and validates JWT access tokens and refresh tokens.
 *
 * Access token:
 * Used for accessing protected APIs.
 *
 * Refresh token:
 * Stored in HttpOnly cookie and used only to generate a new access token.
 *
 * @author Yashvanth
 */
@Service
public class JwtService {

    private static final String TOKEN_TYPE_CLAIM = "tokenType";
    private static final String ACCESS_TOKEN_TYPE = "ACCESS";
    private static final String REFRESH_TOKEN_TYPE = "REFRESH";

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${app.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    /**
     * Generates JWT access token for logged-in user.
     *
     * Purpose:
     * Access token is used in Authorization header to access protected APIs.
     *
     * @param user logged-in user entity
     * @return generated JWT access token
     */
    public String generateAccessToken(User user) {

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .claim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Generates JWT refresh token for logged-in user.
     *
     * Purpose:
     * Refresh token is stored in HttpOnly cookie.
     * It is used only to generate a new access token when access token expires.
     *
     * @param user logged-in user entity
     * @return generated JWT refresh token
     */
    public String generateRefreshToken(User user) {

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .claim(TOKEN_TYPE_CLAIM, REFRESH_TOKEN_TYPE)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Extracts email from JWT token.
     *
     * Purpose:
     * In our project, JWT subject stores the user's email.
     *
     * @param token JWT token
     * @return email stored inside token
     */
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Validates access token.
     *
     * Purpose:
     * JWT filter should allow only ACCESS tokens for protected APIs.
     *
     * @param token JWT access token
     * @param userDetails user details loaded from database
     * @return true if token is valid access token
     */
    public boolean isAccessTokenValid(String token, UserDetails userDetails) {

        String email = extractEmail(token);

        return email.equals(userDetails.getUsername())
                && !isTokenExpired(token)
                && isAccessToken(token);
    }

    /**
     * Validates refresh token.
     *
     * Purpose:
     * Refresh API should accept only REFRESH tokens from cookie.
     *
     * @param token JWT refresh token
     * @return true if token is valid refresh token
     */
    public boolean isRefreshTokenValid(String token) {

        return !isTokenExpired(token) && isRefreshToken(token);
    }

    /**
     * Checks whether token is an access token.
     *
     * @param token JWT token
     * @return true if tokenType is ACCESS
     */
    public boolean isAccessToken(String token) {
        return ACCESS_TOKEN_TYPE.equals(
                extractAllClaims(token).get(TOKEN_TYPE_CLAIM, String.class)
        );
    }

    /**
     * Checks whether token is a refresh token.
     *
     * @param token JWT token
     * @return true if tokenType is REFRESH
     */
    public boolean isRefreshToken(String token) {
        return REFRESH_TOKEN_TYPE.equals(
                extractAllClaims(token).get(TOKEN_TYPE_CLAIM, String.class)
        );
    }

    /**
     * Reads all claims from JWT token.
     *
     * Purpose:
     * Claims are data stored inside JWT like email, role, tokenType, issued time, expiry time.
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
     * Checks whether JWT token is expired.
     *
     * @param token JWT token
     * @return true if token is expired
     */
    private boolean isTokenExpired(String token) {

        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
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
}