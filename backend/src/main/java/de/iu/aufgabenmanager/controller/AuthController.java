package de.iu.aufgabenmanager.controller;

import de.iu.aufgabenmanager.dto.LoginRequest;
import de.iu.aufgabenmanager.dto.LoginResponse;
import de.iu.aufgabenmanager.dto.UserInfoResponse;
import de.iu.aufgabenmanager.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentifizierungs-Endpunkte (US7): Login gibt ein JWT zurueck, Logout ist zustandslos
 * (der Client verwirft das Token). {@code /me} liefert Infos zum angemeldeten Benutzer.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Zustandslos: der Client entfernt das Token. Endpunkt existiert der Vollstaendigkeit halber.
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public UserInfoResponse me(Authentication authentication) {
        return new UserInfoResponse(
                authentication.getName(),
                AuthService.extractRole(authentication));
    }
}
