package de.iu.aufgabenmanager.controller;

import de.iu.aufgabenmanager.dto.CreateTaskRequest;
import de.iu.aufgabenmanager.dto.TaskResponse;
import de.iu.aufgabenmanager.dto.UpdateTaskRequest;
import de.iu.aufgabenmanager.dto.UpdateTaskStatusRequest;
import de.iu.aufgabenmanager.service.TaskService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Aufgabenverwaltung (US4) und Statuswechsel (US5). Projektbezogene Endpunkte liegen
 * unter {@code /api/projects/{projectId}/tasks}, aufgabenbezogene unter {@code /api/tasks/{id}}.
 * Aufgaben duerfen Mitarbeitende und (grosszuegig) Projektleiter bearbeiten; die
 * Mitgliedschaftspruefung erfolgt im {@link TaskService}.
 */
@RestController
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/api/projects/{projectId}/tasks")
    public List<TaskResponse> listByProject(@PathVariable Long projectId,
                                            Authentication authentication) {
        return taskService.findByProject(projectId, authentication.getName());
    }

    @PostMapping("/api/projects/{projectId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('MITARBEITER', 'PROJEKTLEITER')")
    public TaskResponse create(@PathVariable Long projectId,
                               @Valid @RequestBody CreateTaskRequest request,
                               Authentication authentication) {
        return taskService.create(projectId, request, authentication.getName());
    }

    @PutMapping("/api/tasks/{id}")
    @PreAuthorize("hasAnyRole('MITARBEITER', 'PROJEKTLEITER')")
    public TaskResponse update(@PathVariable Long id,
                               @Valid @RequestBody UpdateTaskRequest request,
                               Authentication authentication) {
        return taskService.update(id, request, authentication.getName());
    }

    @PutMapping("/api/tasks/{id}/status")
    @PreAuthorize("hasAnyRole('MITARBEITER', 'PROJEKTLEITER')")
    public TaskResponse updateStatus(@PathVariable Long id,
                                     @Valid @RequestBody UpdateTaskStatusRequest request,
                                     Authentication authentication) {
        return taskService.updateStatus(id, request, authentication.getName());
    }
}
