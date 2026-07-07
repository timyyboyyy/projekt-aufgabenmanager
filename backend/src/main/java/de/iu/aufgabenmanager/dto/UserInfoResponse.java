package de.iu.aufgabenmanager.dto;

/** Info zum aktuell angemeldeten Benutzer (GET /api/auth/me). */
public record UserInfoResponse(
        String username,
        String role) {
}
