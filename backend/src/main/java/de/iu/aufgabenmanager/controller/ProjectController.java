package de.iu.aufgabenmanager.controller;

import de.iu.aufgabenmanager.dto.AddMemberRequest;
import de.iu.aufgabenmanager.dto.CreateProjectRequest;
import de.iu.aufgabenmanager.dto.ProjectProgressResponse;
import de.iu.aufgabenmanager.dto.ProjectResponse;
import de.iu.aufgabenmanager.dto.UpdateProjectRequest;
import de.iu.aufgabenmanager.service.ProjectService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Projektverwaltung (US2, US3, US6, US8). Grobes Rollen-Gate hier, die feine
 * Eigentuemer-/Mitgliedschaftspruefung erfolgt in {@link ProjectService}.
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /** US8: nur berechtigte Projekte je Rolle. */
    @GetMapping
    public List<ProjectResponse> list(Authentication authentication) {
        return projectService.findVisible(authentication.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('PROJEKTLEITER')")
    public ProjectResponse create(@Valid @RequestBody CreateProjectRequest request,
                                  Authentication authentication) {
        return projectService.create(request, authentication.getName());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROJEKTLEITER')")
    public ProjectResponse update(@PathVariable Long id,
                                  @Valid @RequestBody UpdateProjectRequest request,
                                  Authentication authentication) {
        return projectService.update(id, request, authentication.getName());
    }

    @PutMapping("/{id}/archive")
    @PreAuthorize("hasRole('PROJEKTLEITER')")
    public ProjectResponse archive(@PathVariable Long id, Authentication authentication) {
        return projectService.archive(id, authentication.getName());
    }

    @PostMapping("/{id}/members")
    @PreAuthorize("hasRole('PROJEKTLEITER')")
    public ProjectResponse addMember(@PathVariable Long id,
                                     @Valid @RequestBody AddMemberRequest request,
                                     Authentication authentication) {
        return projectService.addMember(id, request.userId(), authentication.getName());
    }

    @DeleteMapping("/{id}/members/{userId}")
    @PreAuthorize("hasRole('PROJEKTLEITER')")
    public ProjectResponse removeMember(@PathVariable Long id,
                                        @PathVariable Long userId,
                                        Authentication authentication) {
        return projectService.removeMember(id, userId, authentication.getName());
    }

    /** US6: Fortschritt; Zugriff fuer Leiter oder Mitglied (Pruefung im Service). */
    @GetMapping("/{id}/progress")
    public ProjectProgressResponse progress(@PathVariable Long id, Authentication authentication) {
        return projectService.progress(id, authentication.getName());
    }
}
