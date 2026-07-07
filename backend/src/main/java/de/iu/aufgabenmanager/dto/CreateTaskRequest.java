package de.iu.aufgabenmanager.dto;

import jakarta.validation.constraints.NotBlank;

/** Anlage einer Aufgabe in einem Projekt (US4). Bearbeiter optional. */
public record CreateTaskRequest(
        @NotBlank String titel,
        String beschreibung,
        Long bearbeiterId) {
}
