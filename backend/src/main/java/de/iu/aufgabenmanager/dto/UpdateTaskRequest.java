package de.iu.aufgabenmanager.dto;

import jakarta.validation.constraints.NotBlank;

/** Bearbeitung von Titel/Beschreibung/Bearbeiter einer Aufgabe (US4). */
public record UpdateTaskRequest(
        @NotBlank String titel,
        String beschreibung,
        Long bearbeiterId) {
}
