package de.iu.aufgabenmanager.service;

import de.iu.aufgabenmanager.dto.CreateProjectRequest;
import de.iu.aufgabenmanager.dto.ProjectProgressResponse;
import de.iu.aufgabenmanager.dto.ProjectResponse;
import de.iu.aufgabenmanager.dto.UpdateProjectRequest;
import de.iu.aufgabenmanager.exception.ForbiddenException;
import de.iu.aufgabenmanager.exception.NotFoundException;
import de.iu.aufgabenmanager.model.Project;
import de.iu.aufgabenmanager.model.ProjectStatus;
import de.iu.aufgabenmanager.model.RoleName;
import de.iu.aufgabenmanager.model.TaskStatus;
import de.iu.aufgabenmanager.model.User;
import de.iu.aufgabenmanager.repository.ProjectRepository;
import de.iu.aufgabenmanager.repository.TaskRepository;
import de.iu.aufgabenmanager.repository.UserRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Projektverwaltung inkl. Mitgliederzuordnung, Fortschritt und rollenbasierter Sicht
 * (US2, US3, US6, US8). Autorisierung (Leiter/Mitglied) wird hier serverseitig erzwungen.
 */
@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public ProjectService(ProjectRepository projectRepository,
                          UserRepository userRepository,
                          TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    /** Nur berechtigte, aktive Projekte je Rolle (US8). Admin sieht alle. */
    @Transactional(readOnly = true)
    public List<ProjectResponse> findVisible(String username) {
        User current = requireUser(username);
        RoleName role = current.getRole().getName();

        List<Project> projects = switch (role) {
            case ADMIN -> projectRepository.findByStatus(ProjectStatus.AKTIV);
            case PROJEKTLEITER -> projectRepository.findByLeiterId(current.getId());
            case MITARBEITER -> projectRepository.findByMembersId(current.getId());
        };

        return projects.stream()
                .filter(p -> p.getStatus() == ProjectStatus.AKTIV)
                .sorted(Comparator.comparing(Project::getName, String.CASE_INSENSITIVE_ORDER))
                .map(ProjectResponse::from)
                .toList();
    }

    /** US2: Anlegen; der anlegende Projektleiter wird Leiter des Projekts. */
    public ProjectResponse create(CreateProjectRequest request, String username) {
        User leiter = requireUser(username);
        Project project = new Project(request.name(), request.beschreibung(), leiter);
        return ProjectResponse.from(projectRepository.save(project));
    }

    /** US2: Bearbeiten; nur der Leiter des Projekts. */
    public ProjectResponse update(Long projectId, UpdateProjectRequest request, String username) {
        Project project = requireProject(projectId);
        requireLeiter(project, username);
        project.setName(request.name());
        project.setBeschreibung(request.beschreibung());
        return ProjectResponse.from(project);
    }

    /** US2: Archivieren; nur der Leiter. Archivierte Projekte bleiben erhalten, sind aber ausgeblendet. */
    public ProjectResponse archive(Long projectId, String username) {
        Project project = requireProject(projectId);
        requireLeiter(project, username);
        project.setStatus(ProjectStatus.ARCHIVIERT);
        return ProjectResponse.from(project);
    }

    /** US3: Mitglied hinzufuegen; nur der Leiter. */
    public ProjectResponse addMember(Long projectId, Long userId, String username) {
        Project project = requireProject(projectId);
        requireLeiter(project, username);
        User member = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Benutzer nicht gefunden: " + userId));
        project.getMembers().add(member);
        return ProjectResponse.from(project);
    }

    /** US3: Mitglied entfernen; nur der Leiter. */
    public ProjectResponse removeMember(Long projectId, Long userId, String username) {
        Project project = requireProject(projectId);
        requireLeiter(project, username);
        project.getMembers().removeIf(member -> member.getId().equals(userId));
        return ProjectResponse.from(project);
    }

    /** US6: Fortschritt (% erledigt); sichtbar fuer Leiter oder Mitglied des Projekts. */
    @Transactional(readOnly = true)
    public ProjectProgressResponse progress(Long projectId, String username) {
        Project project = requireProject(projectId);
        requireLeiterOrMember(project, username);
        long total = taskRepository.countByProjectId(projectId);
        long done = taskRepository.countByProjectIdAndStatus(projectId, TaskStatus.ERLEDIGT);
        return ProjectProgressResponse.of(projectId, total, done);
    }

    // --- Hilfsmethoden ---

    private User requireUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Benutzer nicht gefunden: " + username));
    }

    private Project requireProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Projekt nicht gefunden: " + projectId));
    }

    private void requireLeiter(Project project, String username) {
        if (!project.getLeiter().getUsername().equals(username)) {
            throw new ForbiddenException("Nur der Projektleiter darf dieses Projekt bearbeiten.");
        }
    }

    private void requireLeiterOrMember(Project project, String username) {
        boolean leiter = project.getLeiter().getUsername().equals(username);
        boolean member = project.getMembers().stream()
                .anyMatch(m -> m.getUsername().equals(username));
        if (!leiter && !member) {
            throw new ForbiddenException("Kein Zugriff auf dieses Projekt.");
        }
    }
}
