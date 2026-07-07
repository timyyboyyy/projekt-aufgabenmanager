package de.iu.aufgabenmanager.dto;

import de.iu.aufgabenmanager.model.User;

/** Benutzerdaten fuer die Ausgabe (ohne Passwort-Hash). */
public record UserResponse(
        Long id,
        String username,
        String role,
        boolean aktiv) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole().getName().name(),
                user.isAktiv());
    }
}
