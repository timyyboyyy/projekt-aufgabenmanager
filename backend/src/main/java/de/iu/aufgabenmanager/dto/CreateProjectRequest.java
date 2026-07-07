package de.iu.aufgabenmanager.dto;

import jakarta.validation.constraints.NotBlank;

/** Anlage eines Projekts durch den Projektleiter (US2). */
public record CreateProjectRequest(
        @NotBlank String name,
        String beschreibung) {
}
