package de.iu.aufgabenmanager.dto;

import de.iu.aufgabenmanager.model.Project;
import de.iu.aufgabenmanager.model.ProjectStatus;
import java.util.List;

/** Projektdaten fuer die Ausgabe inkl. Leiter und Mitgliedern. */
public record ProjectResponse(
        Long id,
        String name,
        String beschreibung,
        ProjectStatus status,
        UserResponse leiter,
        List<UserResponse> members) {

    /** Muss innerhalb einer Transaktion aufgerufen werden (Mitglieder werden lazy geladen). */
    public static ProjectResponse from(Project project) {
        List<UserResponse> members = project.getMembers().stream()
                .map(UserResponse::from)
                .sorted((a, b) -> a.username().compareToIgnoreCase(b.username()))
                .toList();
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getBeschreibung(),
                project.getStatus(),
                UserResponse.from(project.getLeiter()),
                members);
    }
}
