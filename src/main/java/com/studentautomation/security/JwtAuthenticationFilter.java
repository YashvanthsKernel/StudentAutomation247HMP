package com.studentautomation.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT authentication filter.
 *
 * Purpose:
 * This filter runs once for every request.
 * It checks whether the request contains a JWT access token
 * in the Authorization header.
 *
 * If the access token is valid, it tells Spring Security
 * that the user is authenticated.
 *
 * Important:
 * This filter accepts only ACCESS tokens.
 * REFRESH tokens should not be accepted here.
 * Refresh tokens will be handled separately through cookie
 * in /api/auth/refresh-token API.
 *
 * @author Yashvanth
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   CustomUserDetailsService customUserDetailsService) {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }

    /**
     * Filters every incoming request.
     *
     * Purpose:
     * This method reads JWT access token from Authorization header,
     * validates it, and sets authentication in Spring Security context.
     *
     * Flow:
     * 1. Read Authorization header.
     * 2. Check whether it starts with Bearer.
     * 3. Extract token.
     * 4. Extract email from token.
     * 5. Load user from database.
     * 6. Validate token as ACCESS token.
     * 7. Set authentication in SecurityContext.
     *
     * @param request incoming HTTP request
     * @param response outgoing HTTP response
     * @param filterChain security filter chain
     * @throws ServletException if servlet filtering fails
     * @throws IOException if request/response processing fails
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String email = jwtService.extractEmail(token);

            if (email != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails =
                        customUserDetailsService.loadUserByUsername(email);

                if (jwtService.isAccessTokenValid(token, userDetails)) {

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }

        } catch (JwtException | IllegalArgumentException exception) {

            /*
             * Purpose:
             * If token is expired, malformed, invalid, or not readable,
             * clear the security context and continue the filter chain.
             *
             * Then Spring Security will treat the request as unauthenticated
             * and return 401 through SecurityConfig authenticationEntryPoint.
             */
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}