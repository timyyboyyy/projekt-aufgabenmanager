package de.iu.aufgabenmanager.dto;

import de.iu.aufgabenmanager.model.RoleName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Anlage eines Benutzerkontos durch den Admin (US1). */
public record CreateUserRequest(
        @NotBlank String username,
        @NotBlank String password,
        @NotNull RoleName role) {
}
