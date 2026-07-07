package de.iu.aufgabenmanager.dto;

import jakarta.validation.constraints.NotBlank;

/** Bearbeitung eines Projekts durch den Projektleiter (US2). */
public record UpdateProjectRequest(
        @NotBlank String name,
        String beschreibung) {
}
