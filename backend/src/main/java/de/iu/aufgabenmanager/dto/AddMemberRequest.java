package de.iu.aufgabenmanager.dto;

import jakarta.validation.constraints.NotNull;

/** Zuordnung eines Mitarbeitenden zu einem Projekt (US3). */
public record AddMemberRequest(
        @NotNull Long userId) {
}
