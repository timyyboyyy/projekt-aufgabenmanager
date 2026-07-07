package de.iu.aufgabenmanager.dto;

/** Antwort auf einen erfolgreichen Login: JWT plus Basisinfos zum Benutzer. */
public record LoginResponse(
        String token,
        String username,
        String role) {
}
