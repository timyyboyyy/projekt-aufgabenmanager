package de.iu.aufgabenmanager.repository;

import de.iu.aufgabenmanager.model.Project;
import de.iu.aufgabenmanager.model.ProjectStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByStatus(ProjectStatus status);

    /** Projekte, die dieser Benutzer leitet (US8: Sicht des Projektleiters). */
    List<Project> findByLeiterId(Long leiterId);

    /** Projekte, denen dieser Benutzer als Mitglied zugeordnet ist (US8: Sicht Mitarbeitende). */
    List<Project> findByMembersId(Long userId);
}
