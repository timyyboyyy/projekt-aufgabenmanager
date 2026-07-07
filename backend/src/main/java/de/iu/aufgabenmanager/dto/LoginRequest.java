package de.iu.aufgabenmanager.dto;

import jakarta.validation.constraints.NotBlank;

/** Anmeldedaten fuer POST /api/auth/login. */
public record LoginRequest(
        @NotBlank String username,
        @NotBlank String password) {
}
