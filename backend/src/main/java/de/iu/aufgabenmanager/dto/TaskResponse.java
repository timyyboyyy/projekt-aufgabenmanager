package de.iu.aufgabenmanager.dto;

import de.iu.aufgabenmanager.model.Task;
import de.iu.aufgabenmanager.model.TaskStatus;

/** Aufgabendaten fuer die Ausgabe. Bearbeiter ist optional. */
public record TaskResponse(
        Long id,
        String titel,
        String beschreibung,
        TaskStatus status,
        Long projectId,
        UserResponse bearbeiter) {

    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitel(),
                task.getBeschreibung(),
                task.getStatus(),
                task.getProject().getId(),
                task.getBearbeiter() == null ? null : UserResponse.from(task.getBearbeiter()));
    }
}
