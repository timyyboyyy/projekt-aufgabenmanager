package de.iu.aufgabenmanager.service;

import de.iu.aufgabenmanager.dto.CreateTaskRequest;
import de.iu.aufgabenmanager.dto.TaskResponse;
import de.iu.aufgabenmanager.dto.UpdateTaskRequest;
import de.iu.aufgabenmanager.dto.UpdateTaskStatusRequest;
import de.iu.aufgabenmanager.exception.BadRequestException;
import de.iu.aufgabenmanager.exception.ForbiddenException;
import de.iu.aufgabenmanager.exception.NotFoundException;
import de.iu.aufgabenmanager.model.Project;
import de.iu.aufgabenmanager.model.Task;
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
 * Aufgabenverwaltung und Statuswechsel (US4, US5). Zugriff nur fuer Leiter oder Mitglied
 * des jeweiligen Projekts – serverseitig erzwungen.
 */
@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository,
                       ProjectRepository projectRepository,
                       UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    /** US4: Aufgaben eines Projekts; nur fuer Leiter/Mitglieder sichtbar. */
    @Transactional(readOnly = true)
    public List<TaskResponse> findByProject(Long projectId, String username) {
        Project project = requireProject(projectId);
        requireLeiterOrMember(project, username);
        return taskRepository.findByProjectId(projectId).stream()
                .sorted(Comparator.comparing(Task::getId))
                .map(TaskResponse::from)
                .toList();
    }

    /** US4: Aufgabe anlegen. */
    public TaskResponse create(Long projectId, CreateTaskRequest request, String username) {
        Project project = requireProject(projectId);
        requireLeiterOrMember(project, username);
        User bearbeiter = resolveBearbeiter(project, request.bearbeiterId());
        Task task = new Task(request.titel(), request.beschreibung(), project, bearbeiter);
        return TaskResponse.from(taskRepository.save(task));
    }

    /** US4: Titel/Beschreibung/Bearbeiter bearbeiten. */
    public TaskResponse update(Long taskId, UpdateTaskRequest request, String username) {
        Task task = requireTask(taskId);
        requireLeiterOrMember(task.getProject(), username);
        task.setTitel(request.titel());
        task.setBeschreibung(request.beschreibung());
        task.setBearbeiter(resolveBearbeiter(task.getProject(), request.bearbeiterId()));
        return TaskResponse.from(task);
    }

    /** US5: Status wechseln unter Beachtung der erlaubten Uebergaenge. */
    public TaskResponse updateStatus(Long taskId, UpdateTaskStatusRequest request, String username) {
        Task task = requireTask(taskId);
        requireLeiterOrMember(task.getProject(), username);

        TaskStatus current = task.getStatus();
        TaskStatus target = request.status();
        if (current == target) {
            return TaskResponse.from(task); // keine Aenderung
        }
        if (!current.canTransitionTo(target)) {
            throw new BadRequestException(
                    "Unzulaessiger Statuswechsel: " + current + " -> " + target);
        }
        task.setStatus(target);
        return TaskResponse.from(task);
    }

    // --- Hilfsmethoden ---

    private Task requireTask(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Aufgabe nicht gefunden: " + taskId));
    }

    private Project requireProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Projekt nicht gefunden: " + projectId));
    }

    private void requireLeiterOrMember(Project project, String username) {
        boolean leiter = project.getLeiter().getUsername().equals(username);
        boolean member = project.getMembers().stream()
                .anyMatch(m -> m.getUsername().equals(username));
        if (!leiter && !member) {
            throw new ForbiddenException("Kein Zugriff auf dieses Projekt.");
        }
    }

    /** Bearbeiter ist optional; wenn gesetzt, muss er Leiter oder Mitglied des Projekts sein. */
    private User resolveBearbeiter(Project project, Long bearbeiterId) {
        if (bearbeiterId == null) {
            return null;
        }
        User bearbeiter = userRepository.findById(bearbeiterId)
                .orElseThrow(() -> new NotFoundException("Benutzer nicht gefunden: " + bearbeiterId));
        boolean leiter = project.getLeiter().getId().equals(bearbeiterId);
        boolean member = project.getMembers().stream()
                .anyMatch(m -> m.getId().equals(bearbeiterId));
        if (!leiter && !member) {
            throw new BadRequestException(
                    "Bearbeiter muss Leiter oder Mitglied des Projekts sein.");
        }
        return bearbeiter;
    }
}
