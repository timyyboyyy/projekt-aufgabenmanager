package de.iu.aufgabenmanager.service;

import de.iu.aufgabenmanager.config.JwtService;
import de.iu.aufgabenmanager.dto.LoginRequest;
import de.iu.aufgabenmanager.dto.LoginResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

/**
 * Login-Geschaeftslogik: prueft die Zugangsdaten ueber den {@link AuthenticationManager}
 * und stellt bei Erfolg ein JWT aus.
 */
@Service
public class AuthService {

    private static final String ROLE_PREFIX = "ROLE_";

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        String role = extractRole(authentication);
        String token = jwtService.generateToken(authentication.getName(), role);
        return new LoginResponse(token, authentication.getName(), role);
    }

    /** Nimmt die erste Authority und entfernt das {@code ROLE_}-Praefix. */
    public static String extractRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.startsWith(ROLE_PREFIX)
                        ? authority.substring(ROLE_PREFIX.length())
                        : authority)
                .orElse("");
    }
}
