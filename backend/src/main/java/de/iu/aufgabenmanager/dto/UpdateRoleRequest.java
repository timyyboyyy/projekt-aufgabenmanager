package de.iu.aufgabenmanager.dto;

import de.iu.aufgabenmanager.model.RoleName;
import jakarta.validation.constraints.NotNull;

/** Zuweisung einer Rolle zu einem Benutzer (US1). */
public record UpdateRoleRequest(
        @NotNull RoleName role) {
}
