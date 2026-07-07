package de.iu.aufgabenmanager.dto;

import de.iu.aufgabenmanager.model.TaskStatus;
import jakarta.validation.constraints.NotNull;

/** Aenderung des Aufgabenstatus (US5). */
public record UpdateTaskStatusRequest(
        @NotNull TaskStatus status) {
}
