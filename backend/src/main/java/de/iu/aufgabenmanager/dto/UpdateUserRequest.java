package de.iu.aufgabenmanager.dto;

/**
 * Bearbeitung eines Benutzers durch den Admin (US1). Alle Felder optional;
 * nur gesetzte (nicht-null) Felder werden uebernommen. Die Rolle wird ueber
 * einen eigenen Endpunkt geaendert ({@code PUT /api/users/{id}/role}).
 */
public record UpdateUserRequest(
        String username,
        String password,
        Boolean aktiv) {
}
