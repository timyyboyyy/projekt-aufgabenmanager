package de.iu.aufgabenmanager.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Liest ein Bearer-JWT aus dem Authorization-Header, prueft es und setzt bei Gueltigkeit
 * die Authentication in den SecurityContext. Ungueltige/fehlende Tokens fuehren nicht zu
 * einem Fehler hier – die Autorisierung uebernimmt anschliessend die Filter-Chain (401/403).
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            String token = header.substring(BEARER_PREFIX.length());
            if (jwtService.isValid(token)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                authenticate(request, token);
            }
        }
        filterChain.doFilter(request, response);
    }

    private void authenticate(HttpServletRequest request, String token) {
        String username = jwtService.extractUsername(token);
        if (username == null) {
            return;
        }
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            // Deaktivierte/gesperrte Konten nicht authentifizieren, auch wenn das Token noch
            // gueltig ist. Sonst behaelt ein deaktivierter Benutzer bis zum Token-Ablauf Zugriff.
            if (!userDetails.isEnabled() || !userDetails.isAccountNonLocked()) {
                return;
            }
            var authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (UsernameNotFoundException ex) {
            // Benutzer existiert nicht (mehr): unauthentifiziert weiterlaufen lassen.
            SecurityContextHolder.clearContext();
        }
    }
}
